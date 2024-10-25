package com.jpdr.apps.demo.webflux.authentication.exception;

public class InvalidAuthenticationForTokenException extends RuntimeException{
  public InvalidAuthenticationForTokenException(){
    super("Invalid authentication for token");
  }
}
