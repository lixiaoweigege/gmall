package com.atguigu.gmall.user.mapper;

import com.atguigu.gmall.bean.UserBean;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    UserBean getUerById(@Param("id") String id);
}
