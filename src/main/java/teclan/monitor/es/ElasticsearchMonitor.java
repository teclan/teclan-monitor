package teclan.monitor.es;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import teclan.monitor.handle.Handler;

/**
 * elasticsearch 监控工具
 * 
 * 请求 _nodes/stats 接口，查询需要的信息，参考 README.md
 * 
 * @author dev
 *
 */
public class ElasticsearchMonitor {
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchMonitor.class);


	public static void monitor(String host, int port) throws IOException {

		OkHttpClient client = new OkHttpClient();

		String url = String.format("http://%s:%d/_nodes/stats", host, port);

		Request request = new Request.Builder().url(url).build();

		Response response = client.newCall(request).execute();

		JSONObject jsonObject = JSONObject.parseObject(response.body().string());

		LOGGER.info("{}", jsonObject);

	}

	public static void monitor(String host, int port, Handler handler) throws IOException {

		OkHttpClient client = new OkHttpClient();

		String url = String.format("http://%s:%d/_nodes/stats", host, port);

		Request request = new Request.Builder().url(url).build();

		Response response = client.newCall(request).execute();

		JSONObject jsonObject = JSONObject.parseObject(response.body().string());

		LOGGER.info("{}", jsonObject);

		handler.handle(jsonObject);

	}


}
