package com.yss.fsip.web.context;

import com.yss.fsip.context.FSIPContext;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Description: TODO
 * @Author gumpLiu
 * @Date 2020-02-25
 * @Version V1.0
 **/
public class FSIPWebContextFactoryImpi extends FSIPWebContextFactory {

    @Override
    public void setContext(ServletRequest request, FSIPContext context) {
        setUserParams(request, context);
        context.setAttribute("fullContextPath", getFullContextPath((HttpServletRequest) request));
        context.setLoggerTrackId(null);
    }

    /**
     * 维护用户相关信息
     *
     * @param request
     * @param context
     */
    private void setUserParams(ServletRequest request, FSIPContext context) {

//        try {
//            if (request != null) {
//                String cookieId = CookieUtil.getSessionIdCookie((HttpServletRequest)request);
//                log.info("FSIPContextFactory.setUserParams sessionId={},threadId={}", cookieId,Thread.currentThread().getId());
//                if(!StringUtil.isEmpty(cookieId)) {
//                    Claims claims = JWTTokenUtil.parseJWT(JWTSessionUtil.getJwt(cookieId), PortalConstants.SECRET_KEY);
//                    if(!StringUtil.isEmpty(claims.get("userId"))) {
//                        context.setUserId(claims.get("userId").toString());
//                    }
//                    for(Map.Entry<String, Object> entry : claims.entrySet()) {
//                        context.setAttribute(entry.getKey(), entry.getValue().toString());
//                    }
//                }else{
        context.setUserId("12345678901234567890");
        context.setUserCode("admin");
        context.setUserName("管理员");
//                }

//            } else {
//                log.debug("ServletRequest is null,不能填充上下文对象");
//            }
//        } catch (Exception e) {
//            log.warn("FSIPContext setContext :" + e.getMessage(), e);
//        }
    }

    /**
     * 获取用户IP.
     * @param request
     * @return String
     */
    private String getIp(ServletRequest request) {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String ip = httpRequest.getParameter("clientAddress");
        if (ip == null) {
            ip = httpRequest.getHeader("clientAddress");
        }
        if (ip == null) {
            ip = httpRequest.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = httpRequest.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = httpRequest.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        }
        return ip;
    }

    /**
     * 将当前的BasePath保存到session中
     * @param httpRequest
     */
    private String getFullContextPath(HttpServletRequest httpRequest) {

        HttpSession session = httpRequest.getSession(false);
        String basePath = httpRequest.getScheme() + "://" + httpRequest.getServerName() + ":"
                + httpRequest.getServerPort() + httpRequest.getContextPath();
        if (session != null) {

            session.setAttribute("basePath", basePath);
        }
        return basePath;
    }
}
