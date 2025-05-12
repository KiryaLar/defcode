package com.larkin.defcode.service;

import com.larkin.defcode.dao.UserDao;
import com.larkin.defcode.dto.response.UserResponse;
import com.larkin.defcode.dto.request.RegisterUserRequest;
import com.larkin.defcode.entity.User;
import com.larkin.defcode.exception.AlreadyExistsException;
import com.larkin.defcode.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegisterUserRequest userDto) {
        if (userDto.getRole().equalsIgnoreCase("ADMIN")) {
            log.info("Registering user is an admin");
            List<User> users = userDao.findByRole(userDto.getRole().toUpperCase());
            if (!users.isEmpty()) {
                log.error("Admin already exists");
                AlreadyExistsException.admin();
            }
        }

        Optional<User> maybeUser = userDao.findByUsername(userDto.getUsername());
        if (maybeUser.isPresent()) {
            log.error("User with username: {} already exists", userDto.getUsername());
            AlreadyExistsException.user(userDto.getUsername());
        }

        log.debug("Mapping dto to user: {}", userDto);
        User user = userMapper.dtoToEntity(userDto, passwordEncoder);
        userDao.createUser(user);
    }

    public List<UserResponse> getNonAdminUsers() {
        return userDao.findByRole("USER").stream().map(userMapper::entityToDto).toList();
    }


    public void deleteUser(Integer id) {
        userDao.deleteById(id);
    }
}
