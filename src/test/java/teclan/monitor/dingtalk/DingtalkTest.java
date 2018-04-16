package teclan.monitor.dingtalk;

import java.io.IOException;

import org.junit.Test;

import teclan.dingtalk.DingTalkServer;

public class DingtalkTest {
	@Test
	public void send() throws IOException {
		DingTalkServer.send("系统状态监控", "CPU使用率超过 1.0%,当前:" + "10%");
	}

}
