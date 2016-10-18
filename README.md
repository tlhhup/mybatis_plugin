# mybatis_plugin
  Mybatis自定义的分页插件，及说明Mapper代理开发的执行流程。

1. Mapper代理开发执行流程
	1. 四大对象
		1. Executor：调度StatementHandler、ParameterHandler、ResultHandler等来执行对应的SQL,通过mybatis的配置文件中的defaultExecutorType进行设置
			1. simple：默认执行器(SimpleExecutor)
			2. reuse：一种执行器重用预处理语句
			3. batch：执行器重用语句和批量更新，它是针对批量专用的执行器
		2. StatementHandler：使用数据库的Statement(PreparedStatement)执行操作
			1. RoutingStatementHandler：一个路由，delegate存储最终得到的StatementHandler对象
			2. BaseStatementHandler：
				1. CallableStatementHandler：采用的是java.sql中的CallableStatement(支持存储过程)
				2. PreparedStatementHandler：采用的是java.sql中的PreparedStatement
				3. SimpleStatementHandler：采用的是java.sql中Statement处理(默认)
		3. ParameterHandler：用户SQL对参数的处理(默认实现类：DefaultParameterHandler)
		4. ResultHandler：进行最后结果集的封装返回处理(默认实现类：DefaultResultHandler)
	5. 工具类
		1. MetaObject：可以有效的读取或者修改一些重要对象的属性
			1. forObject：用于包装对象，但不再使用，使用**SystemMetaObject.forObject方法取代**
			2. getValue：获取对象中指定属性的值，支持OGNL
			3. setValue：修改对象的属性，支持OGNL
			4. hasSetter\hasGetter：判断对象是否具有指定属性的get或set方法
		2. Plugin：
			1. wrap：静态方法，用户在插件中使用该方法来方法代理对象
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
				  //插件中的plugin方法
				  public Object plugin(Object target) {
					return Plugin.wrap(target, this);
				  }
				   public static Object wrap(Object target, Interceptor interceptor) {
				    Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);
				    Class<?> type = target.getClass();
				    Class<?>[] interfaces = getAllInterfaces(type, signatureMap);
				    if (interfaces.length > 0) {
					  //返回代理对象
				      return Proxy.newProxyInstance(
				          type.getClassLoader(),
				          interfaces,
				          new Plugin(target, interceptor, signatureMap));
				    }
				    return target;
				  }
				 // 到此StatementHandler的代理对象就创建完毕
				
				//2
			    开始看预处理sql语句-->将触发插件中的方法对sql进行修改
				  private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
				    Statement stmt;
				    Connection connection = getConnection(statementLog);
				    //此时的handler为代理对象
				    stmt = handler.prepare(connection, transaction.getTimeout());
				    handler.parameterize(stmt);
				    return stmt;
				  }
               	// 调用Plugin的invoke方法
				  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				    try {
				      Set<Method> methods = signatureMap.get(method.getDeclaringClass());
					  //具有插件及有需要拦截的方法-->触发插件中的方法(修改sql语句)
				      if (methods != null && methods.contains(method)) {
						// 传入了拦截的方法、参数、代理对象
				        return interceptor.intercept(new Invocation(target, method, args));
				      }
				      return method.invoke(target, args);
				    } catch (Exception e) {
				      throw ExceptionUtil.unwrapThrowable(e);
				    }
				  }
			    //下面看interceptor中的方法
				public Object intercept(Invocation invocation) throws Throwable {
					//取出拦截对象
					StatementHandler handler=(StatementHandler) invocation.getTarget();
					MetaObject metaObject = SystemMetaObject.forObject(handler);
					//分离代理对象链
					while(metaObject.hasGetter("h")){
						Object object = metaObject.getValue("h");
						metaObject=SystemMetaObject.forObject(object);
					}
					//得到原始对象(及第一次插件在产生代理的时候Plugin中target存放的是最原始的对象)
					while(metaObject.hasGetter("target")){
						Object object = metaObject.getValue("target");
						metaObject=SystemMetaObject.forObject(object);
					}
					String sql=(String) metaObject.getValue("delegate.boundSql.sql");
					System.out.println("执行的sql语句为："+sql);
					//继续往下面执行-->该方法相当重要
					return invocation.proceed();
				}
				// 调用invocation的proceed方法-->有它来执行目标对象执行的方法
				  public Object proceed() throws InvocationTargetException, IllegalAccessException {
					// 值得赋值时在创建intercept方法参数是已经封装好了--->将一层层返回最后调用目标对象的方法
				    return method.invoke(target, args);
				  }
				// 到此Mapper的一个执行流程完毕	
3. 插件开发流程
	1. 确定需要拦截的对象(四大对象)：StatementHandler为最常用的拦截对象
	2. 去顶拦截的方法和参数
	3. 实现拦截方法
		1. 编写插件实现Interceptor接口
		2. 添加注解指定拦截对象、方法及参数

				@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class ,Integer.class}) })
				public class QueryLimitPlugin implements Interceptor {
				 ....
				}
		3. 实现拦截方法
			
				//改变执行的sql语句
				public Object intercept(Invocation invocation) throws Throwable {
					//取出拦截对象
					StatementHandler handler=(StatementHandler) invocation.getTarget();
					MetaObject metaObject = SystemMetaObject.forObject(handler);
					//分离代理对象代理链
					while(metaObject.hasGetter("h")){
						Object object = metaObject.getValue("h");
						metaObject=SystemMetaObject.forObject(object);
					}
					//取出最后的代理对象及原始的四大对象
					while(metaObject.hasGetter("target")){
						Object object = metaObject.getValue("target");
						metaObject=SystemMetaObject.forObject(object);
					}
					// 获取指定的sql语句
					String sql=(String) metaObject.getValue("delegate.boundSql.sql");
					System.out.println("执行的sql语句为："+sql);
					return invocation.proceed();
				}
			
				//返回代理对象
				public Object plugin(Object target) {
					return Plugin.wrap(target, this);
				}
			
				//获取mybatis中配置的插件的属性
				public void setProperties(Properties properties) {
					if(properties!=null){
						this.limit=Integer.parseInt(properties.getProperty("limit"));
						this.dbType=properties.getProperty("dbType");
					}
				}
		4. 配置mybatis的配置文件

				<plugins>
			        <plugin interceptor="com.snail.mybatis.plugin.TestPlugin">
			            
			        </plugin>
			        <plugin interceptor="com.snail.mybatis.plugin.QueryLimitPlugin">
			            <!-- 设置属性 -->
						<property name="limit" value="50"/>
			            <property name="dbType" value="mysql"/>
			        </plugin>
			    </plugins>
5. 调用存储过程
	1. 编写接口方法

			/**
			 * 调用存储过程
			 * @param result 用于接收存储过程的输出参数  只能通过包装类来存储数据
			 */
			void callGetUserCount(Callable result);
	2. 编写映射文件

			<!-- 调用存储过程 -->
		    <select id="callGetUserCount" parameterType="callable" statementType="CALLABLE">
		        CALL countUsers(#{count,mode=OUT,jdbcType=INTEGER});
		    </select>
	3. 说明
		1. 映射文件中的mode属性值在ParameterMode中定义的为枚举数据类型，jdbcType是在JdbcType中定义的也是枚举类型，**statementType必须指定**
		2. 为啥只能通过实体对象来存储数据?

			 	//最终调用的是CallableStatementHandler中的query方法，定义如下
				  public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
				    CallableStatement cs = (CallableStatement) statement;
				    cs.execute();
					//为空，存储过程没有返回值
				    List<E> resultList = resultSetHandler.<E>handleResultSets(cs);
					//将数据进行绑定
				    resultSetHandler.handleOutputParameters(cs);
				    return resultList;
				  }
				//数据绑定
				  public void handleOutputParameters(CallableStatement cs) throws SQLException {
					//获取参数对象
				    final Object parameterObject = parameterHandler.getParameterObject();
					//得到包装类MetaObject
				    final MetaObject metaParam = configuration.newMetaObject(parameterObject);
					//获取参数映射
				    final List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
				    for (int i = 0; i < parameterMappings.size(); i++) {
				      final ParameterMapping parameterMapping = parameterMappings.get(i);
				      if (parameterMapping.getMode() == ParameterMode.OUT || parameterMapping.getMode() == ParameterMode.INOUT) {
				        if (ResultSet.class.equals(parameterMapping.getJavaType())) {
				          handleRefCursorOutputParameter((ResultSet) cs.getObject(i + 1), parameterMapping, metaParam);
				        } else {
				          final TypeHandler<?> typeHandler = parameterMapping.getTypeHandler();
						  //这个最重要：调用的是包装对象(MetaObject)的setValue方法对指定的属性进行赋值
				          metaParam.setValue(parameterMapping.getProperty(), typeHandler.getResult(cs, i + 1));
				        }
				      }
				    }
				  }
4. 总结
	1. 从以上的执行流程可以看出，加入的插件是对StatementHandler的prepare方法进行拦截，故在newStatementHandler方法的时候先生成的目录对象是一个**RoutingStatementHandler**类型的
	2. **RoutingStatementHandler中的delegate属性**是对最终得到的StatementHandler对象的存储属性
	3. **在StatementHandler中的BoundSql属性**存放执行的sql语句和所有的参数
	4. **invocation中的proceed方法**必须在插件中继续调用，否则将改变mybatis的底层实现
	5. **插件中代理对象的说明**
		1. 后面的插件会对上一层产生的代理对象在进行一次包装，生成带h属性的代理对象，并且**该层的目标对象为上一层所生成的代理对象**
		2. **metaObject.getValue("target")**获取的是上层插件生成的代理对象(该对象可能还是一个代理并非最原始的对象，也有可能就是原始对象)
		3. 在对目标对象进行操作时必须明确知道当前获取的目标对象到底是代理对象还是原始对象，可以通过当前的插件是处于第几层来区分(只有在第二层的时候获取的目标对象就是原始对象)