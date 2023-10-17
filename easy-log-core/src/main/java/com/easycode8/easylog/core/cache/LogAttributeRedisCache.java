package com.easycode8.easylog.core.cache;

import com.alibaba.fastjson.JSON;
import com.easycode8.easylog.core.annotation.EasyLogProperties;
import com.easycode8.easylog.core.aop.interceptor.DefaultLogAttribute;
import com.easycode8.easylog.core.aop.interceptor.LogAttribute;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LogAttributeRedisCache implements LogAttributeCache, MessageListener, ApplicationRunner {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogAttributeRedisCache.class);

    private ConcurrentHashMap<String, DefaultLogAttribute> cacheMap = new ConcurrentHashMap<>();

    private final RedisTemplate redisTemplate;
    private final EasyLogProperties easyLogProperties;

    private Boolean inited = false;
    private String hashKey;

    public LogAttributeRedisCache(RedisTemplate redisTemplate, EasyLogProperties easyLogProperties) {
        this.redisTemplate = redisTemplate;
        this.easyLogProperties = easyLogProperties;

        this.hashKey = easyLogProperties.getCache().getKeyPrefix();
    }

    @Override
    public boolean containsKey(String key) {
        return cacheMap.containsKey(key);
    }

    @Override
    public void put(String key, LogAttribute attribute) {
        if (inited) {
            // 1.配置同步更新redis中配置
            this.pushToHash(key, attribute);

            // 2.通过redis发送事件,通知其他节点更新配置
            redisTemplate.convertAndSend("easy-log.update", (DefaultLogAttribute) attribute);
        } else {
            cacheMap.put(key, (DefaultLogAttribute) attribute);
        }
    }

    @Override
    public LogAttribute get(String key) {
        return cacheMap.get(key);
    }

    @Override
    public Map<String, LogAttribute> getAll() {
        Map<String, LogAttribute> resultMap = this.getAllFromHash().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue()
                ));
        return resultMap;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.inited = true;
        this.initConfig();
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        DefaultLogAttribute defaultLogAttribute = JSON.parseObject(body, DefaultLogAttribute.class);
        LOGGER.info("[easy-log] cache receive redis message. channel[{}] content:{}", channel, body);
        cacheMap.put(defaultLogAttribute.title(), defaultLogAttribute);
    }

    private void initConfig() {


        Map<String, DefaultLogAttribute> redisCacheMap = this.getAllFromHash();

        if (redisCacheMap == null || redisCacheMap.isEmpty()) {

            LOGGER.info("[easy-log]通过redis初始化日志属性--开始 cache:{}", this.cacheMap);
            this.pushAllToHash(this.cacheMap);
            LOGGER.info("[easy-log]通过redis初始化日志属性---成功");
        } else if (easyLogProperties.getCache().getDropFirst()) {
            LOGGER.warn("[easy-log]使用本地配置强制初始化集群配置--开始");
            this.pushAllToHash(this.cacheMap);
            LOGGER.warn("[easy-log]使用本地配置强制初始化集群配置--成功");
        } else {
            LOGGER.info("[easy-log]通过redis刷新数据源配置--开始");
            this.reloadLocalConfig();
            LOGGER.info("[easy-log]通过redis刷新数据源配置--成功");
        }
    }

//    private void pushConfig(List<LogAttribute> attributes) {
//        redisTemplate.opsForValue().set(easyLogProperties.getCache().getKeyPrefix()  + applicationName, JSON.toJSONString(attributes));
//        LOGGER.info("[easy-log] push data to redis success!");
//    }

    protected void reloadLocalConfig() {

        Map<String, DefaultLogAttribute> redisCacheMap = this.getAllFromHash();

        // 远程多出key配置 redis要删除 说明本地代码已经移除了对应的日志记录点/ 也有可能某个研发提交了代码,其他人没有更新
        // 这种情况避免误判,只删除redis缓存,日志记录实际上还是使用内存来判断
        List<String> waitDeleteSet = (List<String>) CollectionUtils.subtract(redisCacheMap.keySet(), new HashSet(cacheMap.keySet()));
        for (String key : waitDeleteSet) {
            LOGGER.info("[easy-log] redis remove cache key:{}", key);
            this.deleteFromHash(key);
            redisCacheMap.remove(key);
        }
        // 本地和远程都有的部分,使用远程来覆盖
        this.cacheMap.putAll(redisCacheMap);

        // 本地多出key配置 补充到 redis中 说明是新补充的日志记录点，或者其他人节点放弃记录了为来得及更新。
        List<String> waitAddSet = (List<String>) CollectionUtils.subtract(new HashSet(cacheMap.keySet()), redisCacheMap.keySet());
        for (String key : waitAddSet) {
            LOGGER.info("[easy-log] redis push cache key:{}", key);
            this.pushToHash(key, cacheMap.get(key));
        }
    }


    public void pushToHash(String key, Object value) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(hashKey, key, value);
    }

    public void pushAllToHash(Map<String, DefaultLogAttribute> map) {
        HashOperations<String, String, DefaultLogAttribute> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(hashKey, map);
    }

    public Object getFromHash(String key) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(hashKey, key);
    }

    public Map<String, DefaultLogAttribute> getAllFromHash() {
        HashOperations<String, String, DefaultLogAttribute> hashOperations = redisTemplate.opsForHash();
        return hashOperations.entries(hashKey);
    }

    public void deleteFromHash(String key) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(hashKey, key);
    }
}
