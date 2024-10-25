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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AppController {

  private final AppService appService;
  
  @GetMapping("/authentication/secure/users")
  public ResponseEntity<Flux<LoginUserDto>> findUsers(
    @RequestParam(name = "userEmail", required = false) String userEmail){
    return new ResponseEntity<>(appService.findUsers(userEmail), HttpStatus.OK);
  }
  
  @PostMapping("/authentication/unsecure/users")
  public ResponseEntity<Mono<LoginUserDto>> createUser(@RequestBody LoginUserDto userDto){
    return new ResponseEntity<>(appService.createUser(userDto), HttpStatus.CREATED);
  }
  
  @GetMapping("/authentication/secure/tokens")
  public ResponseEntity<Mono<TokenDto>> getToken(){
    return new ResponseEntity<>(appService.getToken(), HttpStatus.OK);
  }
  
  @PostMapping(path = "/authentication/unsecure/tokens/validate",
  consumes = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<Mono<Void>> validateToken(@RequestBody String token){
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(appService.validateToken(token));
  }
}
