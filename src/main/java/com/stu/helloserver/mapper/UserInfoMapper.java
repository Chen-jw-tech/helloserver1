package com.stu.helloserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stu.helloserver.Entity.UserInfo;
import com.stu.helloserver.vo.UserDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    @Select("""
    SELECT
        u.id AS user_id,
        u.username,
        i.real_name,
        i.phone,
        i.address
    FROM sys_user u
    LEFT JOIN user_info i ON u.id = i.user_id
    WHERE u.id = #{userId}
""")
    UserDetailVO getUserDetail(@Param("userId") Long userId);
}