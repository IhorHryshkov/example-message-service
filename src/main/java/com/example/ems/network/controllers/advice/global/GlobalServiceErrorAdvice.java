/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-11T00:01
 */
package com.example.ems.network.controllers.advice.global;

import com.example.ems.config.messages.Messages;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.network.controllers.exceptions.global.ResponseEmptyException;
import com.example.ems.network.controllers.exceptions.global.ResponseIfNoneMatchException;
import com.example.ems.network.controllers.exceptions.status.UserIDNotFoundException;
import com.example.ems.network.controllers.exceptions.user.ResponseUsernameUsedException;
import com.example.ems.utils.network.Response;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
  public ResponseEntity<Res> handleAnyException(HttpServletRequest req, Exception e) {
    log.error("message: {}", e.getMessage());
    e.printStackTrace();
    return response.formattedError(
        req,
        messages.getInternalServerError().getMessage(),
        MediaType.APPLICATION_JSON,
        messages.getInternalServerError().getCode());
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
  public ResponseEntity<Res> handleValidationException(HttpServletRequest req, Exception e) {
    log.error("message: {}", e.getMessage());
    return response.formattedError(
        req,
        messages.getRequestBodyIncorrect().getMessage(),
        MediaType.APPLICATION_JSON,
        messages.getRequestBodyIncorrect().getCode());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<Res> handleResourceNotFoundException(HttpServletRequest req, Exception e) {
    log.error("message: {}", e.getMessage());
    return response.formattedError(
        req,
        messages.getEndpointNotFound().getMessage(),
        MediaType.APPLICATION_JSON,
        messages.getEndpointNotFound().getCode());
  }

  @ExceptionHandler(ResponseEmptyException.class)
  public ResponseEntity<Res> handleResultEmpty(HttpServletRequest req, Exception e) {
    log.info("message: {}", e.getMessage());
    return response.formattedError(
        req, null, MediaType.APPLICATION_JSON, messages.getResultEmpty().getCode());
  }

  @ExceptionHandler(ResponseIfNoneMatchException.class)
  public ResponseEntity<Res> handleNotModified(HttpServletRequest req, Exception e) {
    log.info("message: {}", e.getMessage());
    return response.formattedError(
        req, null, MediaType.APPLICATION_JSON, messages.getNotModified().getCode());
  }

  @ExceptionHandler(ResponseUsernameUsedException.class)
  public ResponseEntity<Res> handleUsernameUsed(HttpServletRequest req, Exception e) {
    log.info("message: {}", e.getMessage());
    return response.formattedError(
        req,
        messages.getUsernameUsed().getMessage(),
        MediaType.APPLICATION_JSON,
        messages.getUsernameUsed().getCode());
  }

  @ExceptionHandler(UserIDNotFoundException.class)
  public ResponseEntity<Res> handleUserIDNotfound(HttpServletRequest req, Exception e) {
    log.info("message: {}", e.getMessage());
    return response.formattedError(
        req,
        messages.getUserIdNotFound().getMessage(),
        MediaType.APPLICATION_JSON,
        messages.getUserIdNotFound().getCode());
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<Res> handleBindException(HttpServletRequest req, BindException e) {
    List<String> errors =
        e.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.toList());
    log.error("errors: {},\nmessage: {}", errors, e.getMessage());
    e.printStackTrace();
    return response.formattedError(
        req,
        messages.getRequestBodyIncorrect().getMessage(),
        MediaType.APPLICATION_JSON,
        messages.getRequestBodyIncorrect().getCode());
  }
}
