package com.yss.fsip.web.config;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yss.fsip.context.FSIPContext;
import com.yss.fsip.context.FSIPContextFactory;
import com.yss.fsip.generic.PageInfo;
import com.yss.fsip.generic.Result;
import com.yss.fsip.generic.ResultFactory;
import com.yss.fsip.web.utils.FSIPWebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一返回结构
 * 
 * @author LSP
 *
 */
@ControllerAdvice
public class ResponseAdvisor implements ResponseBodyAdvice<Object> {

	private FSIPLogProperties fsipLogProperties;

	public ResponseAdvisor(FSIPLogProperties fsipLogProperties){
		this.fsipLogProperties = fsipLogProperties;
	}

	private Logger logger = LoggerFactory.getLogger(RequestAdvisor.class);

	private final String BASE_PACKAGE = "com.yss";


	private final ObjectMapper mapper = new ObjectMapper();


	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		//只处理已com.yss开头的包
		return returnType.getContainingClass().getName().startsWith(BASE_PACKAGE);
	}

	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {

		Result result;

		if (!(body instanceof Result)) {
			if( body!=null && Page.class.isAssignableFrom(body.getClass())){
				result = ResultFactory.success(new PageInfo((Page) body));
			}else{
				result = ResultFactory.success(body);
			}

		}else{
			result = (Result) body;
		}

		/**
		 * 满足以下条件之一，不统一打印log：
		 *  1、配置文件fsip.log.response=false 打印response开关为false
		 *  2、请求参数大于设置fsip.log.responseMax值。
		 */
		try{
			if(fsipLogProperties.isResponse()
					&& JSON.toJSONString(body).length() < fsipLogProperties.getResponseMax()){
				ObjectNode rootNode = mapper.createObjectNode();

				FSIPContext context = FSIPContextFactory.getContext();
				long endTime = System.currentTimeMillis();

				rootNode.put(FSIPWebConstants.CONTEXT_KEY_SERIALNO, context.getAttribute(FSIPWebConstants.CONTEXT_KEY_SERIALNO));
				rootNode.put("status", ((ServletServerHttpResponse) response).getServletResponse().getStatus());
				rootNode.put("startTime", context.getAttribute(FSIPWebConstants.CONTEXT_KEY_STARTTIME));
				rootNode.put("endTime", endTime);
				rootNode.put("time",  endTime - Long.parseLong(context.getAttribute(FSIPWebConstants.CONTEXT_KEY_STARTTIME)));
				rootNode.put("response", JSON.toJSONString(result));

				logger.info(rootNode.toString());
			}
		}catch (Exception e){
			logger.error(e.getMessage());
		}

		return result;
	}

}