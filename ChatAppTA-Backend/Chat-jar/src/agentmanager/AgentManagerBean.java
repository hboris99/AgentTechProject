package agentmanager;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import agents.AID;
import agents.Agent;
import agents.CachedAgentsRemote;
import models.AgentType;
import util.JNDILookup;

/**
 * Session Bean implementation class AgentManagerBean
 */
@Stateless
@LocalBean
public class AgentManagerBean implements AgentManagerRemote {
	
	@EJB
	private CachedAgentsRemote cachedAgents;
	
	private List<AID> remoteAgents = new ArrayList<>();
    public AgentManagerBean() {
        
    }

	@Override
	public AID startAgent(String name, AID id) {
		Agent agent = (Agent) JNDILookup.lookUp(name, Agent.class);
		return agent.init(id);
	}

	@Override
	public Agent getAgentById(AID agentId) {
		System.out.println("NAsao sam agenta " + agentId.getName());
		return cachedAgents.getRunningAgents().get(agentId);
	}
	@Override
	public Agent getByIdOrStartNew(String name, AID id) {
		System.out.println("trazim sam agenta " + id.getName());

			if(getAgentById(id) == null) {
				Agent agent = (Agent) JNDILookup.lookUp(name, Agent.class);
				agent.init(id);
				return agent;
			}
			else {
				return getAgentById(id);
			}
	}

	@Override
	public void stop(AID name) {
		// TODO Auto-generated method stub
		cachedAgents.stop(name);
		
	}

	@Override
	public List<AgentType> getAgentTypes() {
		
		return cachedAgents.getTypes();
		
	}

	@Override
	public void stopAgentByName(String aid) {
		cachedAgents.removeByName(aid);
		
	}

	@Override
	public void setRemoteRunningAgents(List<AID> agents) {
		
		this.remoteAgents = agents;
		
	}

	@Override
	public List<AID> getRemoteAgents() {
		// TODO Auto-generated method stub
		return remoteAgents;
	}

	@Override
	public void setRemoteAgentTypes(List<AgentType> types) {

		cachedAgents.setRemoteAgentTypes(types);
	}


}
