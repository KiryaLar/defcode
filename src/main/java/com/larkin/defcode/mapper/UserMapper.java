package com.larkin.defcode.mapper;

import com.larkin.defcode.dto.request.RegisterUserRequest;
import com.larkin.defcode.dto.response.UserResponse;
import com.larkin.defcode.entity.Role;
import com.larkin.defcode.entity.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Mapping(source = "role", target = "role", qualifiedByName = "stringToRole")
    @Mapping(source = "password", target = "password", qualifiedByName = "encodePassword")
    public abstract User dtoToEntity(RegisterUserRequest userDto, @Context PasswordEncoder passwordEncoder);

    public abstract UserResponse entityToDto(User user);

    @Named("stringToRole")
    public Role stringToRole(String roleString) {
        if (roleString == null) {
            return null;
        }
        try {
            return Role.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + roleString);
        }
    }

    @Named("encodePassword")
    public String encodePassword(String password, @Context PasswordEncoder passwordEncoder) {
        if (password == null) {
            return null;
        }
        return passwordEncoder.encode(password);
    }
}

