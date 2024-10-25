package com.jpdr.apps.demo.webflux.authentication.service.impl;

import com.jpdr.apps.demo.webflux.authentication.helper.LoginUserDetails;
import com.jpdr.apps.demo.webflux.authentication.repository.LoginUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUserDetailsServiceImpl implements ReactiveUserDetailsService {
  
  private final LoginUserRepository loginUserRepository;
  
  @Override
  public Mono<UserDetails> findByUsername(String username) {
    log.debug("loadUserByUsername");
    return this.loginUserRepository.findByUsernameAndIsActiveIsTrue(username)
      .switchIfEmpty(Mono.error(new UsernameNotFoundException(username)))
      .map(loginUser -> User
        .withUserDetails(new LoginUserDetails(loginUser))
        .build())
    .doOnNext(loginUser -> log.debug(loginUser.toString()));
  }
}
