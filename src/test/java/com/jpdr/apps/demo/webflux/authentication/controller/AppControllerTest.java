package com.jpdr.apps.demo.webflux.authentication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpdr.apps.demo.webflux.authentication.exception.InvalidUsernameException;
import com.jpdr.apps.demo.webflux.authentication.service.AppService;
import com.jpdr.apps.demo.webflux.authentication.service.dto.LoginUserDto;
import com.jpdr.apps.demo.webflux.authentication.service.dto.TokenDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.EMAIL;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getBasicAuthorization;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getLoginUserDto;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getLoginUserDtos;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getNewLoginUser;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getToken;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getTokenDto;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getUserDetails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ExtendWith(MockitoExtension.class)
class AppControllerTest {
  
  @Autowired
  private WebTestClient webTestClient;
  @MockBean
  private AppService appService;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private ReactiveUserDetailsService reactiveUserDetailsService;
  
  @Test
  @DisplayName("OK - Find Users - No User Email")
  void givenNoUserEmailWhenFindUsersThenReturnUsers() throws JsonProcessingException {
    
    List<LoginUserDto> expectedUsers = getLoginUserDtos();
    String authorizationValue = getBasicAuthorization();
    UserDetails userDetails = getUserDetails();
    
    String expectedBody = objectMapper.writeValueAsString(expectedUsers);
    
    when(appService.findUsers(isNull()))
      .thenReturn(Flux.fromIterable(expectedUsers));
    when(reactiveUserDetailsService.findByUsername(anyString()))
      .thenReturn(Mono.just(userDetails));
    
    FluxExchangeResult<String> exchangeResult = this.webTestClient.get()
      .uri("/authentication/secure/users")
      .header(HttpHeaders.AUTHORIZATION, authorizationValue)
      .exchange()
      .expectHeader()
      .contentType(MediaType.APPLICATION_JSON)
      .expectStatus()
      .isOk()
      .returnResult(String.class);
    
    StepVerifier.create(exchangeResult.getResponseBody())
      .assertNext(receivedBody -> assertEquals(expectedBody, receivedBody))
      .expectComplete()
      .verify();
    
  }
  
  @Test
  @DisplayName("OK - Find Users - With User Email")
  void givenWithUserEmailWhenFindUsersThenReturnUser() throws JsonProcessingException {
    
    List<LoginUserDto> expectedUsers = List.of(getLoginUserDto());
    String authorizationValue = getBasicAuthorization();
    UserDetails userDetails = getUserDetails();
    
    String expectedBody = objectMapper.writeValueAsString(expectedUsers);
    
    when(appService.findUsers(anyString()))
      .thenReturn(Flux.fromIterable(expectedUsers));
    when(reactiveUserDetailsService.findByUsername(anyString()))
      .thenReturn(Mono.just(userDetails));
    
    FluxExchangeResult<String> exchangeResult = this.webTestClient.get()
      .uri("/authentication/secure/users" + "?userEmail=" + EMAIL)
      .header(HttpHeaders.AUTHORIZATION, authorizationValue)
      .exchange()
      .expectHeader()
      .contentType(MediaType.APPLICATION_JSON)
      .expectStatus()
      .isOk()
      .returnResult(String.class);
    
    StepVerifier.create(exchangeResult.getResponseBody())
      .assertNext(receivedBody -> assertEquals(expectedBody, receivedBody))
      .expectComplete()
      .verify();
  }
  
  
  @Test
  @DisplayName("Error - Find Users - No Authorization")
  void givenNoAuthorizationWhenFindUsersThenReturnError() {
    
    this.webTestClient.get()
      .uri("/authentication/secure/users")
      .exchange()
      .expectStatus()
        .isUnauthorized()
      .expectBody()
        .isEmpty();
    
  }
  
  
  
  @Test
  @DisplayName("OK - Create User")
  void givenUserWhenCreateUserThenReturnUser() throws JsonProcessingException {
    
    LoginUserDto requestUser = getNewLoginUser();
    LoginUserDto expectedUser = getLoginUserDto();
    
    String expectedBody = objectMapper.writeValueAsString(expectedUser);
    
    when(appService.createUser(any(LoginUserDto.class)))
      .thenReturn(Mono.just(expectedUser));
    
    FluxExchangeResult<String> exchangeResult = this.webTestClient.post()
      .uri("/authentication/unsecure/users")
      .bodyValue(requestUser)
      .exchange()
      .expectHeader()
      .contentType(MediaType.APPLICATION_JSON)
      .expectStatus()
      .isCreated()
      .returnResult(String.class);
    
    StepVerifier.create(exchangeResult.getResponseBody())
      .assertNext(receivedBody -> assertEquals(expectedBody, receivedBody))
      .expectComplete()
      .verify();
    
    
  }
  
  
  
  
  
  @Test
  @DisplayName("OK - Get Token")
  void givenUserWhenGetTokenThenReturnToken() throws Exception {
    
    TokenDto expectedToken = getTokenDto();
    String authorizationValue = getBasicAuthorization();
    UserDetails userDetails = getUserDetails();
    
    String expectedBody = objectMapper.writeValueAsString(expectedToken);
    
    when(reactiveUserDetailsService.findByUsername(anyString()))
      .thenReturn(Mono.just(userDetails));
    when(appService.getToken())
      .thenReturn(Mono.just(expectedToken));
    
    FluxExchangeResult<String> exchangeResult = this.webTestClient.get()
      .uri("/authentication/secure/tokens")
      .header(HttpHeaders.AUTHORIZATION, authorizationValue)
      .exchange()
      .expectHeader()
      .contentType(MediaType.APPLICATION_JSON)
      .expectStatus()
      .isOk()
      .returnResult(String.class);
    
    StepVerifier.create(exchangeResult.getResponseBody())
      .assertNext(receivedBody -> assertEquals(expectedBody, receivedBody))
      .expectComplete()
      .verify();
  }
  
  
  @Test
  @DisplayName("Error - Get Token - No Authorization")
  void givenNoAuthorizationWhenGetTokenThenReturnError() {
    
    this.webTestClient.get()
      .uri("/authentication/secure/tokens")
      .exchange()
      .expectStatus()
        .isUnauthorized()
      .expectBody()
        .isEmpty();
    
  }
  
  
  @Test
  @DisplayName("OK - Validate Token")
  void givenTokenWhenValidateTokenThenReturnEmpty(){
    
    String token = getToken();
    UserDetails userDetails = getUserDetails();
    
    when(reactiveUserDetailsService.findByUsername(anyString()))
      .thenReturn(Mono.just(userDetails));
    when(appService.validateToken(anyString()))
      .thenReturn(Mono.empty());
    
    this.webTestClient.post()
      .uri("/authentication/unsecure/tokens/validate")
      .accept(MediaType.TEXT_PLAIN)
      .bodyValue(token)
      .exchange()
      .expectStatus()
        .isNoContent()
      .expectBody()
        .isEmpty();
    
  }
  
  
  @Test
  @DisplayName("Error - Validate Token - Invalid Username")
  void givenInvalidUsernameWhenValidateTokenThenReturnError(){
    
    String token = getToken();
    UserDetails userDetails = getUserDetails();
    
    when(reactiveUserDetailsService.findByUsername(anyString()))
      .thenReturn(Mono.just(userDetails));
    when(appService.validateToken(anyString()))
      .thenReturn(Mono.error(new InvalidUsernameException()));
    
    this.webTestClient.post()
      .uri("/authentication/unsecure/tokens/validate")
      .accept(MediaType.TEXT_PLAIN)
      .bodyValue(token)
      .exchange()
      .expectStatus()
        .isUnauthorized()
      .expectBody()
        .isEmpty();
    
  }
  
  
}
