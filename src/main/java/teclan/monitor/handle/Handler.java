package teclan.monitor.handle;

import java.util.List;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Swap;

import com.alibaba.fastjson.JSONObject;

import teclan.monitor.model.MQModel;
import teclan.sigar.modle.DiskLoad;
import teclan.sigar.modle.NetTraffic;
import teclan.sigar.modle.ProcessInfo;

public interface Handler {

	void handle(JSONObject jsonObject);

	void handle(List<MQModel> models);

	void handle(Mem men, Swap swap, CpuPerc cpuPerc, List<DiskLoad> diskLoads, NetTraffic netTraffic,
			List<ProcessInfo> processInfos);
}
