package com.stu.helloserver.controller;

import com.stu.helloserver.Entity.User;
import com.stu.helloserver.common.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // 1. 获取用户信息（查）
    @GetMapping("/{id}")
    public String getUser(@PathVariable("id") Long id) {
        return "查询成功，正在返回 ID 为 " + id + " 的用户信息";
    }

    // 2. 新增用户（增）- 接收 JSON 格式数据
    @PostMapping
    public String createUser(@RequestBody User user) {
        return "新增成功，接收到用户：" + user.getName() + "，年龄：" + user.getAge();
    }

    // 3. 全量更新用户信息（改）
    @PutMapping("/{id}")
    public String updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        return "更新成功，ID " + id + " 的用户已修改为：" + user.getName();
    }

    // 4. 删除用户（删）
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        return "删除成功，已移除 ID 为 " + id + " 的用户";
    }
}