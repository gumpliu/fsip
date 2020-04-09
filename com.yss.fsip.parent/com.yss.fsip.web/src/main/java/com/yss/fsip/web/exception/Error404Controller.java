package com.yss.fsip.web.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理404错误
 * 
 * @author LSP
 *
 */
@Controller
public class Error404Controller implements ErrorController {
	
	private static final String ERROR_PATH = "/error";

    @RequestMapping(ERROR_PATH)
    public String handleError(HttpServletRequest request){
        //获取statusCode:401,404,500
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if(statusCode == 404){
            //对应的是/error/404.html、/error/404.jsp等，文件位于/templates下面
            return ERROR_PATH + "/404";
        }else if(statusCode == 403){
            return ERROR_PATH + "/403";
        }else{
            return ERROR_PATH + "/500";
        }
    }

    public String getErrorPath() {
        return ERROR_PATH;
    }
}