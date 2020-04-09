package com.yss.fsip.web.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yss.fsip.context.FSIPContext;
import com.yss.fsip.context.FSIPContextFactory;
import com.yss.fsip.util.IDGeneratorFactory;
import com.yss.fsip.web.utils.FSIPWebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 统一返回结构
 * 
 * @author LSP
 *
 */
@ControllerAdvice
public class RequestAdvisor extends RequestBodyAdviceAdapter {

	private Logger logger = LoggerFactory.getLogger(RequestAdvisor.class);

	@Autowired
	private FSIPLogProperties fsipLogProperties;

	private final ObjectMapper mapper = new ObjectMapper();


	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		// 只处理@RequestBody注解了的参数请求
		// fsipLong request开启
		return methodParameter.getParameterAnnotation(RequestBody.class) != null
				&& fsipLogProperties.isRequest();
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
								Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

		try {
			ObjectNode rootNode = mapper.createObjectNode();

			String requestParams;

			// 参数对象转JSON字符串
			if (StringHttpMessageConverter.class.isAssignableFrom(converterType)) {
				requestParams = body.toString();
			} else {
				requestParams = JSON.toJSONString(body, SerializerFeature.UseSingleQuotes);
			}

			/**
			 * 请求参数大于设置fsip.log.requestMax值，不打印日志
			 */
			if(requestParams.length() > fsipLogProperties.getRequestMax()){
				return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
			}


			Method method = parameter.getMethod();

			Class decarClass = parameter.getDeclaringClass();

			RequestMapping requestMapping = (RequestMapping) decarClass.getAnnotation(RequestMapping.class);
			String requestPath = getRequestPath(requestMapping);
			String requestMethod = "GET";

			Annotation[] annotations = method.getDeclaredAnnotations();
			if(annotations != null && annotations.length > 0){
				for(Annotation annotation : annotations){
					if(annotation.annotationType().isAssignableFrom(RequestMapping.class)){
						requestPath += getRequestPath((RequestMapping) annotation);
						requestMethod = getRequestMethod((RequestMapping)annotation);
						break;
					}
				}
			}

			//todo 设置序号值
			FSIPContext context = FSIPContextFactory.getContext();
			String serialNo = IDGeneratorFactory.getIDGenerator().nextId() + "";

			context.setAttribute(FSIPWebConstants.CONTEXT_KEY_SERIALNO, serialNo);
			context.setAttribute(FSIPWebConstants.CONTEXT_KEY_STARTTIME, System.currentTimeMillis() + "");

			rootNode.put(FSIPWebConstants.CONTEXT_KEY_SERIALNO, context.getAttribute(FSIPWebConstants.CONTEXT_KEY_SERIALNO));
			rootNode.put("uri", requestPath);
			rootNode.put("requestMethod", requestMethod);
			rootNode.put("className", parameter.getContainingClass().getName());
			rootNode.put("method", method.getName());
			rootNode.put("request", requestParams);

			logger.info(rootNode.toString());

		}catch (Exception e){
			logger.error(e.getMessage());
		}

		return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);

	}

	/**
	 * 获取类请求地址
	 * @param requestMapping
	 * @return
	 */
	private String getRequestPath(RequestMapping requestMapping){

		String path = "";
		if(requestMapping != null){
			String[] values = requestMapping.value();
			if(values != null && values.length > 0){
				path = values[0];
			}
		}
		return path;
	}

	/**
	 * 获取类请求方法名称
	 * @param requestMapping
	 * @return
	 */
	private String getRequestMethod(RequestMapping requestMapping){

		String methodName = "";
		if(requestMapping != null){
			RequestMethod[] methods = requestMapping.method();
			if(methods != null && methods.length > 0){
				methodName = methods[0].name();
			}
		}
		return methodName;
	}


}