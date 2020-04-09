package com.yss.fsip.web.exception;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.yss.fsip.constants.FSIPConstants;
import com.yss.fsip.constants.FSIPErrorCode;
import com.yss.fsip.exception.FSIPRuntimeException;
import com.yss.fsip.generic.Result;
import com.yss.fsip.generic.ResultFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.UndeclaredThrowableException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Set;

/**
 * 基础异常处理
 * 
 * @author LSP
 *
 */
@ControllerAdvice
public class FSIPExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(FSIPExceptionHandler.class);
	
	/**
	 * 返回json或者错误页面
	 * @param request
	 * @param response
	 * @param handlerMethod
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = Throwable.class)
	public Object handlerRuntimeException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Throwable ex) {
		if(isReturnJson(handlerMethod)) {
			Result result;

			logger.error(getDetailStackTrace(ex));
//			if(logger.isDebugEnabled()){
//				ex.printStackTrace();
//			}
			if (ex instanceof FSIPRuntimeException) {
				FSIPRuntimeException fException = (FSIPRuntimeException) ex;
				result = ResultFactory.error(fException.getCode(), fException.getMsg());
			} else if (ex instanceof MethodArgumentNotValidException) {
				MethodArgumentNotValidException me = (MethodArgumentNotValidException) ex;
				result = ResultFactory.error(FSIPErrorCode.PARAM_ERR.getErrorCode(), getParamErrorMsg(me));
			} else if(ex instanceof IllegalArgumentException) {
				String codeMsg =  ex.getMessage();
				if(codeMsg.contains(FSIPConstants.ASSERT_SEPARATOR)){
					String [] strs = codeMsg.split(FSIPConstants.ASSERT_SEPARATOR);
					result = ResultFactory.error(strs[0], strs[1]);
				}else{
					result = ResultFactory.error(FSIPErrorCode.FAIL_ERR.getErrorCode(), FSIPErrorCode.FAIL_ERR.getErrorDesc());
					ex.printStackTrace();
				}
			}else {
				result = ResultFactory.error(FSIPErrorCode.FAIL_ERR.getErrorCode(), FSIPErrorCode.FAIL_ERR.getErrorDesc());
				ex.printStackTrace();
			}
			ex.printStackTrace();

			PrintWriter out = null;
	            try {
					response.setContentType("application/json;charset=utf-8");
					out = response.getWriter();
	                out.print(JSON.toJSONString(result));
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }finally {
		            out.flush();  
				}
			return null;
		}else {
			ModelAndView modelAndView = new ModelAndView();
			//这里需要在templates文件夹下新建一个/error/500.html文件用作错误页面
            modelAndView.setViewName("/error/500"); 
            modelAndView.addObject("errorMsg",ex.getMessage());
            return modelAndView;
		}
	}
	
	 /**
     * 判断是否返回json
     * spring 返回含有 ResponseBody 或者 RestController注解
     * @param handlerMethod HandlerMethod
     * @return 是否返回json
     */
    private boolean isReturnJson(HandlerMethod handlerMethod) {

    	ResponseBody responseBody = handlerMethod.getMethodAnnotation(ResponseBody.class);
        if (null != responseBody) {
            return true;
        }
        // 获取类上面的Annotation，可能包含组合注解，故采用spring的工具类
        Class<?> beanType = handlerMethod.getBeanType();
        responseBody = AnnotationUtils.getAnnotation(beanType, ResponseBody.class);
        if (null != responseBody) {
            return true;
        }
        return false;
    }
	
	/**
	 * @param cause
	 * @return
	 */
	public String getDetailStackTrace(Throwable cause) {

		StringBuffer buf = new StringBuffer();

		while (cause != null) {
			StackTraceElement[] sts = cause.getStackTrace();
			if (sts != null && sts.length > 0) {
				StackTraceElement ele = sts[0];
				buf.append(ele.toString()).append("\n");
			}
			cause = getNextCause(cause);
		}

		return buf.toString();
	}

	/**
	 * 获取下一层异常
	 * 
	 * @param cause
	 * @return 当前异常的底层异常对象
	 */
	private Throwable getNextCause(Throwable cause) {

		Throwable realCause = getRealException(cause);

		Throwable nextCause = realCause.getCause() == null ? null : realCause.getCause();

		return nextCause;
	}

	private Throwable getRealException(Throwable cause) {

		Throwable ex = null;
		if (cause instanceof FSIPRuntimeException) {
			ex = cause;
			return ex;
		} else if (cause instanceof NestedServletException && (cause.getCause() != null)) {
			cause = cause.getCause();
		}

		ex = getUnKnowCause(cause);

		if (ex == null) {
			return cause;
		} else if (ex instanceof UndeclaredThrowableException) {
			ex = ex.getCause();
		} else if (ex instanceof InvocationTargetException) {
			ex = ((InvocationTargetException) ex).getTargetException();
		}

		return ex;
	}

	private Throwable getUnKnowCause(Throwable cause) {

		Throwable ex = cause;

		if (ex == null)
			return null;
		if (ex instanceof UndeclaredThrowableException || ex instanceof InvocationTargetException) {
			return ex;
		} else {
			ex = getUnKnowCause(ex.getCause());
		}
		return ex;
	}

	/**
	 * 获取参数错误异常信息
	 *
	 * @param ex
	 * @return
	 */
	private String getParamErrorMsg(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();
		Set<String> messages = Sets.newHashSet();
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			messages.add(fieldError.getDefaultMessage());
		}
		String errorMsg = StringUtils.join(messages, ", ");
		return MessageFormat.format(FSIPErrorCode.PARAM_ERR.getErrorDesc(), errorMsg);
	}

}