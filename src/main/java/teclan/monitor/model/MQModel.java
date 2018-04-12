package teclan.monitor.model;

public abstract class MQModel {

	// IP
	private String ip;

	// 队列名称或广播名称
	private String name;
	// 队列或广播的剩余数据量
	private long queueSize;
	// 消费者数量
	private long consumerCount;
	// 出队数量
	private long dequeueCount;
	// 类型：queue 或 topic
	private String type;

	public MQModel(String type) {
		this.type = type;
	}

	public MQModel(String ip, String type, String name, long queueSize, long consumerCount, long dequeueCount) {
		this.ip = ip;
		this.type = type;
		this.name = name;
		this.queueSize = queueSize;
		this.consumerCount = consumerCount;
		this.dequeueCount = dequeueCount;

	}

	public String toString() {
		return String.format("type:%s,name:%s,size:%s,consumerCount:%s,dequeueCount:%s", type, name, queueSize,
				consumerCount, dequeueCount);
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(long queueSize) {
		this.queueSize = queueSize;
	}

	public long getConsumerCount() {
		return consumerCount;
	}

	public void setConsumerCount(long consumerCount) {
		this.consumerCount = consumerCount;
	}

	public long getDequeueCount() {
		return dequeueCount;
	}

	public void setDequeueCount(long dequeueCount) {
		this.dequeueCount = dequeueCount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
