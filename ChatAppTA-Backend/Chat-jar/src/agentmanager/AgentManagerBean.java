package agentmanager;

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
	
    public AgentManagerBean() {
        
    }

	@Override
	public AID startAgent(String name, AID id) {
		Agent agent = (Agent) JNDILookup.lookUp(name, Agent.class);
		return agent.init(id);
	}

	@Override
	public Agent getAgentById(AID agentId) {
		return cachedAgents.getRunningAgents().get(agentId);
	}

	@Override
	public Agent getByIdOrStartNew(String name, AID id) {
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


}
