package com.snail.mybatis.mapper;

import com.snail.mybatis.entity.User;

public interface UserMapper extends BaseMapper<User> {

	void deleteUserInfoById(int id);

	User findUserInfo(User user);

}
