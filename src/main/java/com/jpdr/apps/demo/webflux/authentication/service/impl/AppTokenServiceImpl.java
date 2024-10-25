package com.jpdr.apps.demo.webflux.authentication.service.impl;

import com.jpdr.apps.demo.webflux.authentication.exception.PropertyNotFoundException;
import com.jpdr.apps.demo.webflux.authentication.service.AppTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppTokenServiceImpl implements AppTokenService {
  
  public static final String TOKEN_EXPIRATION_PROPERTY_NAME = "app.token.expiration";
  public static final String TOKEN_SECRET_PROPERTY_NAME = "app.token.secret";
  
  private final Environment env;
  
  @Override
  public Mono<String> generateToken(UserDetails userDetails) {
    return Mono.zip(
      Mono.just(userDetails),
      Mono.from(
        Mono.justOrEmpty(env.getProperty(TOKEN_EXPIRATION_PROPERTY_NAME))
          .switchIfEmpty(Mono.defer(() -> Mono.error(new PropertyNotFoundException(TOKEN_EXPIRATION_PROPERTY_NAME))))),
        Mono.from(getKey()))
      .flatMap(tuple -> Mono.just(Jwts.builder()
        .subject(tuple.getT1().getUsername())
        .claim("roles", tuple.getT1().getAuthorities())
        .issuedAt(new Date())
        .expiration(new Date(new Date().getTime() + Long.parseLong(tuple.getT2())))
        .signWith(tuple.getT3())
        .compact()));
  }
  
  @Override
  public Mono<Claims> getClaims(String token) {
    return Mono.zip(
        Mono.just(token),
        Mono.from(getKey()))
      .flatMap(tuple ->
        Mono.fromCallable(() -> Jwts.parser()
            .verifyWith(tuple.getT2())
            .build()
            .parseSignedClaims(tuple.getT1())
            .getPayload())
          .subscribeOn(Schedulers.boundedElastic()));
  }
  
  @Override
  public Mono<SecretKey> getKey() {
    return Mono.justOrEmpty(env.getProperty(TOKEN_SECRET_PROPERTY_NAME))
      .switchIfEmpty(Mono.defer(() ->
        Mono.error(new PropertyNotFoundException(TOKEN_SECRET_PROPERTY_NAME))))
      .flatMap(secret -> Mono.fromCallable(() -> Decoders.BASE64.decode(secret))
          .subscribeOn(Schedulers.boundedElastic()))
      .flatMap(decodedSecret -> Mono.fromCallable(() -> Keys.hmacShaKeyFor(decodedSecret))
          .subscribeOn(Schedulers.boundedElastic()));
  }
}
