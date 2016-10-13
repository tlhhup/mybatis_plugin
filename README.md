# mybatis_plugin
  Mybatis自定义的分页插件，及说明Mapper代理开发的执行流程。

1. Mapper代理开发执行流程
	1. 四大对象
		1. Executor：调度StatementHandler、ParameterHandler、ResultHandler等来执行对应的SQL
		2. StatementHandler：使用数据库的Statement(PreparedStatement)执行操作
		3. ParameterHandler：用户SQL对参数的处理
		4. ResultHandler：进行最后结果集的封装返回处理
