package com.stu.helloserver.controller;

import com.stu.helloserver.common.Result;
import com.stu.helloserver.dto.UserDTO;
import com.stu.helloserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // POST 请求 - 新增用户（注册）
    @PostMapping
    public Result<String> register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    // POST 请求 - 登录（放行接口）
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO);
    }

    // GET 请求 - 根据ID查询用户
    @GetMapping("/{id}")
    public Result<String> getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    // DELETE 请求 - 删除用户（需要鉴权）
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable("id") Long id) {
        // 可选实现删除逻辑
        String data = "删除用户成功，ID: " + id;
        return Result.success(data);
    }

    // PUT 请求 - 更新用户（需要鉴权）
    @PutMapping("/{id}")
    public Result<String> updateUser(@PathVariable("id") Long id, @RequestBody(required = false) UserDTO userDTO) {
        // 可选实现更新逻辑
        String data = "更新用户成功，ID: " + id;
        return Result.success(data);
    }
}