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
	 * @param ip
	 * @param connectorPort
	 * @param connectorPath
	 * @param jmxDomainName
	 *            必须与activemq.xml中的jmxDomainName一致
	 * @throws IOException
	 * @throws MalformedObjectNameException
	 */
	public static List<MQModel> monitor(String ip, int connectorPort, String connectorPath, String jmxDomainName)
			throws IOException, MalformedObjectNameException {

		List<MQModel> models = new ArrayList<MQModel>();

			JMXServiceURL url = new JMXServiceURL(
				String.format("service:jmx:rmi:///jndi/rmi://%s:%s%s", ip, connectorPort, connectorPath));
			JMXConnector connector = JMXConnectorFactory.connect(url, null);
			connector.connect();

		MBeanServerConnection connection = connector.getMBeanServerConnection();

		ObjectName name = new ObjectName(jmxDomainName + ":BrokerName=localhost,Type=Broker");
		BrokerViewMBean mBean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection, name,
				BrokerViewMBean.class, true);

		for (ObjectName topicName : mBean.getTopics()) {
			TopicViewMBean topice = (TopicViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection,
					topicName, TopicViewMBean.class, true);

			TopicModel topicModel = new TopicModel(topice.getName(), topice.getQueueSize(), topice.getConsumerCount(),
					topice.getDequeueCount());

			models.add(topicModel);
		}

		for (ObjectName queueName : mBean.getQueues()) {
			QueueViewMBean queueMBean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection,
					queueName, QueueViewMBean.class, true);

			QueueModel queue = new QueueModel(queueMBean.getName(), queueMBean.getQueueSize(),
					queueMBean.getConsumerCount(), queueMBean.getDequeueCount());

			models.add(queue);
		}

		connector.close();

		return models;

	}
}
