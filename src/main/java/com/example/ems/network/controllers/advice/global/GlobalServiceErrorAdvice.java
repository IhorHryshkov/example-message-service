/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-11T00:01
 */
package com.example.ems.network.controllers.advice.global;

import com.example.ems.config.messages.Messages;
import com.example.ems.network.controllers.exceptions.global.ResponseEmptyException;
import com.example.ems.network.controllers.wrapper.EMSServletRequestWrapper;
import com.example.ems.network.models.Res;
import com.example.ems.utils.network.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalServiceErrorAdvice {

	private final Response<Object> response;
	private final Messages messages;

	GlobalServiceErrorAdvice(Response<Object> response, Messages messages) {
		this.response = response;
		this.messages = messages;
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Res<Object>> handleAnyException(HttpServletRequest req, Exception e) {
		String requestId = ((EMSServletRequestWrapper) req).getRequestId().toString();
		log.error("requestId: {},\nmessage: {}", requestId, e.getMessage());
		e.printStackTrace();
		return response.formattedError(req, messages.getInternalServerError().getMessage(), MediaType.APPLICATION_JSON, messages.getInternalServerError().getCode());
	}

	@ExceptionHandler(ResponseEmptyException.class)
	public ResponseEntity<Res<Object>> handleNotModified(HttpServletRequest req, ResponseEmptyException e) {
		String requestId = ((EMSServletRequestWrapper) req).getRequestId().toString();
		log.info("requestId: {},\nmessage: {}", requestId, e.getMessage());
		return response.formattedError(req, messages.getNotModified().getMessage(), MediaType.APPLICATION_JSON, messages.getNotModified().getCode());
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<Res<Object>> handleBindException(HttpServletRequest req, BindException e) {
		String requestId = ((EMSServletRequestWrapper) req).getRequestId().toString();
		List<String> errors = e.getAllErrors()
				.stream()
				.map(x -> x.getDefaultMessage())
				.collect(Collectors.toList());
		log.error("requestId: {},\nerrors: {},\nmessage: {}", requestId, errors, e.getMessage());
		e.printStackTrace();
		return response.formattedError(req, messages.getRequestBodyIncorrect().getMessage(), MediaType.APPLICATION_JSON, messages.getRequestBodyIncorrect().getCode());
	}
}
