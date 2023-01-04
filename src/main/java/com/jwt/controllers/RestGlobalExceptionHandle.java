package com.jwt.controllers;

import com.jwt.exception.BadRequestException;
import com.jwt.exception.CustomErrorException;
import com.jwt.payload.response.ErrorMessage;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Log4j2
@RestControllerAdvice(basePackages = "com.jwt")
public class RestGlobalExceptionHandle extends ResponseEntityExceptionHandler {

  @ExceptionHandler({Exception.class})
  public ResponseEntity<ErrorMessage> handleAllException(Exception ex, HttpServletRequest request) {
    log.error("Lỗi không xác định : {}" , ExceptionUtils.getStackTrace(ex));
    ErrorMessage errorMsg = new ErrorMessage("Error: Username is already taken!")
            .setTimestamp(LocalDateTime.now().toString())
            .setStatus("500")
            .setPath(request.getRequestURI())
            .setErrors(Collections.singletonList(ex.getMessage()))
            .setEdesc(ex.toString());
    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorMsg);
  }

  @ExceptionHandler({NoSuchElementException.class})
  public ResponseEntity<ErrorMessage> handleNoSuchElementException(NoSuchElementException ex, HttpServletRequest request) {

    ErrorMessage errorMsg = new ErrorMessage()
            .setTimestamp(LocalDateTime.now().toString())
            .setStatus(HttpStatus.BAD_REQUEST.toString())
            .setPath(request.getRequestURI())
            .setErrors(Collections.singletonList(ex.getMessage()))
            .setEdesc(ex.toString());
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorMsg);
  }

  @ExceptionHandler({AccessDeniedException.class})
  public ResponseEntity<ErrorMessage> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) throws UnknownHostException {
    String ip4address = String.valueOf(Inet4Address.getLocalHost());
    log.error("Có xâm phạm nghi ngờ ip: {} , URL : {} , EX : {}" , ip4address , request.getRequestURI() , ex.getMessage());
    ex.printStackTrace();
    ErrorMessage errorMsg = new ErrorMessage()
            .setTimestamp(LocalDateTime.now().toString())
            .setStatus(HttpStatus.FORBIDDEN.toString())
            .setPath(request.getRequestURI())
            .setErrors(Collections.singletonList(ex.getMessage()))
            .setEdesc(ex + ":" + " " + ip4address);
    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorMsg);
  }

  @ExceptionHandler(CustomErrorException.class)
  public ResponseEntity<ErrorMessage> handleCustomException(CustomErrorException e, HttpServletRequest request) {
    ErrorMessage errorMsg = new ErrorMessage()
            .setTimestamp(LocalDateTime.now().toString())
            .setStatus(e.getHttpStatus().toString())
            .setPath(request.getRequestURI())
            .setErrors(Collections.singletonList(e.getMessage()))
            .setEdesc(e.getMessage());

    return ResponseEntity
            .status(e.getHttpStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorMsg);
  }

  @ExceptionHandler({BadRequestException.class, InvalidDataAccessApiUsageException.class})
  public ResponseEntity<ErrorMessage> handleBadRequestException(Exception ex, HttpServletRequest request) {
    log.error(ex.getMessage());
    ErrorMessage errorMsg = new ErrorMessage()
            .setTimestamp(LocalDateTime.now().toString())
            .setStatus("400")
            .setPath(request.getRequestURI())
            .setErrors(Collections.singletonList(ex.getMessage()))
            .setEdesc(ex.getMessage());
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorMsg);
  }

  // error handle for @Valid
  @NotNull
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                HttpHeaders headers,
                                                                @NotNull HttpStatus status,
                                                                @NotNull WebRequest request) {

    headers.setContentType(MediaType.APPLICATION_JSON);
    //Get all errors
    List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(x -> x.getField() + " : " + x.getDefaultMessage())
            .collect(Collectors.toList());

    ErrorMessage errorMsg = new ErrorMessage()
            .setTimestamp(LocalDateTime.now().toString())
            .setStatus("400")
            .setPath(((ServletWebRequest) request).getRequest().getRequestURI())
            .setErrors(errors);


    return new ResponseEntity<>(errorMsg, headers, status);

  }
}
