package com.interswitch.userservice.service;

import com.interswitch.userservice.dto.request.UserRequest;
import com.interswitch.userservice.dto.response.UserResponse;

public interface UserService {

    UserResponse createUser(UserRequest userRequest);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserRequest userRequest);
    void deleteUser(Long id);

}
