package agents;

import models.AgentType;
import models.Host;

public class AID {
	private String name;
	private Host host;
	private AgentType agentType;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Host getHost() {
		return host;
	}
	@Override
	public String toString() {
		return name + agentType.getName() +  host.getAddress() +host.getAlias();
	}
	public void setHost(Host host) {
		this.host = host;
	}
	public AgentType getAgentType() {
		return agentType;
	}
	public void setAgentType(AgentType agentType) {
		this.agentType = agentType;
	}
	public AID(String name, Host host, AgentType agentType) {
		super();
		this.name = name;
		this.host = host;
		this.agentType = agentType;
	}
	public AID() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
