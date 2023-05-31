

package com.easycode8.easylog.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Enumeration;

/**
 * 页面鉴权
 */
public class SecurityBasicAuthFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityBasicAuthFilter.class);
    private static final String AUTH_SESSION = "DynamicDataSourceBasicAuthSession";
    /***
     * 是否开启basic验证,默认不开启
     */
    private boolean enableBasicAuth = false;

    private String userName;

    private String password;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration<String> enumeration = filterConfig.getInitParameterNames();
        //SpringMVC环境中,由此init方法初始化此Filter,SpringBoot环境中则不同
        if (enumeration.hasMoreElements()) {
            setEnableBasicAuth(Boolean.valueOf(filterConfig.getInitParameter("enableBasicAuth")));
            setUserName(filterConfig.getInitParameter("userName"));
            setPassword(filterConfig.getInitParameter("password"));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        //针对dynamic-datasource资源请求过滤
        if (enableBasicAuth) {
            if (servletRequest.getRequestURI().contains("easy-log-ui.html")) {
                //判断Session中是否存在
                Object swaggerSessionValue = servletRequest.getSession().getAttribute(AUTH_SESSION);
                if (swaggerSessionValue != null) {
                    chain.doFilter(request, response);
                } else {
                    //匹配到,判断auth
                    //获取请求头Authorization
                    String auth = servletRequest.getHeader("Authorization");
                    if (auth == null || "".equals(auth)) {
                        writeForbiddenCode(httpServletResponse);
                        return;
                    }
                    String userAndPass = decodeBase64(auth.substring(6));
                    String[] upArr = userAndPass.split(":");
                    if (upArr.length != 2) {
                        writeForbiddenCode(httpServletResponse);
                    } else {
                        String iptUser = upArr[0];
                        String iptPass = upArr[1];
                        //匹配服务端用户名及密码
                        if (iptUser.equals(userName) && iptPass.equals(password)) {
                            servletRequest.getSession().setAttribute(AUTH_SESSION, userName);
                            chain.doFilter(request, response);
                        } else {
                            writeForbiddenCode(httpServletResponse);
                            return;
                        }
                    }
                }
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }

    private void writeForbiddenCode(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setStatus(401);
        httpServletResponse.setHeader("WWW-Authenticate", "Basic realm=\"input Dynamic DataSource Basic userName & password \"");
        httpServletResponse.getWriter().write("You do not have permission to access this resource");
    }

    public SecurityBasicAuthFilter(boolean enableBasicAuth, String userName, String password) {
        this.enableBasicAuth = enableBasicAuth;
        this.userName = userName;
        this.password = password;
    }

    public SecurityBasicAuthFilter(boolean enableBasicAuth) {
        this.enableBasicAuth = enableBasicAuth;
    }

    public SecurityBasicAuthFilter() {
    }

    public boolean isEnableBasicAuth() {
        return enableBasicAuth;
    }

    public void setEnableBasicAuth(boolean enableBasicAuth) {
        this.enableBasicAuth = enableBasicAuth;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    protected String decodeBase64(String source) {
        String decodeStr = null;
        if (source != null) {
            try {

                byte[] bytes = Base64.getDecoder().decode(source);
                decodeStr = new String(bytes);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return decodeStr;
    }
}
