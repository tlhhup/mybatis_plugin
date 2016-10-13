# mybatis_plugin
  Mybatis自定义的分页插件，及说明Mapper代理开发的执行流程。

1. Mapper代理开发执行流程
	1. 四大对象
		1. Executor：调度StatementHandler、ParameterHandler、ResultHandler等来执行对应的SQL
		2. StatementHandler：使用数据库的Statement(PreparedStatement)执行操作
		3. ParameterHandler：用户SQL对参数的处理
		4. ResultHandler：进行最后结果集的封装返回处理
	5. 执行流程
		1. 代理的创建
			
				//1. 获取mapper
				session.getMapper(UserMapper.class);
	
				//2. 调用DefaultSqlSession的getMapper方法
				 public <T> T getMapper(Class<T> type) {
				    return configuration.<T>getMapper(type, this);
				 }
				
				...
				//3. 最终调用MapperRegistry的方法
				public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
					// 获取mapper工厂
				    final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
				   ....
				   //创建mapper代理对象
				   return mapperProxyFactory.newInstance(sqlSession);
				   ...
				}
	
				//4. MapperProxyFactory的方法返回代理对象
				public T newInstance(SqlSession sqlSession) {
				    final MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, mapperInterface, methodCache);
				    // 调用内部方法创建代理
				    return newInstance(mapperProxy);
				}
		2. 执行操作

				//1. 执行MapperProxy对象的invoke方法
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				    ... 
				    final MapperMethod mapperMethod = cachedMapperMethod(method);
				    // 执行操作
					return mapperMethod.execute(sqlSession, args);
				  }
				//2. 调用mapperMethod的execute方法

				public Object execute(SqlSession sqlSession, Object[] args) {
			    Object result;
					// 根据不同的类型执行不同的操作
				    if (SqlCommandType.INSERT == command.getType()) {
				      Object param = method.convertArgsToSqlCommandParam(args);
				      result = rowCountResult(sqlSession.insert(command.getName(), param));
				    } else if (SqlCommandType.UPDATE == command.getType()) {
						// 已更新讲解
				      Object param = method.convertArgsToSqlCommandParam(args);
				      result = rowCountResult(sqlSession.update(command.getName(), param));
				    } else if (SqlCommandType.DELETE == command.getType()) {
				      Object param = method.convertArgsToSqlCommandParam(args);
				      result = rowCountResult(sqlSession.delete(command.getName(), param));
				    } else if (SqlCommandType.SELECT == command.getType()) {
				      if (method.returnsVoid() && method.hasResultHandler()) {
				        executeWithResultHandler(sqlSession, args);
				        result = null;
				      } else if (method.returnsMany()) {
				        result = executeForMany(sqlSession, args);
				      } else if (method.returnsMap()) {
				        result = executeForMap(sqlSession, args);
				      } else if (method.returnsCursor()) {
				        result = executeForCursor(sqlSession, args);
				      } else {
				        Object param = method.convertArgsToSqlCommandParam(args);
				        result = sqlSession.selectOne(command.getName(), param);
				      }
				    } else if (SqlCommandType.FLUSH == command.getType()) {
				        result = sqlSession.flushStatements();
				    } 
					...
				    return result;
			    }