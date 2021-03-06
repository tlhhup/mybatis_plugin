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

@Intercepts({
		@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class TestPlugin implements Interceptor {

	public Object intercept(Invocation invocation) throws Throwable {
		// 取出拦截对象
		StatementHandler handler = (StatementHandler) invocation.getTarget();
		MetaObject metaObject = SystemMetaObject.forObject(handler);
		while (metaObject.hasGetter("h")) {
			Object object = metaObject.getValue("h");
			metaObject = SystemMetaObject.forObject(object);
		}
		//此时获取的目标对象即为为原始对象
		while (metaObject.hasGetter("target")) {
			Object object = metaObject.getValue("target");
			metaObject = SystemMetaObject.forObject(object);
		}
		String sql = (String) metaObject.getValue("delegate.boundSql.sql");
		System.out.println("执行的sql语句为：" + sql);
		return invocation.proceed();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {

	}

}
