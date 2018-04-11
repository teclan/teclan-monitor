package teclan.monitor.es;

import java.io.IOException;

import org.junit.Test;

import teclan.monitor.handle.DefaultHandler;
import teclan.monitor.handle.Handler;

public class ESMonitorTest {

	@Test
	public void esMonitorTest() {

		Handler handler = new DefaultHandler();

		try {
			ElasticsearchMonitor.monitor("10.0.0.222", 8200, handler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
