package com.jpdr.apps.demo.webflux.authentication.exception;

import com.jpdr.apps.demo.webflux.authentication.exception.dto.ErrorDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
  
  @InjectMocks
  GlobalExceptionHandler globalExceptionHandler;
  
  @Test
  @DisplayName("Error - MethodNotAllowedException")
  void givenMethodNotAllowedExceptionWhenHandleExceptionThenReturnError(){
    MethodNotAllowedException exception = new MethodNotAllowedException(HttpMethod.GET, List.of(HttpMethod.POST));
    ResponseEntity<Mono<ErrorDto>> response = globalExceptionHandler.handleException(exception);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
  
  @Test
  @DisplayName("Error - ServerWebInputException")
  void givenServerWebInputExceptionWhenHandleExceptionThenReturnError(){
    ServerWebInputException exception = new ServerWebInputException("");
    ResponseEntity<Mono<ErrorDto>> response = globalExceptionHandler.handleException(exception);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
  
  @Test
  @DisplayName("Error - ValidationException")
  void givenValidationExceptionWhenHandleValidationExceptionThenReturnError(){
    ValidationException exception = new ValidationException("");
    ResponseEntity<Mono<ErrorDto>> response = globalExceptionHandler.handleException(exception);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
  
  
  @Test
  @DisplayName("Error - InvalidAuthenticationForTokenException")
  void givenInvalidAuthenticationForTokenExceptionWhenHandleExceptionThenReturnError(){
    InvalidAuthenticationForTokenException exception = new InvalidAuthenticationForTokenException();
    ResponseEntity<Mono<ErrorDto>> response = globalExceptionHandler.handleException(exception);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }
  

  
  @Test
  @DisplayName("Error - InvalidUsernameException")
  void givenInvalidUsernameExceptionWhenHandleExceptionThenReturnError(){
    InvalidUsernameException exception = new InvalidUsernameException();
    ResponseEntity<Mono<Void>> response = globalExceptionHandler.handleException(exception);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }
  
  @Test
  @DisplayName("Error - UserNotAuthenticatedException")
  void givenUserNotAuthenticatedExceptionWhenHandleExceptionThenReturnError(){
    UserNotAuthenticatedException exception = new UserNotAuthenticatedException();
    ResponseEntity<Mono<ErrorDto>> response = globalExceptionHandler.handleException(exception);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }
  
  @Test
  @DisplayName("Error - MalformedJwtException")
  void givenMalformedJwtExceptionWhenHandleExceptionThenReturnError(){
    MalformedJwtException exception = new MalformedJwtException("");
    ResponseEntity<Mono<ErrorDto>> response = globalExceptionHandler.handleException(exception);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }
  
  @Test
  @DisplayName("Error - ExpiredJwtException")
  void givenExpiredJwtExceptionWhenHandleExceptionThenReturnError(){
    ExpiredJwtException exception = new ExpiredJwtException(null,null,"");
    ResponseEntity<Mono<ErrorDto>> response = globalExceptionHandler.handleException(exception);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }
  
  @Test
  @DisplayName("Error - UnsupportedJwtException")
  void givenUnsupportedJwtExceptionWhenHandleExceptionThenReturnError(){
    UnsupportedJwtException exception = new UnsupportedJwtException("");
    ResponseEntity<Mono<ErrorDto>> response = globalExceptionHandler.handleException(exception);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }
  
  @Test
  @DisplayName("Error - SignatureException")
  void givenSignatureExceptionWhenHandleExceptionThenReturnError(){
    SignatureException exception = new SignatureException("");
    ResponseEntity<Mono<ErrorDto>> response = globalExceptionHandler.handleException(exception);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }
  
  @Test
  @DisplayName("Error - IllegalArgumentException")
  void givenIllegalArgumentExceptionWhenHandleExceptionThenReturnError(){
    IllegalArgumentException exception = new IllegalArgumentException("");
    ResponseEntity<Mono<ErrorDto>> response = globalExceptionHandler.handleException(exception);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }
  
  
  
  
  
  
  @Test
  @DisplayName("Error - PropertyNotFoundException")
  void givenProductNotFoundExceptionWhenHandleExceptionThenReturnError(){
    PropertyNotFoundException exception = new PropertyNotFoundException("");
    ResponseEntity<Mono<ErrorDto>> response = globalExceptionHandler.handleException(exception);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }
  
  
  
  
  @Test
  @DisplayName("Error - RuntimeException")
  void givenRuntimeExceptionWhenHandleRuntimeExceptionThenReturnError(){
    RuntimeException exception = new RuntimeException();
    ResponseEntity<Mono<ErrorDto>> response = globalExceptionHandler.handleException(exception);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }
  
  @Test
  @DisplayName("Error - Exception")
  void givenExceptionWhenHandleExceptionThenReturnError(){
    Exception exception = new Exception();
    ResponseEntity<Mono<ErrorDto>> response = globalExceptionHandler.handleException(exception);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }
  
}
