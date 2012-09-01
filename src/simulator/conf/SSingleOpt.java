package simulator.conf;

public class SSingleOpt implements SOptInterface {
	private String name;
	private String value;

	public SSingleOpt(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String getOptName() {
		return name;
	}

	public String getValue() {
		return value;
	}

}
