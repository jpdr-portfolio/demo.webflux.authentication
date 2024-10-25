package com.jpdr.apps.demo.webflux.authentication.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Data
@Table("login_user")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginUser {
  
  @Id
  @Column("id")
  Integer id;
  @Column("username")
  String username;
  @Column("password")
  String password;
  @Column("is_active")
  Boolean isActive;
  @Column("creation_date")
  OffsetDateTime creationDate;
  @Column("deletion_date")
  OffsetDateTime deletionDate;

}
