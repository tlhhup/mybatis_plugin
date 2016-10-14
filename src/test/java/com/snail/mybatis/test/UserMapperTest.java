package com.snail.mybatis.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import com.snail.mybatis.entity.Callable;
import com.snail.mybatis.entity.User;
import com.snail.mybatis.mapper.UserMapper;

public class UserMapperTest {

	@Test
	public void find(){
		SqlSessionFactory sqlSessionFactory = null;
		SqlSession session = null;
		try {
			InputStream inputStream = Resources.getResourceAsStream("mybatis.xml");
			//创建SqlSessionFactory对象
			SqlSessionFactoryBuilder sqlSessionFactoryBuilder=new SqlSessionFactoryBuilder();
			sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);
			//得到sqlSession对象--->connection
			session = sqlSessionFactory.openSession();
			
			UserMapper userMapper = session.getMapper(UserMapper.class);
			User user=new User();
			user.setPage(1);
			user.setPageSize(10);
			List<User> users = userMapper.findEntity(user);
			System.out.println(users.size());
			//提交事务
			session.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(session!=null){
				session.close();
			}
			if(sqlSessionFactory!=null){
				sqlSessionFactory=null;
			}
		}
	}
	
	@Test
	public void update(){
		SqlSessionFactory sqlSessionFactory = null;
		SqlSession session = null;
		try {
			InputStream inputStream = Resources.getResourceAsStream("mybatis.xml");
			//创建SqlSessionFactory对象
			SqlSessionFactoryBuilder sqlSessionFactoryBuilder=new SqlSessionFactoryBuilder();
			sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);
			//得到sqlSession对象--->connection
			session = sqlSessionFactory.openSession();
			
			UserMapper userMapper = session.getMapper(UserMapper.class);
			User user=new User();
			user.setId(2);
			user.setUserName("zhangsan");
			userMapper.updateEntity(user);
			//提交事务
			session.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(session!=null){
				session.close();
			}
			if(sqlSessionFactory!=null){
				sqlSessionFactory=null;
			}
		}
	}
	
	@Test
	public void callableTest(){
		SqlSessionFactory sqlSessionFactory = null;
		SqlSession session = null;
		try {
			InputStream inputStream = Resources.getResourceAsStream("mybatis.xml");
			//创建SqlSessionFactory对象
			SqlSessionFactoryBuilder sqlSessionFactoryBuilder=new SqlSessionFactoryBuilder();
			sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);
			//得到sqlSession对象--->connection
			session = sqlSessionFactory.openSession();
			
			Callable i=new Callable();
			UserMapper userMapper = session.getMapper(UserMapper.class);
			userMapper.callGetUserCount(i);
			
			System.out.println(i.getCount());
			
			//提交事务
			session.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(session!=null){
				session.close();
			}
			if(sqlSessionFactory!=null){
				sqlSessionFactory=null;
			}
		}
	}
	
}
