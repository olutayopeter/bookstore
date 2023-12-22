package com.interswitch.userservice.service.impl;


import com.interswitch.userservice.dto.request.UserRequest;
import com.interswitch.userservice.dto.response.UserResponse;
import com.interswitch.userservice.entity.User;
import com.interswitch.userservice.exception.UserException;
import com.interswitch.userservice.repository.UserRepository;
import com.interswitch.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserResponse createUser(UserRequest userRequest) {
        log.info("Creating user.....");
        // Check if the username is already taken
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new UserException("Username is already taken");
        }

        User user = convertToEntity(userRequest);
        // Encrypt the password before saving to the database
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user = userRepository.save(user);
        return convertToDTO(user);
    }


    @Override
    public UserResponse getUserById(Long id) {
        // Retrieve user from the database by ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found"));

        // Convert the User entity to UserDTO and return
        return convertToDTO(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        // Retrieve user from the database by ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found"));

        // Update user details
        user.setFullName(userRequest.getFullName());
        user.setUsername(userRequest.getUsername());

        // Save the updated user to the database
        user = userRepository.save(user);

        // Convert the updated User entity back to UserDTO and return
        return convertToDTO(user);
    }

    @Override
    public void deleteUser(Long id) {
        // Delete user from the database by ID
        userRepository.deleteById(id);
    }


    private User convertToEntity(UserRequest userDTO) {
        if (userDTO == null) {
            throw new UserException("UserDTO cannot be null");
        }
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }

    private UserResponse convertToDTO(User user) {
        if (user == null) {
            throw new UserException("User cannot be null");
        }
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        return userResponse;
    }

}
