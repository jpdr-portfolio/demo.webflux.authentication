package com.jpdr.apps.demo.webflux.authentication.service.mapper;

import com.jpdr.apps.demo.webflux.authentication.model.LoginUser;
import com.jpdr.apps.demo.webflux.authentication.service.dto.LoginUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", imports = java.util.Objects.class)
public interface LoginUserMapper {
  
  LoginUserMapper INSTANCE = Mappers.getMapper(LoginUserMapper.class);
  
  LoginUserDto entityToDto(LoginUser entity);
  
}
