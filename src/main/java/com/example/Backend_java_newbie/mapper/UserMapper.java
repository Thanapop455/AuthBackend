package com.example.Backend_java_newbie.mapper;

import com.example.Backend_java_newbie.domain.dto.user.res.UserRegisterRes;
import com.example.Backend_java_newbie.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserRegisterRes toRegisterResponse(User user);

}
