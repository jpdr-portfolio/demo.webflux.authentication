package com.jpdr.apps.demo.webflux.authentication.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

public interface AppTokenService {

  Mono<String> generateToken(UserDetails userDetails);
  Mono<Claims> getClaims(String token);
  Mono<SecretKey> getKey();
  

}
