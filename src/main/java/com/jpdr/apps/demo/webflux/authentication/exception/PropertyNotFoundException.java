package com.jpdr.apps.demo.webflux.authentication.exception;

public class PropertyNotFoundException extends RuntimeException{
  public PropertyNotFoundException(String propertyName){
    super("The property " + propertyName + " wasn't found");
  }
}
