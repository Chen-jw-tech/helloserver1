package com.stu.helloserver.service.impl;

import com.stu.helloserver.common.Result;
import com.stu.helloserver.dto.UserDTO;

public interface UserService {
    Result<String> register(UserDTO userDTO);
    Result<String> login(UserDTO userDTO);
}