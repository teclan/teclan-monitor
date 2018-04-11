package teclan.monitor.mq;

import java.text.SimpleDateFormat;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class MqService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MqService.class);
	private static String BROKER_URL;
	private static String USER_NAME = "admin";
	private static String PASSWORD = "admin";
	private static String QUEUE_NAME;

	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static ActiveMQConnectionFactory CONNECTION_FACTORY;

	public static void init(String brokerUrl, String queueName) {
		BROKER_URL = brokerUrl;
		QUEUE_NAME = queueName;
		CONNECTION_FACTORY = new ActiveMQConnectionFactory(USER_NAME, PASSWORD, BROKER_URL);
	}


	public static void sendMessage(JSONObject jsonObject) {
		sendMessage(jsonObject.toJSONString());
	}

	private static void sendMessage(String message) {
		Session session = null;
		Destination sendQueue;
		Connection connection = null;

		try {
			connection = CONNECTION_FACTORY.createConnection();
			connection.start();
			session = connection
					.createSession(true, Session.SESSION_TRANSACTED);
			sendQueue = session.createQueue(QUEUE_NAME);

			MessageProducer sender = session.createProducer(sendQueue);
			TextMessage outMessage = session.createTextMessage(message);
			sender.send(outMessage);
			session.commit();
			sender.close();
			connection.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
