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
						// 已更新讲解 最终调用的是sqlSession中的update方法
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
		3. 到此看到最终调用的是**sqlSession中的update方法**
		
				//1. 调用DefaultSqlSession中的update方法
				public int update(String statement, Object parameter) {
			    try {
				  // 获取到执行的sql语句的包装对象
			      MappedStatement ms = configuration.getMappedStatement(statement);
				  // 真实的执行操作
			      return executor.update(ms, wrapCollection(parameter));
			      ...
			    }
				//2. 执行executor的update方法(CachingExecutor的update方法)
				public int update(MappedStatement ms, Object parameterObject) throws SQLException {
					// 清除缓存
				    flushCacheIfRequired(ms);
					//执行BaseExecutor的update方法
				    return delegate.update(ms, parameterObject);
				  }
				//3. 执行BaseExecutor的update方法
				  public int update(MappedStatement ms, Object parameter) throws SQLException {
				    ...
					//执行doUpdate方法，该方法为抽象方法
				    return doUpdate(ms, parameter);
				  }
                //4. 根据不同的Executor指定对应的doUpdate方法(默认为SimpleExecutor)
				  public int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
				    Statement stmt = null;
				    try {
				      Configuration configuration = ms.getConfiguration();
					  // 主要看这个方法 请看下面的 1
				      StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null, null);
					  // 预处理语句 此时的stmt为代理对象(由plugin实例)  请看下面的 2
				      stmt = prepareStatement(handler, ms.getStatementLog());
					  // 执行语句
				      return handler.update(stmt);
				    } finally {
				      closeStatement(stmt);
				    }
				  }

				
				  // 1 生成StatementHandler的代理对象
				  public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
             		// 根据路由创建不同的的StatementHandler(但是记住返回的对象还是RoutingStatementHandler的类型)
				    StatementHandler statementHandler = new RoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
					// 主要看这个方法，放回代理对象 情况下面的 3
				    statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
				    return statementHandler;
				  }
				  //3 调用InterceptorChain中的方法
				  public Object pluginAll(Object target) {
				    for (Interceptor interceptor : interceptors) {
					  //调用插件中的plugin方法创建代理对象，对RoutingStatementHandler的实例进行层层封装
				      target = interceptor.plugin(target);
				    }
				    return target;
				  }
				 // 到此StatementHandler的代理对象就创建完毕
				
				//2
			    开始看预处理sql语句-->将触发插件中的方法对sql进行修改
				  private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
				    Statement stmt;
				    Connection connection = getConnection(statementLog);
				    stmt = handler.prepare(connection, transaction.getTimeout());
				    handler.parameterize(stmt);
				    return stmt;
				  }