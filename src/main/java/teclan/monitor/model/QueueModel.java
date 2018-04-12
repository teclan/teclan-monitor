package teclan.monitor.model;

public class QueueModel extends MQModel {

	public QueueModel() {
		super("queue");
	}

	public QueueModel(String ip, String name, long queueSize, long consumerCount, long dequeueCount) {
		super(ip, "queue", name, queueSize, consumerCount, dequeueCount);
	}

}
