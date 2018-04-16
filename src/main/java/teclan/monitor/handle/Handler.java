package teclan.monitor.handle;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import teclan.monitor.model.MQModel;

public interface Handler {

	void handle(JSONObject jsonObject);

	void handle(List<MQModel> models);
}
