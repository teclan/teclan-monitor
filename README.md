
# elasticsearch 状态查询

## 查看整个集群当前的运行状态API接口：
```
GET _cluster/stats 
```

## 指定node运行状态 API接口：

```
GET  _nodes/stats （或者指定node获取 _nodes/node1,node2/stats）
```

详情参见 `elasticsearch性能指标的部分解释.md`

# ActiveMQ 状态查询 
 
 监控 activemq 各个`queue`和`topic`的状态，包括剩余数据量，消费者数量，出队数量，
 
 需要activemq支持远程监控，配置如下：
 
  修改conf目录下的activemq.xml文件的 managementContext 节点，
  
 配置 `connectorHost`可以不配，配了好像不起作用，后续研究。
 
```  
<managementContext>  
			<managementContext createConnector="true" connectorHost="10.0.0.134" connectorPort="1099" 				connectorPath="/jmxrmi" jmxDomainName="org.apache.activemq"/>  
		</managementContext>  
```

特别注意

```
 # ActiveMQ 配置
   mq {
   
   # 是否监控  MQ
   enable=true
   
   ip="10.0.0.134"
   
   connectorPort=1099
   
   connectorPath="/jmxrmi"
   
   # 必须与activemq.xml中的jmxDomainName一致
   jmxDomainName="org.apache.activemq"
   
   # 必须与activemq.xml中   broker 节点的 brokerName一致
   brokerName="BROKER1" 
   
   # 需要监控的 queue
   # queues=["LC_TEST","LC_TEST_FEEDBACK","fromSignalCollect"]
   # 监听所以队列
   queues=[]
   
   # 需要监控的 topic
   topics=["ActiveMQ.Advisory.Consumer.Queue.LC_TEST_FEEDBACK"]
   } 
```

# 内存和CPU

 内存和CPU目前均通过`elasticsearch`的状态查询接口获取。项目中已经集成 `sigar`,但还不够完善，后续需要查询每个
 
 进程的资源消耗情况，则换成`sigar`。
 
 # 数据库
 
 本程序涉及到的数据库相关的脚步如下:
 
 ```
 CREATE TABLE cpu_status (
  id 		INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '自增主键',
  cpu 		VARCHAR(32) COMMENT 'CPU 名称',
  percent	VARCHARACTER(8) COMMENT '使用占比',  
  update_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
 )COMMENT 'CPU 信息';
  
 CREATE TABLE memory_status (
  id 			INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '自增主键',  
  total_memory		VARCHAR(8) COMMENT '物理内存大小',
  use_memory		VARCHAR(8) COMMENT '已使用物理内存大小',
  use_percent		VARCHAR(8) COMMENT '已使用物理内存占比',
  free_memory		VARCHAR(8) COMMENT '剩余物理内存大小',
  free_percent		VARCHAR(8) COMMENT '剩余物理内存占比',
  total_swap		VARCHAR(8) COMMENT '交换空间大小',
  use_swap		VARCHAR(8) COMMENT '已使用交换空间大小',
  use_swap_percent	VARCHAR(8) COMMENT '已使用交换空间占比',
  freel_swap		VARCHAR(8) COMMENT '剩余交换空间大小',
  freel_swap_percent	VARCHAR(8) COMMENT '剩余交换空间占比',  
  update_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
 )COMMENT '内存信息';
 

 CREATE TABLE es_status(
 id 			INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '自增主键',
 address		VARCHAR(256) COMMENT '地址',
 docs_count		INT(11) COMMENT '文档数量',
 docs_delete		INT(11) COMMENT '删除文档数量',
 store_size		VARCHAR(256) COMMENT '存储空间大小',
 store_throttle_time	VARCHAR(256) COMMENT '限流时间',
 search_query_total     INT(11) COMMENT '查询总量',
 search_query_time	VARCHAR(256) COMMENT '查询总耗时',
 search_query_current	INT(11) COMMENT '正在处理的查询量',
 search_fetch_total	INT(11) COMMENT '查询的第二阶段fetch总量',
 search_fetch_time	VARCHAR(256) COMMENT 'fetch耗时',
 search_fetch_current	INT(11) COMMENT '在处理的fetch数量',
 index_total		INT(11) COMMENT '索引总量',
 index_time		VARCHAR(256) COMMENT '索引耗时',
 index_current 		INT(11) COMMENT '正在索引数量',
 refresh_total 		VARCHAR(256) COMMENT '刷新内存总量',
 refresh_total_time	VARCHAR(256) COMMENT '刷新内存耗时',
 flush_total		VARCHAR(256) COMMENT '同步磁盘总量',
 flush_total_time	VARCHAR(256) COMMENT '同步磁盘耗时' ,
 query_cache_size 	VARCHAR(256) COMMENT '查询缓存大小' ,		
 query_cache_evictions  VARCHAR(256) COMMENT '查询缓存剔除大小' ,
 heap_used		VARCHAR(256) COMMENT '已使用堆大小',
 heap_percent		VARCHAR(256) COMMENT '已使用堆占比',
 heap_committed 	VARCHAR(256) COMMENT '承诺堆大小',
 heap_max		VARCHAR(256) COMMENT '最大堆大小',
 non_heap_used		VARCHAR(256) COMMENT '未堆使用大小',
 non_heap_committed	VARCHAR(256) COMMENT '未堆承诺大小',
 tsp_server_open	INT(11) COMMENT 'TCP连接数',
 tsp_rx_count		INT(11) COMMENT '接收的数据包总数',
 tsp_rx_size		VARCHAR(256) COMMENT '接收的数据的总大小',
 tsp_tx_count		INT(11) COMMENT '发送的数据包总数',
 tsp_tx_size		VARCHAR(256) COMMENT '发送的数据的总大小',
 http_current_open	INT(11) COMMENT 'HTTP连接数',
 http_total_opened	INT(11) COMMENT 'HTTP总连接数',
 update_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
 )COMMENT 'ES 状态信息';
 
 CREATE TABLE mq_status (
  id 			INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '自增主键',
  ip			VARCHAR(32) COMMENT 'IP地址',
  NAME  		VARCHAR(256) COMMENT '名称',
  TYPE 			VARCHAR(8) COMMENT '类型，队列或广播',
  size			INT(11) COMMENT '剩余大小', 
  consumer_count 	INT(11) COMMENT '消费者数量',
  dequeue_count 	INT(11) COMMENT '出队数量',  
  update_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
 )COMMENT 'MQ 状态信息';
 ```
  
