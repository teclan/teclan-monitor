package teclan.monitor;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.MalformedObjectNameException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import teclan.monitor.es.ElasticsearchMonitor;
import teclan.monitor.handle.DefaultHandler;
import teclan.monitor.handle.Handler;
import teclan.monitor.mq.MqMonitor;

public class Main {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	private static final Timer TIMER = new Timer();

	public static void main(String[] args) {


		// 加载配置文件
		File file = new File("config/application.conf");

		Config root = ConfigFactory.parseFile(file);

		Config config = root.getConfig("config");

		// ES 配置
		Config es = config.getConfig("es");
		// ES ip地址
		final String esIp = es.getString("ip");
		// es 的 http 端口
		final int esHttpPort = es.getInt("httpPort");

		// MQ 配置
		Config mq = config.getConfig("mq");
		// MQ 地址
		final String mqIp = mq.getString("ip");
		// 以下为 MQ 的其他配置，详见 README.md
		final int connectorPort = mq.getInt("connectorPort");
		final String connectorPath = mq.getString("connectorPath");
		final String jmxDomainName = mq.getString("jmxDomainName");

		// 任务配置
		Config task = config.getConfig("task");
		// 刷新频率，单位：秒
		int flushRate = task.getInt("flushRate");

		final Handler handler = new DefaultHandler();

		TIMER.schedule(new TimerTask() {

			@Override
			public void run() {

				try {
					ElasticsearchMonitor.monitor(esIp, esHttpPort, handler);
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}

				try {

					MqMonitor.monitor(mqIp, connectorPort, connectorPath, jmxDomainName, handler);

				} catch (MalformedObjectNameException | IOException e) {
					LOGGER.error(e.getMessage(), e);
				}


			}
		}, 0, flushRate * 1000);


//		// 内存使用率
//
//		try {
//			List<SigarInfoEntity> memoryInfos = SigarUtils.getMemoryInfos();
//			for (SigarInfoEntity info : memoryInfos) {
//				LOGGER.info("{}", info);
//			}
//
//			LOGGER.info("\n\n=============================\n\n");
//
//			List<SigarInfoEntity> cpuInfos = SigarUtils.getCpuInfos();
//			for (SigarInfoEntity info : cpuInfos) {
//				LOGGER.info("{}", info);
//			}
//
//		} catch (SigarException e) {
//			LOGGER.error(e.getMessage(), e);
//		}
		


	}

}
