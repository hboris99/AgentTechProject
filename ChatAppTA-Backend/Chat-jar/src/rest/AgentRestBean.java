package rest;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import agentmanager.AgentManagerBean;
import agentmanager.AgentManagerRemote;
import agents.AID;
import agents.Agent;
import agents.Performative;
import chatmanager.ChatManagerRemote;
import messagemanager.ACL;
import messagemanager.MessageManagerRemote;
import models.AgentType;
import models.Host;
import models.User;
import sun.management.resources.agent;
import util.JNDILookup;
@Stateless
@LocalBean
@Path("/agents")
public class AgentRestBean implements AgentRest {
	@EJB
	public MessageManagerRemote messageManager;
	
	@EJB
	public ChatManagerRemote chatManager;
	
	private AgentManagerRemote agentManager = JNDILookup.lookUp(JNDILookup.AgentManagerLookup, AgentManagerBean.class);

	@Override
	public void getAvailableAgentTypes(String username) {
		AID aid = new AID(username,chatManager.getHost(), new AgentType("UserAgent", chatManager.getHost().alias));

		ACL message = new ACL();	
		message.receivers.add(aid);
		message.setPerformative(Performative.GET_AGENT_TYPES);

		messageManager.post(message);

	}

	@Override
	public void getRunningAgents(String username) {
		AID aid = new AID(username,chatManager.getHost(), new AgentType("UserAgent", chatManager.getHost().alias));

		ACL message = new ACL();	
		message.receivers.add(aid);
		message.setPerformative(Performative.GET_RUNNING_AGENTS);
		messageManager.post(message);		
	}

	@Override
	public Response startAgent(String type, String name) {
		Host host = chatManager.getHost();
		String aname = "ejb:Chat-ear/Chat-jar//" + type + "!" + Agent.class.getName() + "?stateful";
		AID aid = new AID(name, host, new AgentType(type, host.alias));
		System.out.println(name);
		System.out.println(host.getAddress());
		System.out.println(host.alias);
		agentManager.startAgent(aname, aid);
		
		ACL acl = new ACL();
		acl.setPerformative(Performative.GET_RUNNING_AGENTS);
		for(User u : chatManager.getActiveUsers()) {
			AID aid1 = new AID(u.getUsername(), host, new AgentType("UserAgent", host.alias));
			acl.receivers.add(aid1);
		}
		
		messageManager.post(acl);
		return Response.status(Status.OK).build();
	}

	@Override
	public Response stopAgent(String aid) {
		Host host = chatManager.getHost();
		agentManager.stopAgentByName(aid);
		ACL acl = new ACL();
		acl.setPerformative(Performative.GET_RUNNING_AGENTS);
		for(User u : chatManager.getActiveUsers()) {
			AID aid1 = new AID(u.getUsername(), host, new AgentType("UserAgent", host.alias));
			acl.receivers.add(aid1);
		}
		
		messageManager.post(acl);
		return Response.status(Status.OK).build();
		
	}

	@Override
	public void sendACL(ACL message) {
		
		messageManager.post(message);
	}

	@Override
	public void getPerformatives(Map<String,String> username) {
		String ime = username.get("username");
		AID aid = new AID(ime,chatManager.getHost(), new AgentType("UserAgent", chatManager.getHost().alias));
		System.out.println("Ovo mu saljem u restu" + ime);
		ACL message = new ACL();	
		message.receivers.add(aid);
		message.setPerformative(Performative.GET_PERFORMATIVES);
		messageManager.post(message);				
	}

}
