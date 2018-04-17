package teclan.monitor.mq;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.management.MalformedObjectNameException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import teclan.monitor.handle.DefaultHandler;
import teclan.monitor.handle.Handler;

public class MqMonitorTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(MqMonitorTest.class);

	@Test
	public void monitorTest() {

		try {
			// 加载配置文件
			File file = new File("config/application.conf");

			Config root = ConfigFactory.parseFile(file);

			Config config = root.getConfig("config");

			// MQ 配置
			Config mq = config.getConfig("mq");
			final boolean mqEnable = mq.getBoolean("enable");
			// MQ 地址
			final String mqIp = mq.getString("ip");

			// 以下为 MQ 的其他配置，详见 README.md
			final int connectorPort = mq.getInt("connectorPort");
			final String connectorPath = mq.getString("connectorPath");
			final String jmxDomainName = mq.getString("jmxDomainName");
			final List<String> queues = mq.getStringList("queues");
			final List<String> topics = mq.getStringList("topics");
			final String brokerName = mq.getString("brokerName");

			Handler handler = new DefaultHandler();

			MqMonitor.monitor(mqIp, connectorPort, connectorPath, jmxDomainName, brokerName, queues, topics, handler);


		} catch (MalformedObjectNameException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
