package com.jpdr.apps.demo.webflux.authentication.exception;

public class InvalidUsernameException extends RuntimeException{
  public InvalidUsernameException(){
    super("Invalid username");
  }
}
