package com.jpdr.apps.demo.webflux.authentication.repository;

import com.jpdr.apps.demo.webflux.authentication.model.LoginUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoginUserRepository extends ReactiveCrudRepository<LoginUser, Integer> {
  
  Flux<LoginUser> findAllByIsActiveIsTrue();
  Mono<LoginUser> findByUsernameAndIsActiveIsTrue(String username);
  
}
