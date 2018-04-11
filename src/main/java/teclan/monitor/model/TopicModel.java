package teclan.monitor.model;

public class TopicModel extends MQModel {

	public TopicModel() {
		super("topic");
	}

	public TopicModel(String name, long queueSize, long consumerCount, long dequeueCount) {
		super("topic", name, queueSize, consumerCount, dequeueCount);
	}
}
