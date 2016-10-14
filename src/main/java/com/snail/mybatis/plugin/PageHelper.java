package com.snail.mybatis.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;

import com.snail.mybatis.entity.PageParams;

@Intercepts({@Signature(type=StatementHandler.class,method="prepare",args={Connection.class,Integer.class})})
public class PageHelper implements Interceptor {
	private Integer defaultPage;
	private Integer defaultPageSize;
	private Boolean defaultUseFlag;
	private Boolean defaultCheckFlag;

	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler handler=(StatementHandler) invocation.getTarget();
		MetaObject metaObject = SystemMetaObject.forObject(handler);
		//分离对象链
		while(metaObject.hasGetter("h")){
			Object object = metaObject.getValue("h");
			metaObject=SystemMetaObject.forObject(object);
		}
		//得到原始对象
		while(metaObject.hasGetter("target")){
			Object object = metaObject.getValue("target");
			metaObject=SystemMetaObject.forObject(object);
		}
		//获取执行的sql语句
		String sql=(String) metaObject.getValue("delegate.boundSql.sql");
		//检测是否是select语句
		if(!checkSelect(sql)){
			return invocation.proceed();
		}
		//获取bundSql对象
		BoundSql boundSql=(BoundSql) metaObject.getValue("delegate.boundSql");
		//获取参数对象
		Object parameterObject = boundSql.getParameterObject();
		//分离分页参数
		PageParams pageParams=getPageParams(parameterObject);
		if(pageParams==null){
			return invocation.proceed();
		}
		//获取分页参数
		Integer page=pageParams.getPage()==null?this.defaultPage:pageParams.getPage();
		Integer pageSize=pageParams.getPageSize()==null?this.defaultPageSize:pageParams.getPageSize();
		Boolean useFlag=pageParams.getUseFlag()==null?this.defaultUseFlag:pageParams.getUseFlag();
		Boolean checkFlag=pageParams.getCheckFlag()==null?this.defaultCheckFlag:pageParams.getCheckFlag();
		//不适用插件
		if(!useFlag){
			return invocation.proceed();
		}
		//获取记录总数
		int total=getTotal(invocation,metaObject,boundSql);
		//回填总数到分页参数
		setTotalToPageParams(pageParams,total,pageSize);
		//检查当前页码的有效性
		checkPage(checkFlag,page,pageParams.getTotalPage());
		return chageSql(invocation,metaObject,boundSql,page,pageSize);
	}

	private Object chageSql(Invocation invocation, MetaObject metaObject, BoundSql boundSql, Integer page,
			Integer pageSize) throws Exception {
		String sql=(String) metaObject.getValue("delegate.boundSql.sql");
		//添加分页参数
		String pageSql="select * from ("+sql+") $_pagin_table limit ?,?";
		//修改执行的sql语句
		metaObject.setValue("delegate.boundSql.sql", pageSql);
		//最终调用的是StatementHandler的prepare方法
		PreparedStatement ps = (PreparedStatement) invocation.proceed();
		//设置最后两个参数
		int count = ps.getParameterMetaData().getParameterCount();
		ps.setInt(count-1, (page-1)*pageSize);
		ps.setInt(count, pageSize);
		return ps;
	}

	private void checkPage(Boolean checkFlag, Integer page, Integer totalPage) throws Throwable {
		if(checkFlag){
			if(page>totalPage){
				throw new Exception("查询失败,查询页码【"+page+"】大于总页数【"+totalPage+"】");
			}
		}
	}

	private void setTotalToPageParams(PageParams pageParams, int total, Integer pageSize) {
		pageParams.setTotal(total);
		int totalPage=total%pageSize==0?total/pageSize:total/pageSize+1;
		pageParams.setTotalPage(totalPage);
	}

	private int getTotal(Invocation invocation, MetaObject metaObject, BoundSql boundSql) throws Throwable {
		//获取当前的mappedStatement对象
		MappedStatement mappedStatement=(MappedStatement) metaObject.getValue("delegate.mappedStatement");
		//获取配置对象
		Configuration configuration = mappedStatement.getConfiguration();
		//获取原始的sql语句
		String sql=(String) metaObject.getValue("delegate.boundSql.sql");
		//修改 为统计的sql语句
		String countSql="select count(*) as total from ("+sql+") $_paging";
		//获取拦截的参数connection
		Connection connection = (Connection) invocation.getArgs()[0];
		PreparedStatement ps=null;
		int total=0;
		try{
			//处理预处理sql
			ps=connection.prepareStatement(countSql);
			//构建总条数的BoundSql对象
			BoundSql countBoundSql=new BoundSql(configuration, countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
			//构建ParameterHandler对象
			ParameterHandler parameterHandler=new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), countBoundSql);
			//设置参数
			parameterHandler.setParameters(ps);
			//执行查询
			ResultSet resultSet = ps.executeQuery();
			while(resultSet.next()){
				total=resultSet.getInt("total");
			}
		}finally{
			if(ps!=null){
				ps.close();
			}
		}
		return total;
	}

	@SuppressWarnings("unchecked")
	private PageParams getPageParams(Object parameterObject) {
		if(parameterObject==null){
			return null;
		}
		PageParams pageParams=null;
		if(parameterObject instanceof Map){
			Map<String,Object> paramMap=(Map<String, Object>) parameterObject;
			Set<String> keySet = paramMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			while(iterator.hasNext()){
				String key=iterator.next();
				Object value = paramMap.get(key);
				if(value instanceof PageParams){
					return (PageParams) value;
				}
			}
		}else if(parameterObject instanceof PageParams){
			pageParams=(PageParams) parameterObject;
		}
		return pageParams;
	}

	private boolean checkSelect(String sql) {
		int index=sql.trim().toLowerCase().indexOf("select");
		return index==0;
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
		if(properties!=null){
			String strDefaultPage=properties.getProperty("page", "1");
			String strDefaultPageSize=properties.getProperty("pageSize","10");
			String strDefaultUseFlag=properties.getProperty("useFlag", "flase");
			String strdefaultCheckFlag=properties.getProperty("checkFlag", "flase");
			
			this.defaultPage=Integer.parseInt(strDefaultPage);
			this.defaultPageSize=Integer.parseInt(strDefaultPageSize);
			this.defaultUseFlag=Boolean.parseBoolean(strDefaultUseFlag);
			this.defaultCheckFlag=Boolean.parseBoolean(strdefaultCheckFlag);
		}
	}

}
