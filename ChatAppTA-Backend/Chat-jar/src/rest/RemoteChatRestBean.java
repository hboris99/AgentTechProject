package rest;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import agentmanager.AgentManagerBean;
import agentmanager.AgentManagerRemote;
import agents.AID;
import chatmanager.ChatManagerRemote;
import messagemanager.ACL;
import messagemanager.AgentMessage;
import messagemanager.MessageManagerRemote;
import models.AgentType;
import models.UserMessage;
import util.JNDILookup;
@Stateless
@LocalBean

@Path("/chat")
public class RemoteChatRestBean implements RemoteChatRest {
	
	@EJB
	public MessageManagerRemote messageManager;
	
	@EJB
	public ChatManagerRemote chatManager;
	
	private AgentManagerRemote agentManager = JNDILookup.lookUp(JNDILookup.AgentManagerLookup, AgentManagerBean.class);

	@Override
	public void getLoggedUsers(String username) {
		AID aid = new AID(username,chatManager.getHost(), new AgentType("UserAgent", chatManager.getHost().alias));
		agentManager.getByIdOrStartNew(JNDILookup.ChatAgentLookup, aid);

		ACL message = new ACL();
		message.receivers.add(aid);
		message.userArgs.put("receiver", username);
		message.userArgs.put("command", "GET_LOGGEDIN" );		
		messageManager.post(message);
	
	}

	@Override
	public void getRegisteredUsers(String username) {
		AID aid = new AID(username,chatManager.getHost(), new AgentType("UserAgent", chatManager.getHost().alias));
		agentManager.getByIdOrStartNew(JNDILookup.ChatAgentLookup, aid);

		ACL message = new ACL();
		message.receivers.add(aid);
		message.userArgs.put("receiver", username);
		message.userArgs.put("command", "GET_REGISTERED" );
		
		messageManager.post(message);
			
	}

	@Override
	public void getMessages(String username) {
		AID aid = new AID(username,chatManager.getHost(), new AgentType("UserAgent", chatManager.getHost().alias));
		agentManager.getByIdOrStartNew(JNDILookup.ChatAgentLookup, aid);

		ACL message = new ACL();
		message.receivers.add(aid);
		message.userArgs.put("receiver", username);
		message.userArgs.put("command", "GET_MESSAGES");
		
		messageManager.post(message);
		
	}

	@Override
	public void sendMessage(UserMessage userMessage) {

		AID aid = new AID(userMessage.sender,chatManager.getHost(), new AgentType(userMessage.sender, chatManager.getHost().alias));
		agentManager.getByIdOrStartNew(JNDILookup.ChatAgentLookup, aid);
		AID recipient = new AID(userMessage.recipient,chatManager.getHost(), new AgentType(userMessage.recipient, chatManager.getHost().alias));

		ACL message = new ACL();
		message.setSender(aid);
		message.receivers.add(recipient);
		message.setContent(userMessage.content);
		message.userArgs.put("command", "NEW_MESSAGE");
		

		messageManager.post(message);

		
	}

	@Override
	public void logOut(String username) {
		AID aid = new AID("MASTER",chatManager.getHost(), new AgentType("MASTER", chatManager.getHost().alias));
		AID sender = new AID(username, chatManager.getHost(), new AgentType("UserAgent", chatManager.getHost().getAlias()));
		agentManager.getByIdOrStartNew(JNDILookup.ChatAgentLookup, aid);
		ACL message = new ACL();
		message.setSender(sender);
		message.userArgs.put("command", "LOGOUT" );
		boolean res = chatManager.logOut(username);
		if(res) {
			System.out.println("Odlogovan: " + username);
			messageManager.post(message);		

		}
		System.out.println("Logged out");
	}

	@Override
	public void sendMessageToAll(UserMessage userMessage) {
		AID aid = new AID(userMessage.sender,chatManager.getHost(), new AgentType(userMessage.sender, chatManager.getHost().alias));
		agentManager.getByIdOrStartNew(JNDILookup.ChatAgentLookup, aid);

		ACL message = new ACL();
		message.setSender(aid);
		message.userArgs.put("command", "GROUP_MESSAGE");
		
		
		messageManager.post(message);
	}

}
