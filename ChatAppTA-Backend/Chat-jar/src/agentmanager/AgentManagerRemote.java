package agentmanager;

import java.util.List;

import javax.ejb.Remote;

import agents.AID;
import agents.Agent;
import models.AgentType;

@Remote
public interface AgentManagerRemote {
	public AID startAgent(String name, AID id);
	public Agent getAgentById(AID agentId);
	public Agent getByIdOrStartNew(String name, AID id);
	public void stop(AID name);
	public List<AgentType> getAgentTypes();
	public void stopAgentByName(String aid);
	public void setRemoteRunningAgents(List<AID> agents);
	public List<AID> getRemoteAgents();
	public void setRemoteAgentTypes(List<AgentType> types);
}
