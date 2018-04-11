
# elasticsearch 状态查询

## 查看整个集群当前的运行状态API接口：
```
GET _cluster/stats 
```

## 指定node运行状态 API接口：

```
GET  _nodes/stats （或者指定node获取 _nodes/node1,node2/stats）
```


### 查询性能指标，前缀indices.search.*的指标
```
    indices.search.query_total 查询总量
    indices.search.query_time_in_millis 查询总耗时
    indices.search.query_current 正在处理的查询量
    indices.search.fetch_total 查询的第二阶段fetch总量
    indices.search.fetch_time_in_millis fetch耗时
    indices.search.fetch_current 正在处理的fetch数量
```

### 索引性能指标，前缀indices.indexing.* ，indices.refresh.* ，indices.flush.* 的指标
```
    indices.indexing.index_total 索引总量
    indices.indexing.index_time_in_millis 索引耗时
    indices.indexing.index_current 正在处理的索引量
    indices.refresh.total 刷新内存总量
    indices.refresh.total_time_in_millis 刷新内存耗时
    indices.flush.total 同步磁盘总量
    indices.flush.total_time_in_millis 同步磁盘耗时
```
### Cache性能指标，前缀indices.query_cache.* ，indices.fielddata.* ，indices.request_cache.* 的指标。

fielddata可能会成为内存消耗大户，需要特别注意
```
    indices.query_cache.memory_size_in_bytes 查询缓存大小
    indices.query_cache.evictions 查询缓存剔除大小
    indices.fielddata.memory_size_in_bytes fielddata缓存大小
    indices.fielddata.evictions fielddata缓存剔除大小
    indices.request_cache.memory_size_in_bytes 所有请求缓存大小
    indices.request_cache.evictions 所有请求缓存剔除大小
```

### os指标
```
    os.cpu.percent 系统CPU使用百分比
    os.cpu.load_average.1m 系统CPU 1分钟平均load
    os.cpu.load_average.5m 系统CPU 5分钟平均load
    os.cpu.load_average.15m 系统CPU 15分钟平均load
    os.mem.free_percent 系统内存可用百分比
    os.mem.used_percent 系统内存已使用百分比
    os.mem.total_in_bytes 系统内存总大小
    os.mem.free_in_bytes 系统内存可用大小
    os.mem.used_in_bytes 系统内存已使用大小
    os.swap.total_in_bytes 系统swap总大小
    os.swap.free_in_bytes 系统swap可用大小
    os.swap.used_in_bytes 系统swap已使用大小
```

### process指标，专用与es jvm进程的资源消耗指标
```
    process.cpu.percent 进程CPU使用百分比
    process.cpu.total_in_millis 进程CPU使用时间
    process.mem.total_virtual_in_bytes 进程可用虚拟内存大小
    process.open_file_descriptors 进程打开文件句柄数
    process.max_file_descriptors 进程可用句柄数
```

### JVM性能指标，前缀jvm.*的指标，内存使用及GC指标
```
    jvm.gc.collectors.young.collection_count young gc 大小
    jvm.gc.collectors.young.collection_time_in_millis young gc 耗时
    jvm.gc.collectors.old.collection_count old gc 大小
    jvm.gc.collectors.old.collection_time_in_millis old gc 耗时
    jvm.mem.heap_used_percent 内存使用百分比
    jvm.mem.heap_used_in_bytes 内存使用量
    jvm.mem.heap_committed_in_bytes 内存占用量
```

### 线程池性能指标，前缀thread_pool.*的指标
```
    thread_pool.bulk.queue thread_pool.index.queue thread_pool.search.queue thread_pool.merge.queue 各队列长度
    thread_pool.bulk.rejected thread_pool.index.rejected thread_pool.search.rejected thread_pool.merge.rejected 各队列溢出量（未执行，被放弃）
```

### 文件系统指标
```
    fs.total.total_in_bytes 数据目录总大小
    fs.total.free_in_bytes 数据目录剩余大小
    fs.total.vailable_in_bytes 数据目录可用大小
```
### 集群通信指标
```
    transport.rx_count 集群通信中接收的数据包总数
    transport.rx_size_in_bytes 集群通信中接收的数据的总大小
    transport.tx_count 集群通信中发送的数据包总数
    transport.tx_size_in_bytes 集群通信中发送的数据的总大小
    transport.server_open 为集群通信打开的连接数
```
