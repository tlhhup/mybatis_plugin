package com.snail.mybatis.mapper;

import com.snail.mybatis.entity.Callable;
import com.snail.mybatis.entity.User;

public interface UserMapper extends BaseMapper<User> {

	void deleteUserInfoById(int id);

	User findUserInfo(User user);

	/**
	 * 调用存储过程
	 * @param result 用于接收存储过程的输出参数  只能通过包装类来存储数据
	 */
	void callGetUserCount(Callable result);
	
}
