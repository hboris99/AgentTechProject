package agents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import agentmanager.AgentManagerRemote;
import chatmanager.ChatManagerRemote;
import messagemanager.ACL;
import messagemanager.AgentMessage;
import messagemanager.MessageManagerRemote;
import models.AgentType;
import models.User;
import models.UserMessage;
import sun.management.resources.agent;
import util.JNDILookup;
import ws.WSChat;

@Stateful
@Remote(Agent.class)
public class UserAgent implements Agent, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AID aid;

	@EJB
	private ChatManagerRemote chatManager;
	@EJB
	private CachedAgentsRemote cachedAgents;
	@EJB
	private AgentManagerRemote agentManager;
	@EJB
	private WSChat ws;
	@EJB
	private MessageManagerRemote messageManager;

	@PostConstruct
	public void postConstruct() {
		System.out.println("Created User Agent!");
	}

	//private List<String> chatClients = new ArrayList<String>();

	protected MessageManagerRemote msm() {
		return (MessageManagerRemote) JNDILookup.lookUp(JNDILookup.MessageManagerLookup, MessageManagerRemote.class);
	}

	@Override
	public void handleMessage(ACL message) {
		String username = "";
		String sender = "";
		String content = "";
		String reciever = (String) message.receivers.get(0).getName();
		ACL acl = new ACL();
		if(aid.getName().equals(reciever)) {
			switch(message.getPerformative()) {

			case REGISTER:
				
				username = message.getSender().getName();
				ws.notifyNewRegistration(username);
				break;
			case LOGIN:
				username = message.getSender().getName();
				
				break;
			case GET_LOGGEDIN:
				List<String> activeUsernames = chatManager.getActiveUsernames();
				List<User> activeRemoteUsers = chatManager.loggedInRemote();
				for(String user: activeUsernames) {
						ws.sendMessage(aid.getName(), "LOGIN%" + user);

				}for(User u : activeRemoteUsers) {
						ws.sendMessage(aid.getName(), "LOGIN%" + u);
					}
				
				break;
			case NEW_MESSAGE:
				
				 sender = acl.getSender().getName();
				content = acl.getContent();
				reciever = acl.receivers.get(0).getName();

				UserMessage msg = new UserMessage(reciever, sender, new Date(), "New message", content);
				
				chatManager.saveMessage(msg);
				
				ws.sendMessage(reciever, msg);
				break;
			case GROUP_MESSAGE:
				sender = acl.getSender().getName();
				content = acl.getContent();

				UserMessage msg1 = new UserMessage(reciever, sender, new Date(), "New message", content);
				
				for(String recipient : chatManager.getActiveUsernames()) {
							msg1.setRecipient(recipient);
							chatManager.saveMessage(msg1);
					
				}
				for(User u : chatManager.loggedInRemote()) {
							msg1.setRecipient(u.getUsername());
							chatManager.saveMessage(msg1);
					
				}
				ws.sendMessageToAllActiveUsers(msg1);
				break;
				
			case GET_MESSAGES:
				for(UserMessage msgf : chatManager.getUserMessages(aid.getName())) {
					ws.sendMessage(aid.getName(), msgf);
				}
				break;
				
			case GET_REGISTERED:
				List<String> registeredUsers = chatManager.getRegisteredUsernames();
				for(String registered : registeredUsers) {
					
					System.out.println("These are the registered users: " + registered);
					ws.sendMessage(aid.getName(),"REGISTRATION%" + registered);	
				}
				break;
			case LOGOUT:
				username = message.getSender().getName();
				ws.closeSessionWhenLoggedOut(username);
				break;
			case GET_AGENT_TYPES:
				List<AgentType> agentTypes = agentManager.getAgentTypes();
				for(AgentType type : agentTypes) {
					System.out.println(type.getName());
					ws.sendMessage(aid.getName(),"AGENT_TYPE%" +  type.getName() + ',' + type.getHost());
				}
				break;
			case GET_PERFORMATIVES:
				List<String> performatives = messageManager.getPerformatives();
				for(String p : performatives) {
					System.out.println("Ova performativa postoji " + p);
					ws.sendMessage(aid.getName(), "PERFORMATIVES%" + p);
				}
				break;
			case GET_RUNNING_AGENTS:
				Map<AID, Agent> runningAgents = cachedAgents.getRunningAgents();
				for(Map.Entry<AID, Agent> running : runningAgents.entrySet()) {
					System.out.println("ovo su running agens" + running.getKey().getName());
					ws.sendMessage(aid.getName(), "RUNNING_AGENTS%" + running.getKey().toString());
				}
				
				break;
			default:
				System.out.println("Error selected case does not exist.");
				break;
			
			}
		}
			
					
				
			
		
	}

	@Override
	public AID init(AID aid) {
		this.aid = aid;
		cachedAgents.addRunningAgent(aid, this);
		return aid;
	}

	@Override
	public AID getAID() {
		return this.aid;
	}

	
}
