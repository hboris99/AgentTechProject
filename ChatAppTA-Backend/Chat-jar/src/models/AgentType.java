package models;

import java.io.Serializable;

public class AgentType implements Serializable{
	private String name;
	private String host;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AgentType(String name, String host) {
		super();
		this.name = name;
		this.host = host;
	}

	public AgentType() {
		super();
		// TODO Auto-generated constructor stub
	}
	

}
