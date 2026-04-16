package com.stu.helloserver.Entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_info")          // 对应数据库表名
public class UserInfo {

    @TableId
    private Long id;              // 主键

    private Long userId;         // 外键，关联 sys_user.id

    private String realName;     // 对应数据库字段 real_name

    private String phone;

    private String address;
}