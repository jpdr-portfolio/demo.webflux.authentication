package com.jpdr.apps.demo.webflux.authentication.util;

import com.jpdr.apps.demo.webflux.authentication.helper.LoginUserDetails;
import com.jpdr.apps.demo.webflux.authentication.model.LoginUser;
import com.jpdr.apps.demo.webflux.authentication.service.dto.LoginUserDto;
import com.jpdr.apps.demo.webflux.authentication.service.dto.TokenDto;
import com.jpdr.apps.demo.webflux.authentication.service.mapper.LoginUserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class TestDataGenerator {
  
  public static final String CREATION_DATE = "2024-10-14T10:39:45.732446-03:00";
  public static final String EMAIL = "johnsmith1@mail.com";
  public static final String EMAIL_PREFIX = "johnsmith";
  public static final String EMAIL_SUFIX = "@mail.com";
  public static final String PASSWORD = "1234";
  public static final String EXPIRATION = "3600000";
  public static final String SECRET = "QyQHaOEvKaU+lIaHHaAPYiSGq/6Dyh5AVJ+niudq1HE=";
  
  
  public static String getEncodedPassword(){
    return getEncodedPassword(PASSWORD);
  }
  
  public static String getEncodedPassword(String password){
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    return "{bcrypt}" + encoder.encode(password);
  }
  
  
  
  public static String getBasicAuthorization(){
    return getBasicAuthorization(EMAIL, PASSWORD);
  }
  
  public static String getBasicAuthorization(String username, String password){
    byte[] bytes = (username+":"+password).getBytes(StandardCharsets.UTF_8);
    String base64Token = Encoders.BASE64.encode(bytes);
    return "Basic " + base64Token;
  }
  
  
  
  public static Claims getClaims(String token){
    return Jwts.parser()
      .verifyWith(getKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }
  
  private static SecretKey getKey(){
    byte[] bytes = Decoders.BASE64.decode(SECRET);
    return Keys.hmacShaKeyFor(bytes);
  }
  
  
  
  public static TokenDto getTokenDto(){
    return TokenDto.builder()
      .data(getToken())
      .build();
  }
  
  public static String getToken(){
    return Jwts.builder()
      .subject(EMAIL)
      .claim("roles", "USER")
      .issuedAt(new Date())
      .expiration(new Date(new Date().getTime() + Long.parseLong(EXPIRATION)))
      .signWith(getKey())
      .compact();
  }
  
  
  public static LoginUserDto getNewLoginUser(){
    return LoginUserDto.builder()
      .id(null)
      .username(EMAIL_PREFIX + "1" + EMAIL_SUFIX)
      .password(PASSWORD)
      .isActive(null)
      .creationDate(null)
      .deletionDate(null)
      .build();
  }
  
  
  public static List<LoginUserDto> getLoginUserDtos(){
    return getList(TestDataGenerator::getLoginUserDto);
  }
  
  public static LoginUserDto getLoginUserDto(){
    return getLoginUserDto(1);
  }

  public static LoginUserDto getLoginUserDto(int userId){
    return LoginUserMapper.INSTANCE.entityToDto(getLoginUser(userId));
  }
  
  public static List<LoginUser> getLoginUsers(){
    return getList(TestDataGenerator::getLoginUser);
  }
  
  public static LoginUser getLoginUser(){
    return getLoginUser(1);
  }
  
  public static LoginUser getLoginUser(int userId){
    return LoginUser.builder()
      .id(userId)
      .username(EMAIL_PREFIX + "1" + EMAIL_SUFIX)
      .password(getEncodedPassword())
      .isActive(true)
      .creationDate(OffsetDateTime.parse(CREATION_DATE))
      .deletionDate(null)
      .build();
  }
  
  public static UserDetails getUserDetails(){
    return new LoginUserDetails(getLoginUser());
  }
  
  
  private static <R> List<R> getList(Function<Integer,R> function){
    return Stream.iterate(1, n -> n + 1)
      .limit(3)
      .map(function)
      .toList();
  }
  
}
