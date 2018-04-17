package teclan.monitor.mq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.broker.jmx.TopicViewMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teclan.monitor.handle.Handler;
import teclan.monitor.model.MQModel;
import teclan.monitor.model.QueueModel;
import teclan.monitor.model.TopicModel;

/**
 * activemq 监控工具
 * 
 * 修改conf目录下的activemq.xml文件的 managementContext 节点
 * 
 * <managementContext>
 * 
 * <managementContext createConnector="true" connectorPort="1099" connectorPath=
 * 
 * "/jmxrmi" jmxDomainName="org.apache.activemq"/> </managementContext>
 * 
 * 
 * @author dev
 *
 */
public class MqMonitor {
	private static final Logger LOGGER = LoggerFactory.getLogger(MqMonitor.class);

	/**
	 * 
	 * 监控 ActiveMQ
	 * 
	 * @param ip
	 * @param connectorPort
	 * @param connectorPath
	 * @param jmxDomainName
	 *            必须与activemq.xml中的jmxDomainName一致
	 * @param brokerName
	 *            必须与activemq.xml中 broker 节点的 brokerName一致
	 * @throws IOException
	 * @throws MalformedObjectNameException
	 */
	public static List<MQModel> monitor(String ip, int connectorPort, String connectorPath, String jmxDomainName,
			String brokerName)
			throws IOException, MalformedObjectNameException {

		List<MQModel> models = new ArrayList<MQModel>();

		JMXServiceURL url = new JMXServiceURL(
				String.format("service:jmx:rmi:///jndi/rmi://%s:%s%s", ip, connectorPort, connectorPath));
		JMXConnector connector = JMXConnectorFactory.connect(url, null);
		connector.connect();

		MBeanServerConnection connection = connector.getMBeanServerConnection();

		ObjectName name = new ObjectName(jmxDomainName + ":BrokerName=" + brokerName + ",Type=Broker");
		BrokerViewMBean mBean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection, name,
				BrokerViewMBean.class, true);

		for (ObjectName topicName : mBean.getTopics()) {
			TopicViewMBean topice = (TopicViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection,
					topicName, TopicViewMBean.class, true);

			TopicModel topicModel = new TopicModel(ip, topice.getName(), topice.getQueueSize(),
					topice.getConsumerCount(), topice.getDequeueCount());

			models.add(topicModel);
		}

		for (ObjectName queueName : mBean.getQueues()) {
			QueueViewMBean queueMBean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection,
					queueName, QueueViewMBean.class, true);

			QueueModel queue = new QueueModel(ip, queueMBean.getName(), queueMBean.getQueueSize(),
					queueMBean.getConsumerCount(), queueMBean.getDequeueCount());

			models.add(queue);
		}

		connector.close();

		return models;

	}

	/**
	 * 
	 * 监控 ActiveMQ
	 * 
	 * @param ip
	 * @param connectorPort
	 * @param connectorPath
	 * @param jmxDomainName
	 *            必须与activemq.xml中的jmxDomainName一致
	 * @param brokerName
	 *            必须与activemq.xml中 broker 节点的 brokerName一致
	 * @param handler
	 *            对结果的处理
	 * @throws IOException
	 * @throws MalformedObjectNameException
	 */
	public static void monitor(String ip, int connectorPort, String connectorPath, String jmxDomainName,
			String brokerName, List<String> queues, List<String> topics, Handler handler)
			throws IOException, MalformedObjectNameException {

		List<MQModel> models = new ArrayList<MQModel>();

		JMXServiceURL url = new JMXServiceURL(
				String.format("service:jmx:rmi:///jndi/rmi://%s:%s%s", ip, connectorPort, connectorPath));
		JMXConnector connector = JMXConnectorFactory.connect(url, null);
		connector.connect();

		MBeanServerConnection connection = connector.getMBeanServerConnection();

		ObjectName name = new ObjectName(jmxDomainName + ":brokerName=" + brokerName + ",type=Broker");

		BrokerViewMBean mBean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection, name,
				BrokerViewMBean.class, true);

		for (ObjectName topicName : mBean.getTopics()) {

			TopicViewMBean topice = (TopicViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection,
					topicName, TopicViewMBean.class, true);

			if (!topics.isEmpty() && !topics.contains(topice.getName())) {
				LOGGER.debug("topic `{}` 不在监控列表中...", topice.getName());
				continue;
			}

			TopicModel topicModel = new TopicModel(ip, topice.getName(), topice.getQueueSize(),
					topice.getConsumerCount(), topice.getDequeueCount());

			models.add(topicModel);
		}

		for (ObjectName queueName : mBean.getQueues()) {
			QueueViewMBean queueMBean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection,
					queueName, QueueViewMBean.class, true);

			if (!queues.isEmpty() && !queues.contains(queueMBean.getName())) {
				LOGGER.debug("queue `{}` 不在监控列表中...", queueMBean.getName());
				continue;
			}

			QueueModel queue = new QueueModel(ip, queueMBean.getName(), queueMBean.getQueueSize(),
					queueMBean.getConsumerCount(), queueMBean.getDequeueCount());

			models.add(queue);
		}

		connector.close();

		LOGGER.info("{}", "ActiveMQ扫描完成");

		handler.handle(models);

	}

}
