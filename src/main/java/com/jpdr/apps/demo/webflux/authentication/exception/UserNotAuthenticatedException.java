package com.jpdr.apps.demo.webflux.authentication.exception;

public class UserNotAuthenticatedException extends RuntimeException{
  public UserNotAuthenticatedException(){
    super("The user is not authenticated.");
  }
}
