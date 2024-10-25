package com.jpdr.apps.demo.webflux.authentication.service.impl;

import com.jpdr.apps.demo.webflux.authentication.exception.InvalidAuthenticationForTokenException;
import com.jpdr.apps.demo.webflux.authentication.exception.InvalidUsernameException;
import com.jpdr.apps.demo.webflux.authentication.exception.UserNotAuthenticatedException;
import com.jpdr.apps.demo.webflux.authentication.model.LoginUser;
import com.jpdr.apps.demo.webflux.authentication.repository.LoginUserRepository;
import com.jpdr.apps.demo.webflux.authentication.service.AppService;
import com.jpdr.apps.demo.webflux.authentication.service.AppTokenService;
import com.jpdr.apps.demo.webflux.authentication.service.dto.LoginUserDto;
import com.jpdr.apps.demo.webflux.authentication.service.dto.TokenDto;
import com.jpdr.apps.demo.webflux.authentication.service.mapper.LoginUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppServiceImpl implements AppService {
  
  private final LoginUserRepository loginUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final AppTokenService appTokenService;
  
  
  @Override
  public Flux<LoginUserDto> findUsers(String userEmail) {
    return Mono.just(Optional.ofNullable(userEmail))
      .flatMapMany(optional -> {
        if(optional.isPresent()){
          return Flux.from(findUserByEmail(userEmail));
        }
        return findAllUsers();
      });
  }
  
  @Override
  public Mono<LoginUserDto> createUser(LoginUserDto dto) {
    log.debug("createUser");
    return Mono.fromCallable(() -> LoginUser.builder()
          .id(null)
          .username(dto.getUsername())
          .password(passwordEncoder.encode(dto.getPassword()))
          .isActive(true)
          .creationDate(OffsetDateTime.now())
          .deletionDate(null)
          .build())
      .subscribeOn(Schedulers.boundedElastic())
      .flatMap(this.loginUserRepository::save)
      .map(LoginUserMapper.INSTANCE::entityToDto)
      .doOnNext(loginUserDto -> log.debug(loginUserDto.toString()));
  }
  
  @Override
  public Mono<TokenDto> getToken() {
    return ReactiveSecurityContextHolder.getContext()
      .map(SecurityContext::getAuthentication)
      .switchIfEmpty(Mono.error(new UserNotAuthenticatedException()))
      .map(Authentication::getPrincipal)
      .filter(UserDetails.class::isInstance)
      .switchIfEmpty(Mono.error(new InvalidAuthenticationForTokenException()))
      .cast(UserDetails.class)
      .flatMap(this.appTokenService::generateToken)
      .map(token -> TokenDto.builder()
        .data(token)
        .build());
  }
  
  @Override
  public Mono<Void> validateToken(String token) {
    return this.appTokenService.getClaims(token)
      .flatMap(claims -> Mono.zip(
        Mono.just(claims),
        Mono.from(this.loginUserRepository.findByUsernameAndIsActiveIsTrue(claims.getSubject())))
      .filter(tuple -> tuple.getT1().getSubject().equals(tuple.getT2().getUsername()))
      .switchIfEmpty(Mono.error(new InvalidUsernameException())))
      .flatMap(result -> Mono.empty());
  }
  
  
  private Mono<LoginUserDto> findUserByEmail(String userEmail){
    log.debug("findUserByEmail");
    return this.loginUserRepository.findByUsernameAndIsActiveIsTrue(userEmail)
      .map(LoginUserMapper.INSTANCE::entityToDto)
      .doOnNext(loginUserDto -> log.debug(loginUserDto.toString()));
  }
  
  private Flux<LoginUserDto> findAllUsers(){
    log.debug("findAllUsers");
    return this.loginUserRepository.findAllByIsActiveIsTrue()
      .map(LoginUserMapper.INSTANCE::entityToDto)
      .doOnNext(loginUserDto -> log.debug(loginUserDto.toString()));
  }
  
  
}
