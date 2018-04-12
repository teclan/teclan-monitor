package teclan.monitor.model;

public class TopicModel extends MQModel {

	public TopicModel() {
		super("topic");
	}

	public TopicModel(String ip, String name, long queueSize, long consumerCount, long dequeueCount) {
		super(ip, "topic", name, queueSize, consumerCount, dequeueCount);
	}
}
