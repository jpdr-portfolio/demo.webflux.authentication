package com.jpdr.apps.demo.webflux.authentication.service;

import com.jpdr.apps.demo.webflux.authentication.service.dto.LoginUserDto;
import com.jpdr.apps.demo.webflux.authentication.service.dto.TokenDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AppService {
  
  Mono<List<LoginUserDto>> findUsers(String email);
  Mono<LoginUserDto> createUser(LoginUserDto userDto);
  Mono<TokenDto> getToken();
  Mono<Void> validateToken(String token);
  
}
