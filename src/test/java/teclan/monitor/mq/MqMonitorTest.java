package teclan.monitor.mq;

import java.io.IOException;

import javax.management.MalformedObjectNameException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teclan.monitor.handle.DefaultHandler;
import teclan.monitor.handle.Handler;

public class MqMonitorTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(MqMonitorTest.class);

	@Test
	public void monitorTest() {

		try {

			Handler handler = new DefaultHandler();

			MqMonitor.monitor("localhost", 1099, "/jmxrmi", "org.apache.activemq", handler);


		} catch (MalformedObjectNameException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
