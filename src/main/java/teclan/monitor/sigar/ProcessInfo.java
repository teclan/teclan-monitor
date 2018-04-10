package teclan.monitor.sigar;

import com.alibaba.fastjson.JSON;

public class ProcessInfo {
	private String pid;

	private String user;

	private String startTime;

	private String memSize;

	private String memUse;

	private String memhare;

	private String state;

	private String cpuTime;

	private String name;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getMemSize() {
		return memSize;
	}

	public void setMemSize(String memSize) {
		this.memSize = memSize;
	}

	public String getMemUse() {
		return memUse;
	}

	public void setMemUse(String memUse) {
		this.memUse = memUse;
	}

	public String getMemhare() {
		return memhare;
	}

	public void setMemhare(String memhare) {
		this.memhare = memhare;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCpuTime() {
		return cpuTime;
	}

	public void setCpuTime(String cpuTime) {
		this.cpuTime = cpuTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return JSON.toJSONString(this);
	}
}
