package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.naming.NamingException;

import models.AgentType;
import util.JndiTreeParser;

/**
 * Session Bean implementation class CachedAgents
 */
@Singleton
@LocalBean
@Remote(CachedAgentsRemote.class)
public class CachedAgents implements CachedAgentsRemote{

	HashMap<AID, Agent> runningAgents;
	List<AgentType> types;
	
	
	@EJB
	private JndiTreeParser parser;
	/**
	 * Default constructor.
	 */
	public CachedAgents() {
		types = new ArrayList<AgentType>();
		runningAgents = new HashMap<>();
	}
	
	@PostConstruct
	public void postConstruct() {
		types = getAgentTypes();
	}
	
	private List<AgentType> getAgentTypes(){
		try {
			return parser.parse();
		}catch(NamingException e) {
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	public HashMap<AID, Agent> getRunningAgents() {
		return runningAgents;
	}

	@Override
	public void addRunningAgent(AID key, Agent agent) {
		runningAgents.put(key, agent);
	}

	@Override
	public void stop(AID name) {

		runningAgents.remove(name);
	}

	@Override
	public Agent getByAid(AID aid) {
		for(Agent a : runningAgents.values()) {
			if(a.getAID().getName().equals(aid.getName()) &&
			   a.getAID().getHost().getAddress().equals(aid.getHost().getAddress()) &&
			   a.getAID().getAgentType().getName().equals(aid.getAgentType().getName())
			) {
				return a;
			}
		}
		return null;
	}

	@Override
	public List<AgentType> getTypes() {
		// TODO Auto-generated method stub
		return types;
	}

	@Override
	public void removeByName(String name) {
		for(Map.Entry<AID, Agent> entry : runningAgents.entrySet()) {
			if(entry.getKey().getName().equals(name)) {
				runningAgents.remove(entry.getKey());
			}
		}
		
	}

	

}
