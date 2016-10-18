package com.snail.mybatis.plugin;

import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class ,Integer.class}) })
public class QueryLimitPlugin implements Interceptor {
	
	private int limit;
	private String dbType;
	private static final String LMT_TABLE_NAME="lmt_Table_Name_xxx";

	public Object intercept(Invocation invocation) throws Throwable {
		//取出拦截对象
		StatementHandler handler=(StatementHandler) invocation.getTarget();
		MetaObject metaObject = SystemMetaObject.forObject(handler);
		while(metaObject.hasGetter("h")){
			Object object = metaObject.getValue("h");
			metaObject=SystemMetaObject.forObject(object);
		}
		//执行到该位置是获取到的target其实是TestPlugin生成的代理对象
		while(metaObject.hasGetter("target")){
			Object object = metaObject.getValue("target");
			metaObject=SystemMetaObject.forObject(object);
		}
		//处理该获取不到delegate属性,因为此处获取到的还是代理对象，并非是原始对象 故报错
//		String sql=(String) metaObject.getValue("delegate.boundSql.sql");
//		System.out.println("执行的sql语句为："+sql);
		return invocation.proceed();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
		if(properties!=null){
			this.limit=Integer.parseInt(properties.getProperty("limit"));
			this.dbType=properties.getProperty("dbType");
		}
	}

}
