package agents;

import javax.ejb.EJB;

import messagemanager.ACL;
import ws.WSChat;

public class MasterAgent implements Agent {
	
	@EJB
	private CachedAgentsRemote cachedAgents;
	@EJB
	private WSChat ws;
	private AID aid;

	public AID getAid() {
		return aid;
	}

	public void setAid(AID aid) {
		this.aid = aid;
	}

	@Override
	public AID init(AID aid) {
		this.aid = aid;
		cachedAgents.addRunningAgent(aid, this);
		return aid;
	}

	@Override
	public void handleMessage(ACL aclMessage) {

		ws.sendMessage(aid.getName(), "FLIGHT%" + aclMessage.getContent());
		
	}

	@Override
	public AID getAID() {
		// TODO Auto-generated method stub
		return aid;
	}
	

}
