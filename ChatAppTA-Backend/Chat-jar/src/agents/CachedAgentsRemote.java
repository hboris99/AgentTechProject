package agents;

import java.util.HashMap;
import java.util.List;

import models.AgentType;

public interface CachedAgentsRemote {

	public HashMap<AID, Agent> getRunningAgents();
	public void addRunningAgent(AID aid, Agent agent);
	public void stop(AID name);
	public Agent getByAid(AID aid);
	public List<AgentType> getTypes();
	public void removeByName(String name);
}
