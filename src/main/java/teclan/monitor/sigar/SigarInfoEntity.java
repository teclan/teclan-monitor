package teclan.monitor.sigar;

public class SigarInfoEntity {
	private Object value;
	private String name;

	public String toString() {
		return String.format("%s:%s", name, value);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SigarInfoEntity(Object value, String name) {
		super();
		this.value = value;
		this.name = name;
	}

	public SigarInfoEntity() {

	}
}
