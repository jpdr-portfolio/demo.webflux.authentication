package com.jpdr.apps.demo.webflux.authentication.service;

import com.jpdr.apps.demo.webflux.authentication.exception.PropertyNotFoundException;
import com.jpdr.apps.demo.webflux.authentication.service.impl.AppTokenServiceImpl;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.test.StepVerifier;

import static com.jpdr.apps.demo.webflux.authentication.service.impl.AppTokenServiceImpl.TOKEN_EXPIRATION_PROPERTY_NAME;
import static com.jpdr.apps.demo.webflux.authentication.service.impl.AppTokenServiceImpl.TOKEN_SECRET_PROPERTY_NAME;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.SECRET;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.EXPIRATION;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getClaims;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getToken;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getUserDetails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AppTokenServiceTest {
  
  @InjectMocks
  private AppTokenServiceImpl appTokenService;
  
  @Mock
  private Environment env;
  
  @Test
  @DisplayName("OK - Generate Token")
  void givenUserWhenGenerateTokenThenReturnToken(){
    
    UserDetails userDetails = getUserDetails();
    
    when(env.getProperty(TOKEN_EXPIRATION_PROPERTY_NAME)).thenReturn(EXPIRATION);
    when(env.getProperty(TOKEN_SECRET_PROPERTY_NAME)).thenReturn(SECRET);
    
    StepVerifier.create(appTokenService.generateToken(userDetails))
      .assertNext(receivedToken -> assertToken(userDetails, receivedToken))
      .expectComplete()
      .verify();
    
  }
  
  @Test
  @DisplayName("Error - Generate Token - Expiration Not Found")
  void givenExpirationNotFoundWhenGenerateTokenThenReturnError(){
    
    UserDetails userDetails = getUserDetails();
    
    when(env.getProperty(TOKEN_EXPIRATION_PROPERTY_NAME)).thenReturn(null);
    
    StepVerifier.create(appTokenService.generateToken(userDetails))
      .expectError(PropertyNotFoundException.class)
      .verify();
    
  }
  
  @Test
  @DisplayName("Error - Generate Token - Secret Not Found")
  void givenSecretNotFoundWhenGenerateTokenThenReturnError(){
    
    UserDetails userDetails = getUserDetails();
    
    when(env.getProperty(TOKEN_EXPIRATION_PROPERTY_NAME)).thenReturn(EXPIRATION);
    when(env.getProperty(TOKEN_SECRET_PROPERTY_NAME)).thenReturn(null);
    
    StepVerifier.create(appTokenService.generateToken(userDetails))
      .expectError(PropertyNotFoundException.class)
      .verify();
    
  }
  
  @Test
  @DisplayName("OK - Get Claims")
  void givenUserDetailsWhenGetClaimsThenReturnClaims(){
    
    UserDetails userDetails = getUserDetails();
    String token = getToken();
    
    when(env.getProperty(TOKEN_EXPIRATION_PROPERTY_NAME)).thenReturn(EXPIRATION);
    when(env.getProperty(TOKEN_SECRET_PROPERTY_NAME)).thenReturn(SECRET);
    
    StepVerifier.create(appTokenService.getClaims(token))
      .assertNext(claims -> assertClaims(userDetails, claims))
      .expectComplete()
      .verify();
    
  }
  
  @Test
  @DisplayName("OK - Get Key")
  void givenSecretWhenGetKeyThenReturnKey(){
    
    when(env.getProperty(TOKEN_EXPIRATION_PROPERTY_NAME)).thenReturn(EXPIRATION);
    when(env.getProperty(TOKEN_SECRET_PROPERTY_NAME)).thenReturn(SECRET);
    
    StepVerifier.create(appTokenService.getKey())
      .assertNext(Assertions::assertNotNull)
      .expectComplete()
      .verify();
    
  }
  
  private static void assertToken(UserDetails userDetails, String token){
    Claims claims = getClaims(token);
    assertEquals(userDetails.getUsername(), claims.getSubject());
  }
  
  private static void assertClaims(UserDetails userDetails, Claims claims){
    assertEquals(userDetails.getUsername(), claims.getSubject());
  }
  
}
