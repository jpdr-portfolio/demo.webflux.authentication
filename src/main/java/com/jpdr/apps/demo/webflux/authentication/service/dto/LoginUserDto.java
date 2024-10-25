package com.jpdr.apps.demo.webflux.authentication.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginUserDto {
  
  @JsonInclude(Include.NON_NULL)
  Integer id;
  @JsonInclude(Include.NON_NULL)
  String username;
  @JsonInclude(Include.NON_NULL)
  String password;
  @JsonInclude(Include.NON_NULL)
  Boolean isActive;
  @JsonInclude(Include.NON_EMPTY)
  OffsetDateTime creationDate;
  @JsonInclude(Include.NON_EMPTY)
  OffsetDateTime deletionDate;
  
}
