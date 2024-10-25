package com.jpdr.apps.demo.webflux.authentication.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
  
  private final ReactiveUserDetailsService userDetailsService;
  
  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity){
    httpSecurity.csrf(CsrfSpec::disable)
      .authorizeExchange(spec -> spec
        .pathMatchers("/authentication/unsecure/**").permitAll()
        .pathMatchers("/authentication/secure/**").authenticated())
      .httpBasic(Customizer.withDefaults())
      .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
      .logout(ServerHttpSecurity.LogoutSpec::disable);
    return httpSecurity.build();
  }
  
  @Bean("appPasswordEncoder")
  public PasswordEncoder passwordEncoder(){
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
  
  @Bean
  public ReactiveAuthenticationManager authenticationManager(
    @Qualifier("appPasswordEncoder") PasswordEncoder passwordEncoder){
    UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
      new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    authenticationManager.setPasswordEncoder(passwordEncoder);
    return authenticationManager;
  }
  
  

}
