package com.yss.fsip.web.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 获取console cookie
 * 
 * @author LSP
 * @version 1.0, 2019年6月28日
 * @since 1.0, 2019年6月28日
 */
public class CookieUtils {

	public static String SESSION_COOKIE_ID = "SESSIONID";


	/**
	 * put cookie
	 * @param response
	 * @param cookieKey
	 * @param value
	 */
	public static void putCookie(HttpServletResponse response, String cookieKey, String value) {
		Cookie cookie = new Cookie(cookieKey, value);
		response.addCookie(cookie);
	}

	/**
	 * get cookie
	 * @param request
	 * @return
	 */
	public static String getSessionIdCookie(HttpServletRequest request) {
		return getCookie(request, SESSION_COOKIE_ID);
	}

	
	
	/**
	 * 获取 cookie 
	 * 
	 * @param request
	 * @return
	 */
	public static String getCookie(HttpServletRequest request, String cookieKey) {
		
			Cookie[] cookies = request.getCookies();
			if(cookies != null) {
				for (Cookie cookie : cookies) {
			           if(cookie.getName().equals(cookieKey)) {
			        	   return cookie.getValue();
			           }
			        }
			}
			
			return "";
	}

}
