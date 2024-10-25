package com.jpdr.apps.demo.webflux.authentication.exception;

import com.jpdr.apps.demo.webflux.authentication.exception.dto.ErrorDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
  
  public static final String AN_ERROR_HAS_OCCURRED = "An error has occurred";
  
  @ExceptionHandler(MethodNotAllowedException.class)
  ResponseEntity<Mono<ErrorDto>> handleException(MethodNotAllowedException ex){
    log.warn(ExceptionUtils.getStackTrace(ex));
    ErrorDto errorDto = new ErrorDto(ex.getMessage());
    return new ResponseEntity<>(Mono.just(errorDto), HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(ServerWebInputException.class)
  ResponseEntity<Mono<ErrorDto>> handleException(ServerWebInputException ex){
    log.warn(ExceptionUtils.getStackTrace(ex));
    ErrorDto errorDto = new ErrorDto(ex.getMessage());
    return new ResponseEntity<>(Mono.just(errorDto), HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(ValidationException.class)
  ResponseEntity<Mono<ErrorDto>> handleException(ValidationException ex){
    log.warn(ExceptionUtils.getStackTrace(ex));
    ErrorDto errorDto = new ErrorDto(ex.getMessage());
    return new ResponseEntity<>(Mono.just(errorDto), HttpStatus.BAD_REQUEST);
  }
  
  
  @ExceptionHandler(InvalidUsernameException.class)
  ResponseEntity<Mono<Void>> handleException(InvalidUsernameException ex){
    log.warn(ExceptionUtils.getStackTrace(ex));
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }
  
  @ExceptionHandler(InvalidAuthenticationForTokenException.class)
  ResponseEntity<Mono<ErrorDto>> handleException(InvalidAuthenticationForTokenException ex){
    log.warn(ExceptionUtils.getStackTrace(ex));
    ErrorDto errorDto = new ErrorDto(ex.getMessage());
    return new ResponseEntity<>(Mono.just(errorDto), HttpStatus.UNAUTHORIZED);
  }
  
  @ExceptionHandler(UserNotAuthenticatedException.class)
  ResponseEntity<Mono<ErrorDto>> handleException(UserNotAuthenticatedException ex){
    log.warn(ExceptionUtils.getStackTrace(ex));
    ErrorDto errorDto = new ErrorDto(ex.getMessage());
    return new ResponseEntity<>(Mono.just(errorDto), HttpStatus.UNAUTHORIZED);
  }
  
  
  @ExceptionHandler(ExpiredJwtException.class)
  ResponseEntity<Mono<ErrorDto>> handleException(ExpiredJwtException ex){
    log.warn(ExceptionUtils.getStackTrace(ex));
    ErrorDto errorDto = new ErrorDto(ex.getMessage());
    return new ResponseEntity<>(Mono.just(errorDto), HttpStatus.UNAUTHORIZED);
  }
  
  @ExceptionHandler(UnsupportedJwtException.class)
  ResponseEntity<Mono<ErrorDto>> handleException(UnsupportedJwtException ex){
    log.warn(ExceptionUtils.getStackTrace(ex));
    ErrorDto errorDto = new ErrorDto(ex.getMessage());
    return new ResponseEntity<>(Mono.just(errorDto), HttpStatus.UNAUTHORIZED);
  }
  
  @ExceptionHandler(MalformedJwtException.class)
  ResponseEntity<Mono<ErrorDto>> handleException(MalformedJwtException ex){
    log.warn(ExceptionUtils.getStackTrace(ex));
    ErrorDto errorDto = new ErrorDto(ex.getMessage());
    return new ResponseEntity<>(Mono.just(errorDto), HttpStatus.UNAUTHORIZED);
  }
  
  @ExceptionHandler(SignatureException.class)
  ResponseEntity<Mono<ErrorDto>> handleException(SignatureException ex){
    log.warn(ExceptionUtils.getStackTrace(ex));
    ErrorDto errorDto = new ErrorDto(ex.getMessage());
    return new ResponseEntity<>(Mono.just(errorDto), HttpStatus.UNAUTHORIZED);
  }
  
  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<Mono<ErrorDto>> handleException(IllegalArgumentException ex){
    log.warn(ExceptionUtils.getStackTrace(ex));
    ErrorDto errorDto = new ErrorDto(ex.getMessage());
    return new ResponseEntity<>(Mono.just(errorDto), HttpStatus.UNAUTHORIZED);
  }
  
  
  @ExceptionHandler(PropertyNotFoundException.class)
  ResponseEntity<Mono<ErrorDto>> handleException(PropertyNotFoundException ex){
    log.error(ExceptionUtils.getStackTrace(ex));
    ErrorDto errorDto = new ErrorDto(AN_ERROR_HAS_OCCURRED);
    return new ResponseEntity<>(Mono.just(errorDto), HttpStatus.INTERNAL_SERVER_ERROR);
  }
  
  @ExceptionHandler(RuntimeException.class)
  ResponseEntity<Mono<ErrorDto>> handleException(RuntimeException ex){
    log.error(ExceptionUtils.getStackTrace(ex));
    ErrorDto errorDto = new ErrorDto(AN_ERROR_HAS_OCCURRED);
    return new ResponseEntity<>(Mono.just(errorDto), HttpStatus.INTERNAL_SERVER_ERROR);
  }
  
  @ExceptionHandler(Exception.class)
  ResponseEntity<Mono<ErrorDto>> handleException(Exception ex){
    log.error(ExceptionUtils.getStackTrace(ex));
    ErrorDto errorDto = new ErrorDto(AN_ERROR_HAS_OCCURRED);
    return new ResponseEntity<>(Mono.just(errorDto), HttpStatus.INTERNAL_SERVER_ERROR);
  }
  
  
}
