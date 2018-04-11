package teclan.monitor.mq;

import java.io.IOException;
import java.util.List;

import javax.management.MalformedObjectNameException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teclan.monitor.model.MQModel;

public class MqMonitorTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(MqMonitorTest.class);

	@Test
	public void monitorTest() {

		try {
			List<MQModel> models = MqMonitor.monitor("localhost", 1099, "/jmxrmi", "org.apache.activemq");

			for (MQModel model : models) {
				LOGGER.info("{}", model);
			}

		} catch (MalformedObjectNameException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
