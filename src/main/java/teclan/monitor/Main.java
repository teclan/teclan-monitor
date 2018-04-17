package teclan.monitor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import teclan.dingtalk.DingTalkServer;
import teclan.monitor.es.ElasticsearchMonitor;
import teclan.monitor.handle.DefaultHandler;
import teclan.monitor.handle.Handler;
import teclan.monitor.mq.MqMonitor;

public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private static ScheduledExecutorService EXECUTORS = Executors.newScheduledThreadPool(1);

	public static void main(String[] args) {

		// 加载配置文件
		File file = new File("config/application.conf");

		Config root = ConfigFactory.parseFile(file);

		Config config = root.getConfig("config");

		// ES 配置
		Config es = config.getConfig("es");
		final boolean esEnable = es.getBoolean("enable");
		// ES ip地址
		final String esIp = es.getString("ip");
		// es 的 http 端口
		final int esHttpPort = es.getInt("httpPort");

		// MQ 配置
		Config mq = config.getConfig("mq");
		final boolean mqEnable = mq.getBoolean("enable");
		// MQ 地址
		final String mqIp = mq.getString("ip");

		// 以下为 MQ 的其他配置，详见 README.md
		final int connectorPort = mq.getInt("connectorPort");
		final String connectorPath = mq.getString("connectorPath");
		final String jmxDomainName = mq.getString("jmxDomainName");
		final String brokerName = mq.getString("brokerName");
		final List<String> queues = mq.getStringList("queues");
		final List<String> topics = mq.getStringList("topics");

		// 任务配置
		Config task = config.getConfig("task");
		// 刷新频率，单位：秒
		int flushRate = task.getInt("flushRate");

		final Handler handler = new DefaultHandler();

		EXECUTORS.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					if (esEnable) {
						ElasticsearchMonitor.monitor(esIp, esHttpPort, handler);
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
					try {
						DingTalkServer.send("系统状态监控", String.format(" 来自机器 %s 的异常状态，Elasticsearch 无法监控", esIp));
					} catch (IOException e1) {
						LOGGER.error(e.getMessage(), e);
					}
				}

				try {

					if (mqEnable) {
						MqMonitor.monitor(mqIp, connectorPort, connectorPath, jmxDomainName, brokerName, queues, topics,
								handler);
					}

				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
					try {
						DingTalkServer.send("系统状态监控", String.format("  来自机器  %s 的异常状态，ActiveMQ无法监控", mqIp));
					} catch (IOException e1) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
		}, 0, flushRate, TimeUnit.SECONDS);
	}

}
