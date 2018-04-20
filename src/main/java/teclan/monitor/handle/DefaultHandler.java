package teclan.monitor.handle;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Swap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import teclan.dingtalk.DingTalkServer;
import teclan.monitor.model.MQModel;
import teclan.monitor.mysql.MysqlDatabase;
import teclan.sigar.modle.DiskLoad;
import teclan.sigar.modle.NetTraffic;

public class DefaultHandler implements Handler {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHandler.class);

	private static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(2);

	private static final String INSER_ES_STATUS_SQL = " INSERT INTO es_status (`address`,`docs_count`,`docs_delete`,`store_size`,`store_throttle_time`,"
			+ "`search_query_total`,`search_query_time`,`search_query_current`,`search_fetch_total`,`search_fetch_time`,`search_fetch_current`,"
			+ "`index_total`,`index_time`,`index_current`,`refresh_total`,`refresh_total_time`,`flush_total`,`flush_total_time`,`query_cache_size`,`query_cache_evictions`,"
			+ "`heap_used`,`heap_percent`,`heap_committed`,`heap_max`,`non_heap_used`,`non_heap_committed`,`tsp_server_open`,`tsp_rx_count`,"
			+ "`tsp_rx_size`,`tsp_tx_count`,`tsp_tx_size`,`http_current_open`,`http_total_opened`)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String INSERT_MEMORY_STATUS_SQL = " INSERT INTO memory_status (total_memory,use_memory,use_percent,free_memory,free_percent,total_swap,use_swap,use_swap_percent,freel_swap,freel_swap_percent)\r\n"
			+ " VALUES (?,?,?,?,?,?,?,?,?,?)";

	private static final String INSEET_CPU_STATUS_SQL = "insert into cpu_status (cpu,percent) values (?,?)";

	private static final String INSERT_MQ_STATUS_SQL = " INSERT INTO mq_status (ip,name,type,size,consumer_count,dequeue_count) VALUES (?,?,?,?,?,?)";

	private static final String INSERT_NETWORK_STATUS_SQL = " INSERT INTO network_status (ip,speed,packets,rx_speed,rx_packets,tx_speed,tx_packets) VALUES (?,?,?,?,?,?,?)";

	private static final String INSERT_DISK_STATUS_SQL = " INSERT INTO disk_status (`file_system`,`read_speed`,`write_speed`,`reads`,`writes`,`queue`) VALUES ( ?,?,?,?,?,?)";

	private static Double thresholdCpu;
	private static Double thresholdMemory;
	private static int thresholdQueueSize;

	private static DecimalFormat dft = new DecimalFormat("0.00");

	static {

		// 加载配置文件
		File file = new File("config/application.conf");

		Config root = ConfigFactory.parseFile(file);
		Config config = root.getConfig("config");
		Config threshold = config.getConfig("threshold");

		thresholdCpu = Double.valueOf(threshold.getString("cpu").substring(0, threshold.getString("cpu").indexOf("%")));
		thresholdMemory = Double
				.valueOf(threshold.getString("memory").substring(0, threshold.getString("memory").indexOf("%")));
		thresholdQueueSize = threshold.getInt("queueSize");
	}


	@Override
	public void handle(JSONObject jsonObject) {


		long divisor = 1024 * 1024 * 1024L;

		// String clusterName = jsonObject.getString("cluster_name");
		JSONObject nodes = jsonObject.getJSONObject("nodes");

		for (String nodeName : nodes.keySet()) {

			JSONObject nodeData = nodes.getJSONObject(nodeName);

			final String address = nodeData.getString("host");

			JSONObject indices = nodeData.getJSONObject("indices");

			// docs 展示节点内存有多少文档，包括还没有从段里清除的已删除文档数量。
			JSONObject docs = indices.getJSONObject("docs");
			final long docsCount = docs.getLongValue("count");
			final long docsDeleted = docs.getLongValue("deleted");
			// store 部分显示节点耗用了多少物理存储。这个指标包括主分片和副本分片在内。
			JSONObject store = indices.getJSONObject("store");
			final String storeSize = dft.format(store.getLongValue("size_in_bytes") * 1.0 / divisor) + "G";
			// 如果限流时间很大，那可能表明你的磁盘限流设置得过低
			final String storeThrottleTime = dft.format(store.getLongValue("throttle_time_in_millis") * 1.0 / 1000) + "s";

			JSONObject search = indices.getJSONObject("search");
			// 查询总量
			final long searchQueryTotal = search.getLongValue("query_total");
			// 查询总耗时
			final String searchQueryTime = dft.format(search.getLongValue("query_time_in_millis") * 1.0 / 1000) + "s";
			// 正在处理的查询量
			final long searchQueryCurrent = search.getLongValue("query_current");

			// 查询的第二阶段fetch总量
			final long searchFetchTotal = search.getLongValue("fetch_total");
			// fetch耗时
			final String searchFetchTime = dft.format(search.getLongValue("fetch_time_in_millis") * 1.0 / 1000) + "s";
			// 正在处理的fetch数量
			final long searchFetchCurrent = search.getLongValue("fetch_current");


			JSONObject indexing = indices.getJSONObject("indexing");
			// 索引总量
			final long indexTotal = indexing.getLongValue("index_total");
			// 索引耗时
			final String indexTime = dft.format(indexing.getLongValue("index_time_in_millis") * 1.0 / 1000) + "s";
			// 正在处理的索引量
			final long indexCurrent = indexing.getLongValue("index_current");

			JSONObject refresh = indices.getJSONObject("refresh");
			// 刷新内存总量
			final String refreshTotal = dft.format(refresh.getLongValue("total") * 1.0 / divisor) + "G";
			// 刷新内存耗时
			final String refreshTotalTime = dft.format(refresh.getLongValue("total_time_in_millis") * 1.0 / 1000) + "s";

			JSONObject flush = indices.getJSONObject("flush");
			// 同步磁盘总量
			final String flushTotal = dft.format(flush.getLongValue("total") * 1.0 / divisor) + "G";
			// 同步磁盘耗时
			final String flushTotalTime = dft.format(flush.getLongValue("total_time_in_millis") * 1.0 / 1000) + "s";

			JSONObject queryCache = indices.getJSONObject("query_cache");
			// 查询缓存大小
			final String queryCacheSize = dft.format(queryCache.getLongValue("memory_size_in_bytes") * 1024.0 / divisor)
					+ "M";
			// 查询缓存剔除大小
			final String queryCacheEvictions = dft.format(queryCache.getLongValue("evictions") * 1024.0 / divisor) + "M";

			// // 总的CPU使用率
			// JSONObject os = nodeData.getJSONObject("os");
			// final String cpuPercent = dft.format(os.getLongValue("cpu_percent")) + "%";

			// // 机器内存使用情况
			// JSONObject osMem = os.getJSONObject("mem");
			// final String totalMem = dft.format(osMem.getLongValue("total_in_bytes") * 1.0
			// / divisor) + "G";
			// final String usedMem = dft.format(osMem.getLongValue("used_in_bytes") * 1.0 /
			// divisor) + "G";
			// final String usedMemPercent = dft.format(osMem.getLongValue("used_percent"))
			// + "%";
			// final String freeMem = dft.format(osMem.getLongValue("free_in_bytes") * 1.0 /
			// divisor) + "G";
			// final String freeMemPercent = dft.format(
			// osMem.getLongValue("free_in_bytes") * 100.0 /
			// osMem.getLongValue("total_in_bytes"))
			// + "%";

			// // 机器交换空间使用情况
			// JSONObject swap = os.getJSONObject("swap");
			// final String totalSwap = dft.format(swap.getLongValue("total_in_bytes") * 1.0
			// / divisor) + "G";
			// final String freeSwap = dft.format(swap.getLongValue("free_in_bytes") * 1.0 /
			// divisor) + "G";
			// final String useSwap = dft.format(swap.getLongValue("used_in_bytes") * 1.0 /
			// divisor) + "G";
			// final String freeSwapPercent = dft.format(
			// swap.getLongValue("free_in_bytes") * 100.0 /
			// swap.getLongValue("total_in_bytes"))
			// + "%";
			// final String usedSwapPercent = dft.format(
			// swap.getLongValue("used_in_bytes") * 100.0 /
			// swap.getLongValue("total_in_bytes"))
			// + "%";


			JSONObject jvm = nodeData.getJSONObject("jvm");
			JSONObject jvmMem = jvm.getJSONObject("mem");

			final String heapUsed = dft.format(jvmMem.getLongValue("heap_used_in_bytes") * 1024.0 / divisor) + "M";
			final String heapUsedPercent = jvmMem.getLongValue("heap_used_percent") + "%";
			final String heapCommitted = dft.format(jvmMem.getLongValue("heap_committed_in_bytes") * 1024.0 / divisor) + "M";
			final String heapMax = dft.format(jvmMem.getLongValue("heap_max_in_bytes") * 1024.0 / divisor) + "M";
			final String nonHeapUsed = dft.format(jvmMem.getLongValue("non_heap_used_in_bytes") * 1024.0 / divisor) + "M";
			final String nonHeapCommitted = dft.format(jvmMem.getLongValue("non_heap_committed_in_bytes") * 1024.0 / divisor)
					+ "M";

			// transport 显示和 传输地址 相关的一些基础统计值。包括节点间的通信（通常是 9300
			// 端口）以及任意传输客户端或者节点客户端的连接。如果看到这里有很多连接数不要担心；
			// Elasticsearch 在节点之间维护了大量的连接。
			JSONObject transport = nodeData.getJSONObject("transport");
			final long transportServerOpen = transport.getLongValue("server_open");
			// 接收的数据包总数
			final long rxCount = transport.getLongValue("rx_count");
			final String rxSize = dft.format(transport.getLongValue("rx_size_in_bytes") * 1024.0 / divisor) + "M";
			final long txCount = transport.getLongValue("tx_count");
			final String txSize = dft.format(transport.getLongValue("tx_size_in_bytes") * 1024.0 / divisor) + "M";

			// http 显示 HTTP 端口（通常是 9200）的统计值。如果你看到 total_opened
			// 数很大而且还在一直上涨，这是一个明确信号，说明你的 HTTP 客户端里有没启用
			// keep-alive 长连接的。持续的 keep-alive 长连接对性能很重要，因为连接、
			// 断开套接字是很昂贵的（而且浪费文件描述符）。请确认你的客户端都配置正确。
			final JSONObject http = nodeData.getJSONObject("http");
			final long httpCurrentOpen = http.getLongValue("current_open");
			final long httpTotalOpened = http.getLongValue("total_opened");
			
			
			EXECUTORS.execute(new Runnable() {
				public void run() {
					
					try {
					MysqlDatabase.openDatabase();
					
					MysqlDatabase.getDb().exec(INSER_ES_STATUS_SQL, address, docsCount, docsDeleted, storeSize,
							storeThrottleTime, searchQueryTotal, searchQueryTime, searchQueryCurrent, searchFetchTotal,
							searchFetchTime, searchFetchCurrent, indexTotal, indexTime, indexCurrent, refreshTotal,
							refreshTotalTime,
							flushTotal, flushTotalTime, queryCacheSize, queryCacheEvictions, heapUsed, heapUsedPercent,
							heapCommitted, heapMax, nonHeapUsed, nonHeapCommitted, transportServerOpen, rxCount, rxSize,
							txCount, txSize, httpCurrentOpen, httpTotalOpened);

					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					} finally {
						MysqlDatabase.closeDatabase();
					}
				}
			});

		}
	}

	@Override
	public void handle(final List<MQModel> models) {

		EXECUTORS.execute(new Runnable() {

			@Override
			public void run() {

				try {
					MysqlDatabase.openDatabase();

					for (MQModel model : models) {
						MysqlDatabase.getDb().exec(INSERT_MQ_STATUS_SQL, model.getIp(), model.getName(),
								model.getType(), model.getQueueSize(), model.getConsumerCount(),
								model.getDequeueCount());

						if (model.getQueueSize() > thresholdQueueSize) {
							DingTalkServer.send("系统状态监控", String.format("  来自机器 %s 的异常状态，%s(%s)未处理数据超过预设阈值%s，当前%s ",
									model.getIp(), model.getName(), model.getType(), thresholdQueueSize,
									model.getQueueSize()));
						}

					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				} finally {
					MysqlDatabase.closeDatabase();
				}
			}
		});
	}

	@Override
	public void handle(final Mem mem, final Swap swap, final CpuPerc cpuPerc, final List<DiskLoad> diskLoads,
			final NetTraffic netTraffic) {
		
		final long divisor = 1024 * 1024 * 1024L;

		EXECUTORS.execute(new Runnable() {
			public void run() {
				
				try {
				MysqlDatabase.openDatabase();
				
					MysqlDatabase.getDb().exec(INSERT_MEMORY_STATUS_SQL,
							dft.format(mem.getTotal() * 1.0 / divisor) + "G",
							dft.format(mem.getUsed() * 1.0 / divisor) + "G", dft.format(mem.getUsedPercent()) + "%",
							dft.format(mem.getFree() * 1.0 / divisor) + "G", dft.format(mem.getFreePercent()) + "%",
							dft.format(swap.getTotal() * 1.0 / divisor) + "G",
							dft.format(swap.getUsed() * 1.0 / divisor) + "G",
							dft.format(swap.getUsed() * 100.0 / swap.getTotal()) + "%",
							dft.format(swap.getFree() * 1.0 / divisor) + "G",
							dft.format(swap.getFree() * 100.0 / swap.getTotal()) + "%");

					MysqlDatabase.getDb().exec(INSEET_CPU_STATUS_SQL, "所有",
							dft.format(cpuPerc.getCombined() * 100) + "%");

					MysqlDatabase.getDb().exec(INSERT_NETWORK_STATUS_SQL, netTraffic.getIp(),
							dft.format(netTraffic.getSpeedInBytes() * 1.0 / 1024 / 1024) + "Mbps",
							netTraffic.getSpeedInPackets(),
							dft.format(netTraffic.getRxSpeedInBytes() * 1.0 / 1024 / 1024) + "Mbps",
							netTraffic.getRxSpeesInPackets(),
							dft.format(netTraffic.getTxSpeedInBytes() * 1.0 / 1024 / 1024) + "Mbps",
							netTraffic.getTxSpeesInPackets());

					for (DiskLoad diskLoad : diskLoads) {
						
						MysqlDatabase.getDb().exec(INSERT_DISK_STATUS_SQL,
								diskLoad.getFileSystem(),
								dft.format(diskLoad.getReadInBytes() * 1.0 / 1024) + "Kbps",
								dft.format(diskLoad.getWriteInBytes() * 1.0 / 1024) + "Kbps", diskLoad.getDiskReads(),
								diskLoad.getDiskWrites(), diskLoad.getDiskQueue());
					}

					StringBuilder notice = new StringBuilder();

					if (cpuPerc.getCombined() > thresholdCpu) {
						notice.append(
								String.format("  \nCPU使用率超过 %s ,当前：%s", thresholdCpu + "%",
										cpuPerc.getCombined() + "%"));
					}
					if (mem.getUsedPercent() > thresholdMemory) {
						notice.append(String.format("  \n内存使用率超过 %s ,当前：%s", thresholdMemory + "%",
								dft.format(mem.getUsedPercent()) + "%"));
					}


					if (notice.length() > 0) {
						notice.append(String.format("  \n网络接收速率 ：%s ,网络发送速率：%s，总速率：%s",
								dft.format(netTraffic.getRxSpeedInBytes() * 1.0 / 1024 / 1024) + "Mbps",
								dft.format(netTraffic.getTxSpeedInBytes() * 1.0 / 1024 / 1024) + "Mbps",
								dft.format(netTraffic.getSpeedInBytes() * 1.0 / 1024 / 1024) + "Mbps"));

						for (DiskLoad diskLoad : diskLoads) {
							notice.append(String.format("  \n%s", getDiskLoadAlarm(diskLoad)));
						}


						DingTalkServer.send("系统状态监控",
								String.format("  来自机器 %s 的异常状态", netTraffic.getIp()) + notice.toString());
					}


				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				} finally {
					MysqlDatabase.closeDatabase();
				}
			}
		});
	}

	private String getDiskLoadAlarm(DiskLoad diskLoad) {
		
		return String.format("文件系统:%s，读取速率:%s，写速率:%s，文件队列:%s", diskLoad.getFileSystem(),
				dft.format(diskLoad.getReadInBytes() * 1.0 / 1024) + "Kbps",
				dft.format(diskLoad.getWriteInBytes() * 1.0 / 1024) + "Kbps", diskLoad.getDiskQueue());
	}

}
