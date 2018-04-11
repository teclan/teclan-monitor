package teclan.monitor.handle;

import java.text.DecimalFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import teclan.monitor.model.MQModel;
import teclan.monitor.mysql.MysqlDatabase;

public class DefaultHandler implements Handler {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHandler.class);

	private static final String INSER_ES_STATUS_SQL = " INSERT INTO es_status (`address`,`docs_count`,`docs_delete`,`store_size`,`store_throttle_time`,"
			+ "`search_query_total`,`search_query_time`,`search_query_current`,`search_fetch_total`,`search_fetch_time`,`search_fetch_current`,"
			+ "`index_total`,`index_time`,`index_current`,`refresh_total`,`refresh_total_time`,`flush_total`,`flush_total_time`,`query_cache_size`,`query_cache_evictions`,"
			+ "`heap_used`,`heap_percent`,`heap_committed`,`heap_max`,`non_heap_used`,`non_heap_committed`,`tsp_server_open`,`tsp_rx_count`,"
			+ "`tsp_rx_size`,`tsp_tx_count`,`tsp_tx_size`,`http_current_open`,`http_total_opened`)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


	private static final String INSERT_MEMORY_STATUS_SQL = " INSERT INTO memory_status (total_memory,use_memory,use_percent,free_memory,free_percent,total_swap,use_swap,use_swap_percent,freel_swap,freel_swap_percent)\r\n"
			+ " VALUES (?,?,?,?,?,?,?,?,?,?)";

	private static final String INSEET_CPU_STATUS_SQL = "insert into cpu_status (cpu,percent) values (?,?)";

	@Override
	public void handle(JSONObject jsonObject) {

		DecimalFormat dft = new DecimalFormat("0.00");
		long divisor = 1024 * 1024 * 1024L;

		String clusterName = jsonObject.getString("cluster_name");
		JSONObject nodes = jsonObject.getJSONObject("nodes");

		for (String nodeName : nodes.keySet()) {

			JSONObject nodeData = nodes.getJSONObject(nodeName);

			String address = nodeData.getString("host");

			JSONObject indices = nodeData.getJSONObject("indices");

			// docs 展示节点内存有多少文档，包括还没有从段里清除的已删除文档数量。
			JSONObject docs = indices.getJSONObject("docs");
			long docsCount = docs.getLongValue("count");
			long docsDeleted = docs.getLongValue("deleted");
			// store 部分显示节点耗用了多少物理存储。这个指标包括主分片和副本分片在内。
			JSONObject store = indices.getJSONObject("store");
			String storeSize = dft.format(store.getLongValue("size_in_bytes") * 1.0 / divisor) + "G";
			// 如果限流时间很大，那可能表明你的磁盘限流设置得过低
			String storeThrottleTime = dft.format(store.getLongValue("throttle_time_in_millis") * 1.0 / 1000) + "s";

			JSONObject search = indices.getJSONObject("search");
			// 查询总量
			long searchQueryTotal = search.getLongValue("query_total");
			// 查询总耗时
			String searchQueryTime = dft.format(search.getLongValue("query_time_in_millis") * 1.0 / 1000) + "s";
			// 正在处理的查询量
			long searchQueryCurrent = search.getLongValue("query_current");

			// 查询的第二阶段fetch总量
			long searchFetchTotal = search.getLongValue("fetch_total");
			// fetch耗时
			String searchFetchTime = dft.format(search.getLongValue("fetch_time_in_millis") * 1.0 / 1000) + "s";
			// 正在处理的fetch数量
			long searchFetchCurrent = search.getLongValue("fetch_current");


			JSONObject indexing = indices.getJSONObject("indexing");
			// 索引总量
			long indexTotal = indexing.getLongValue("index_total");
			// 索引耗时
			String indexTime = dft.format(indexing.getLongValue("index_time_in_millis") * 1.0 / 1000) + "s";
			// 正在处理的索引量
			long indexCurrent = indexing.getLongValue("index_current");

			JSONObject refresh = indices.getJSONObject("refresh");
			// 刷新内存总量
			String refreshTotal = dft.format(refresh.getLongValue("total") * 1.0 / divisor) + "G";
			// 刷新内存耗时
			String refreshTotalTime = dft.format(refresh.getLongValue("total_time_in_millis") * 1.0 / 1000) + "s";

			JSONObject flush = indices.getJSONObject("flush");
			// 同步磁盘总量
			String flushTotal = dft.format(flush.getLongValue("total") * 1.0 / divisor) + "G";
			// 同步磁盘耗时
			String flushTotalTime = dft.format(flush.getLongValue("total_time_in_millis") * 1.0 / 1000) + "s";

			JSONObject queryCache = indices.getJSONObject("query_cache");
			// 查询缓存大小
			String queryCacheSize = dft.format(queryCache.getLongValue("memory_size_in_bytes") * 1024.0 / divisor)
					+ "M";
			// 查询缓存剔除大小
			String queryCacheEvictions = dft.format(queryCache.getLongValue("evictions") * 1024.0 / divisor) + "M";

			// 总的CPU使用率
			JSONObject os = nodeData.getJSONObject("os");
			long cpuPercent = os.getLongValue("cpu_percent");


			// 机器内存使用情况
			JSONObject osMem = os.getJSONObject("mem");
			String totalMem = dft.format(osMem.getLongValue("total_in_bytes") * 1.0 / divisor) + "G";
			String usedMem = dft.format(osMem.getLongValue("used_in_bytes") * 1.0 / divisor) + "G";
			String usedMemPercent = osMem.getLongValue("used_percent") 
					+ "%";
			String freeMem = dft.format(osMem.getLongValue("free_in_bytes") * 1.0 / divisor) + "G";
			String freeMemPercent = dft.format(
					osMem.getLongValue("free_in_bytes") * 100.0 / osMem.getLongValue("total_in_bytes"))
					+ "%";

			// 机器交换空间使用情况
			JSONObject swap = os.getJSONObject("swap");
			String totalSwap = dft.format(swap.getLongValue("total_in_bytes") * 1.0 / divisor) + "G";
			String freeSwap = dft.format(swap.getLongValue("free_in_bytes") * 1.0 / divisor) + "G";
			String useSwap = dft.format(swap.getLongValue("used_in_bytes") * 1.0 / divisor) + "G";
			String freeSwapPercent = dft.format(
					swap.getLongValue("free_in_bytes") * 100.0 / swap.getLongValue("total_in_bytes"))
					+ "%";
			String usedSwapPercent = dft.format(
					swap.getLongValue("used_in_bytes") * 100.0 / swap.getLongValue("total_in_bytes"))
					+ "%";


			JSONObject jvm = nodeData.getJSONObject("jvm");
			JSONObject jvmMem = jvm.getJSONObject("mem");

			String heapUsed = dft.format(jvmMem.getLongValue("heap_used_in_bytes") * 1024.0 / divisor) + "M";
			String heapUsedPercent = jvmMem.getLongValue("heap_used_percent") + "%";
			String heapCommitted = dft.format(jvmMem.getLongValue("heap_committed_in_bytes") * 1024.0 / divisor) + "M";
			String heapMax = dft.format(jvmMem.getLongValue("heap_max_in_bytes") * 1024.0 / divisor) + "M";
			String nonHeapUsed = dft.format(jvmMem.getLongValue("non_heap_used_in_bytes") * 1024.0 / divisor) + "M";
			String nonHeapCommitted = dft.format(jvmMem.getLongValue("non_heap_committed_in_bytes") * 1024.0 / divisor)
					+ "M";

			// transport 显示和 传输地址 相关的一些基础统计值。包括节点间的通信（通常是 9300
			// 端口）以及任意传输客户端或者节点客户端的连接。如果看到这里有很多连接数不要担心；
			// Elasticsearch 在节点之间维护了大量的连接。
			JSONObject transport = nodeData.getJSONObject("transport");
			long transportServerOpen = transport.getLongValue("server_open");
			// 接收的数据包总数
			long rxCount = transport.getLongValue("rx_count");
			String rxSize = dft.format(transport.getLongValue("rx_size_in_bytes") * 1024.0 / divisor) + "M";
			long txCount = transport.getLongValue("tx_count");
			String txSize = dft.format(transport.getLongValue("tx_size_in_bytes") * 1024.0 / divisor) + "M";

			// http 显示 HTTP 端口（通常是 9200）的统计值。如果你看到 total_opened
			// 数很大而且还在一直上涨，这是一个明确信号，说明你的 HTTP 客户端里有没启用
			// keep-alive 长连接的。持续的 keep-alive 长连接对性能很重要，因为连接、
			// 断开套接字是很昂贵的（而且浪费文件描述符）。请确认你的客户端都配置正确。
			JSONObject http = nodeData.getJSONObject("http");
			long httpCurrentOpen = http.getLongValue("current_open");
			long httpTotalOpened = http.getLongValue("total_opened");

			MysqlDatabase.openDatabase();
			
			MysqlDatabase.getDb().exec(INSER_ES_STATUS_SQL, address, docsCount, docsDeleted, storeSize,
					storeThrottleTime, searchQueryTotal, searchQueryTime, searchQueryCurrent, searchFetchTotal,
					searchFetchTime, searchFetchCurrent, indexTotal, indexTime, indexCurrent, refreshTotal,
					refreshTotalTime,
					flushTotal, flushTotalTime, queryCacheSize, queryCacheEvictions, heapUsed, heapUsedPercent,
					heapCommitted, heapMax, nonHeapUsed, nonHeapCommitted, transportServerOpen, rxCount, rxSize,
					txCount, txSize, httpCurrentOpen, httpTotalOpened);
			MysqlDatabase.getDb().exec(INSERT_MEMORY_STATUS_SQL, totalMem, usedMem, usedMemPercent, freeMem,
					freeMemPercent, totalSwap, useSwap, usedSwapPercent, freeSwap, freeSwapPercent);

			MysqlDatabase.getDb().exec(INSEET_CPU_STATUS_SQL, "所有", cpuPercent);

			MysqlDatabase.closeDatabase();

		}
	}

	@Override
	public void handle(List<MQModel> models) {
		// TODO Auto-generated method stub

	}

}
