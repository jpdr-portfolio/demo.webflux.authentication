package com.jpdr.apps.demo.webflux.authentication.helper;

import com.jpdr.apps.demo.webflux.authentication.model.LoginUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class LoginUserDetails implements UserDetails {
  
  private String username;
  private String password;
  
  public LoginUserDetails(LoginUser loginUser){
    this.username = loginUser.getUsername();
    this.password = loginUser.getPassword();
  }
  
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of( () -> "USER");
  }
  
  
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }
  
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }
  
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }
  
  @Override
  public boolean isEnabled() {
    return true;
  }
  
  
}
