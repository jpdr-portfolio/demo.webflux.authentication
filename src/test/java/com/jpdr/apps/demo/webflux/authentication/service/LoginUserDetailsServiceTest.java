package com.jpdr.apps.demo.webflux.authentication.service;

import com.jpdr.apps.demo.webflux.authentication.model.LoginUser;
import com.jpdr.apps.demo.webflux.authentication.repository.LoginUserRepository;
import com.jpdr.apps.demo.webflux.authentication.service.impl.LoginUserDetailsServiceImpl;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.EMAIL;
import static com.jpdr.apps.demo.webflux.authentication.util.TestDataGenerator.getLoginUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginUserDetailsServiceTest {
  
  @InjectMocks
  private LoginUserDetailsServiceImpl loginUserDetailsService;
  
  @Mock
  private LoginUserRepository loginUserRepository;
  
  @Test
  @DisplayName("OK - Find By Username")
  void givenUserWhenFindByUsernameThenReturnUser(){
    
    LoginUser expectedUser = getLoginUser();
    
    when(loginUserRepository.findByUsernameAndIsActiveIsTrue(anyString()))
      .thenReturn(Mono.just(expectedUser));
    
    StepVerifier.create(loginUserDetailsService.findByUsername(EMAIL))
      .assertNext(userDetails -> {
        assertEquals(expectedUser.getUsername(), userDetails.getUsername());
        assertEquals(expectedUser.getPassword(), userDetails.getPassword());
      })
      .expectComplete()
      .verify();
    
  }
  
  @Test
  @DisplayName("Error - Find By Username - Not Found")
  void givenUserNotFoundWhenFindByUsernameThenReturnError(){
    
    when(loginUserRepository.findByUsernameAndIsActiveIsTrue(anyString()))
      .thenReturn(Mono.empty());
    
    StepVerifier.create(loginUserDetailsService.findByUsername(EMAIL))
      .expectError(UsernameNotFoundException.class)
      .verify();
  }
  
}
