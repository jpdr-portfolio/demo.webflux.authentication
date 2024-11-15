package com.jpdr.apps.demo.webflux.authentication.service;

import com.jpdr.apps.demo.webflux.authentication.exception.InvalidUsernameException;
import com.jpdr.apps.demo.webflux.authentication.exception.PropertyNotFoundException;
import com.jpdr.apps.demo.webflux.authentication.model.LoginUser;
import com.jpdr.apps.demo.webflux.authentication.repository.LoginUserRepository;
import com.jpdr.apps.demo.webflux.authentication.service.dto.LoginUserDto;
import com.jpdr.apps.demo.webflux.authentication.service.dto.TokenDto;
import com.jpdr.apps.demo.webflux.authentication.service.impl.AppServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.security.test.context.support.ReactorContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.jpdr.apps.demo.webflux.authentication.service.impl.AppTokenServiceImpl.TOKEN_EXPIRATION_PROPERTY_NAME;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.EMAIL;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getClaims;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getEncodedPassword;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getLoginUser;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getLoginUsers;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getNewLoginUser;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getToken;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getTokenDto;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getUserDetails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AppServiceTest {
  
  @InjectMocks
  private AppServiceImpl appService;
  @Mock
  private LoginUserRepository loginUserRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private AppTokenService appTokenService;
  @Mock
  private Authentication authentication;
  
  private TestExecutionListener reactorContextTestExecutionListener =
    new ReactorContextTestExecutionListener();
  
  @BeforeEach
  void beforeEach() throws Exception{
    TestSecurityContextHolder.setAuthentication(authentication);
    reactorContextTestExecutionListener.beforeTestMethod(null);
  }
  
  @Test
  @DisplayName("OK - Find Users - Null User Email")
  void givenNullUserIdWhenFindUserThenReturnUser(){
    
    List<LoginUser> expectedUsers = getLoginUsers();
    Map<Integer, LoginUser> expectedUsersMap = expectedUsers.stream()
      .collect(Collectors.toMap(LoginUser::getId, Function.identity()));
    
    when(loginUserRepository.findAllByIsActiveIsTrue())
      .thenReturn(Flux.fromIterable(expectedUsers));
    
    StepVerifier.create(appService.findUsers(null))
      .assertNext(receivedUsers ->{
        for(LoginUserDto receivedUser : receivedUsers){
          assertUser(expectedUsersMap.get(receivedUser.getId()),receivedUser);
        }
      })
      .expectComplete()
      .verify();
  
  }
  @Test
  @DisplayName("OK - Find Users - With User Email")
  void givenUserIdWhenFindUserThenReturnUser(){
    
    LoginUser expectedUser = getLoginUser();
    
    when(loginUserRepository.findByUsernameAndIsActiveIsTrue(anyString()))
      .thenReturn(Mono.just(expectedUser));
    
    StepVerifier.create(appService.findUsers(EMAIL))
      .assertNext(receivedUsers -> assertUser(expectedUser, receivedUsers.getFirst()))
      .expectComplete()
      .verify();
  }
  
  @Test
  @DisplayName("OK - Create User")
  void givenUserWhenCreateUserThenReturnUser(){
    
    LoginUserDto requestUser = getNewLoginUser();
    LoginUser expectedUser = getLoginUser();
    
    when(passwordEncoder.encode(anyString()))
      .thenReturn(getEncodedPassword());
    when(loginUserRepository.save(any(LoginUser.class)))
      .thenReturn(Mono.just(expectedUser));
    
    StepVerifier.create(appService.createUser(requestUser))
      .assertNext(receivedUser -> {
        assertEquals(expectedUser.getUsername(),receivedUser.getUsername());
        assertEquals(expectedUser.getPassword(), receivedUser.getPassword());
      })
      .expectComplete()
      .verify();
    
  }
  
  
  
  
  
  @Test
  @DisplayName("OK - Get Token")
  void givenUserPasswordWhenCreateTokenThenReturnToken(){
    
    UserDetails userDetails = getUserDetails();
    TokenDto expectedToken = getTokenDto();
    
    when(authentication.getPrincipal())
      .thenReturn(userDetails);
    when(appTokenService.generateToken(any(UserDetails.class)))
      .thenReturn(Mono.just(getToken()));
    
    StepVerifier.create(appService.getToken())
      .assertNext(receivedToken -> assertEquals(expectedToken,receivedToken))
      .expectComplete()
      .verify();
  
  }
  
  @Test
  @DisplayName("Error - Get Token - Property Not Found")
  void givenPropertyNotFoundWhenCreateTokenThenReturnError(){
    
    UserDetails userDetails = getUserDetails();
    
    when(authentication.getPrincipal())
      .thenReturn(userDetails);
    when(appTokenService.generateToken(any(UserDetails.class)))
      .thenReturn(Mono.error(new PropertyNotFoundException(TOKEN_EXPIRATION_PROPERTY_NAME)));
    
    StepVerifier.create(appService.getToken())
      .expectError(PropertyNotFoundException.class)
      .verify();
    
  }
  
  @Test
  @DisplayName("OK - Validate Token")
  void givenTokenWhenValidateTokenThenReturnOK(){
    
    LoginUser loginUser = getLoginUser();
    String token = getToken();
    Claims claims = getClaims(token);
    
    when(appTokenService.getClaims(anyString()))
      .thenReturn(Mono.just(claims));
    when(loginUserRepository.findByUsernameAndIsActiveIsTrue(anyString()))
      .thenReturn(Mono.just(loginUser));
    
    StepVerifier.create(appService.validateToken(token))
      .expectComplete()
      .verify();
  }
  
  @Test
  @DisplayName("Error - Validate Token - Malformed JWT")
  void givenMalformedTokenWhenValidateTokenThenReturnError(){
    
    String token = getToken();
    
    when(appTokenService.getClaims(anyString()))
      .thenReturn(Mono.error(new MalformedJwtException("")));
    
    StepVerifier.create(appService.validateToken(token))
      .expectError(MalformedJwtException.class)
      .verify();
  }
  
  @Test
  @DisplayName("Error - Validate Token - Invalid User")
  void givenInvalidUSerWhenValidateTokenThenReturnError(){
    
    String token = getToken();
    Claims claims = getClaims(token);
    
    when(appTokenService.getClaims(anyString()))
      .thenReturn(Mono.just(claims));
    when(loginUserRepository.findByUsernameAndIsActiveIsTrue(anyString()))
      .thenReturn(Mono.empty());
    
    StepVerifier.create(appService.validateToken(token))
      .expectError(InvalidUsernameException.class)
      .verify();
  }
  
  private static void assertUser(LoginUser entity, LoginUserDto dto){
    assertEquals(entity.getId(),dto.getId());
    assertEquals(entity.getUsername(), dto.getUsername());
    assertEquals(entity.getPassword(), dto.getPassword());
    assertEquals(entity.getIsActive(), dto.getIsActive());
    assertNotNull(dto.getCreationDate());
    assertNull(dto.getDeletionDate());
  }
  
  @AfterEach
  void afterEach() throws Exception{
    reactorContextTestExecutionListener.afterTestMethod(null);
  }
  
  
}
