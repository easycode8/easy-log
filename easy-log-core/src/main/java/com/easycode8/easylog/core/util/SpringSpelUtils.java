package com.easycode8.easylog.core.util;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;


public class SpringSpelUtils {

    /**解析spel表达式*/
    private static ExpressionParser parser = new SpelExpressionParser();
    /**将方法参数纳入Spring管理*/
    private static LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();


    /**
     * 解析spel表达式
     * @param method 方法
     * @param args 获取参数对象数组
     * @param template spel表达式
     * @return
     */
    public static String parse(Method method, Object[] args, String template) {
        if (StringUtils.isEmpty(template)) {
            return "";
        }
        //获取方法参数名
        String[] params = discoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < params.length; len++) {
            context.setVariable(params[len], args[len]);
        }
        Expression expression = parser.parseExpression(template);
        String spelDescription = expression.getValue(context, String.class);
        return spelDescription;
    }


    /**
     * 使用SpEL表达式从session中提取属性信息
     *
     * @param expression SpEL表达式
     * @return 属性值
     */
    public static Object getSessionAttribute(String attributeName, String expression) {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        // 解析表达式并获取属性值
//        return parser.parseExpression(expression).getValue(session.getAttribute("account"));
        Object attributeValue = session.getAttribute(attributeName);
        if (attributeValue == null) {
            return null;
        }
        return parser.parseExpression(expression).getValue(attributeValue);
    }

}
