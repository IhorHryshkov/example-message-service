/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-11T00:01
 */
package com.example.ems.network.controllers.advice.global;

import com.example.ems.network.controllers.exceptions.global.ResponseEmptyException;
import com.example.ems.network.models.Res;
import com.example.ems.utils.network.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

	GlobalServiceErrorAdvice(Response<Object> response) {
		this.response = response;
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Res<Object>> handleAnyException(HttpServletRequest req, Exception e) {
		return response.formattedError(req, e.getMessage(), MediaType.APPLICATION_JSON, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ResponseEmptyException.class)
	public ResponseEntity<Res<Object>> handleAnyException(HttpServletRequest req, ResponseEmptyException e) {
		return response.formattedError(req, null, MediaType.APPLICATION_JSON, HttpStatus.NO_CONTENT);
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<Res<Object>> handleBindException(HttpServletRequest req, BindException e) {
		List<String> errors = e.getAllErrors()
				.stream()
				.map(x -> x.getDefaultMessage())
				.collect(Collectors.toList());

		return response.formattedError(req, String.format("Request body or query or params data is incorrect: %s", errors), MediaType.APPLICATION_JSON, HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
