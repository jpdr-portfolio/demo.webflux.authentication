package com.jpdr.apps.demo.webflux.authentication.controller;

import com.jpdr.apps.demo.webflux.authentication.service.AppService;
import com.jpdr.apps.demo.webflux.authentication.service.dto.LoginUserDto;
import com.jpdr.apps.demo.webflux.authentication.service.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AppController {

  private final AppService appService;
  
  @GetMapping("/authentication/secure/users")
  public Mono<ResponseEntity<List<LoginUserDto>>> findUsers(
    @RequestParam(name = "userEmail", required = false) String userEmail){
    return this.appService.findUsers(userEmail)
      .map(user -> new ResponseEntity<>(user, HttpStatus.OK));
  }
  
  @PostMapping("/authentication/unsecure/users")
  public Mono<ResponseEntity<LoginUserDto>> createUser(@RequestBody LoginUserDto userDto){
    return this.appService.createUser(userDto)
      .map(user -> new ResponseEntity<>(user, HttpStatus.CREATED));
  }
  
  @GetMapping("/authentication/secure/tokens")
  public Mono<ResponseEntity<TokenDto>> getToken(){
    return this.appService.getToken()
      .map(token -> new ResponseEntity<>(token, HttpStatus.OK));
  }
  
  @PostMapping(path = "/authentication/unsecure/tokens/validate",
  consumes = MediaType.TEXT_PLAIN_VALUE)
  public Mono<ResponseEntity<Void>> validateToken(@RequestBody String token){
    return this.appService.validateToken(token)
      .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
  }
}
