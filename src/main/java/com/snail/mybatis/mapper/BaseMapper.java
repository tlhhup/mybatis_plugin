package com.snail.mybatis.mapper;

import java.util.List;

public interface BaseMapper<T> {

	public void saveEntity(T t);

	public void updateEntity(T t);

	public void deleteEntity(T t);
	
	public List<T> findEntity(T t);

}
