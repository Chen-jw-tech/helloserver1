package com.stu.helloserver.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stu.helloserver.Entity.User;
import com.stu.helloserver.Entity.UserInfo;          // 假设存在 user_info 表对应的实体
import com.stu.helloserver.common.Result;
import com.stu.helloserver.common.ResultCode;
import com.stu.helloserver.dto.UserDTO;
import com.stu.helloserver.mapper.UserInfoMapper;    // 新增的 Mapper
import com.stu.helloserver.mapper.UserMapper;
import com.stu.helloserver.security.JwtUtil;
import com.stu.helloserver.service.UserService;
import com.stu.helloserver.vo.UserDetailVO;          // 新增的 VO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // 新增：注入 Redis 和自定义 Mapper
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private JwtUtil jwtUtil;

    // 缓存键前缀
    private static final String CACHE_KEY_PREFIX = "user:detail:";

    // ==================== 原有方法保持不变 ====================
    @Override
    public Result<String> register(UserDTO userDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);
        if (dbUser != null) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        userMapper.insert(user);
        return Result.success("注册成功!");
    }

    public Result<String> login(UserDTO userDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);
        if (dbUser == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        if (!dbUser.getPassword().equals(userDTO.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }
        // 登录成功，生成 JWT 并返回
        String jwt = jwtUtil.generateToken(userDTO.getUsername());
        return Result.success(jwt);
    }

    @Override
    public Result<String> getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        return Result.success("查询成功，用户ID: " + user.getId() + ", 用户名: " + user.getUsername());
    }

    @Override
    public Result<Object> getUserPage(Integer pageNum, Integer pageSize) {
        Page<User> pageParam = new Page<>(pageNum, pageSize);
        Page<User> resultPage = userMapper.selectPage(pageParam, null);
        return Result.success(resultPage);
    }

    // ==================== 新增方法：多表联查 + Redis 缓存 ====================

    @Override
    public Result<UserDetailVO> getUserDetail(Long userId) {
        String key = CACHE_KEY_PREFIX + userId;
        // 1. 先查 Redis 缓存
        String json = redisTemplate.opsForValue().get(key);
        if (json != null && !json.isEmpty()) {
            try {
                UserDetailVO cacheVO = JSONUtil.toBean(json, UserDetailVO.class);
                return Result.success(cacheVO);
            } catch (Exception e) {
                // 缓存数据异常（如格式错误），删除脏缓存，继续查数据库
                redisTemplate.delete(key);
            }
        }

        // 2. 查数据库（多表联查）
        UserDetailVO detail = userInfoMapper.getUserDetail(userId);
        if (detail == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 3. 将结果存入 Redis，设置 10 分钟过期
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(detail), 10, TimeUnit.MINUTES);
        return Result.success(detail);
    }

    @Override
    @Transactional
    public Result<String> updateUserInfo(Long userId, UserInfo userInfo) {
        // 1. 更新数据库
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUserId, userId);
        int rows = userInfoMapper.update(userInfo, wrapper);
        if (rows == 0) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        // 2. 删除缓存（保证下次查询拿到最新数据）
        redisTemplate.delete(CACHE_KEY_PREFIX + userId);
        return Result.success("更新成功");
    }

    @Override
    @Transactional
    public Result<String> deleteUser(Long userId) {
        // 1. 删除用户信息（假设先删扩展信息再删主表）
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUserId, userId);
        userInfoMapper.delete(queryWrapper);
        // 2. 删除主表用户
        int rows = userMapper.deleteById(userId);
        if (rows == 0) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        // 3. 删除缓存
        redisTemplate.delete(CACHE_KEY_PREFIX + userId);
        return Result.success("删除成功");
    }
}