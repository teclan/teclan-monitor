package teclan.monitor.model;

public class QueueModel extends MQModel {

	public QueueModel() {
		super("queue");
	}

	public QueueModel(String name, long queueSize, long consumerCount, long dequeueCount) {
		super("queue", name, queueSize, consumerCount, dequeueCount);
	}

}
