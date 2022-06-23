package ws;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import agentmanager.AgentManagerBean;
import agentmanager.AgentManagerRemote;
import chatmanager.ChatManagerRemote;
import models.UserMessage;
import sun.management.resources.agent;
import util.JNDILookup;

@Singleton
@LocalBean
@ServerEndpoint("/ws/{username}")
public class WSChat {
	private Map<String, Session> sessions = new HashMap<String, Session>();
	
	@EJB
	private ChatManagerRemote chatManager;
	
	@OnOpen
	public void onOpen(@PathParam("username") String username, Session session) {
		sessions.put(username, session);
		
	}
	
	@OnClose
	public void onClose(@PathParam("username") String username, Session session) {
		sessions.remove(username);
		
	}
	
	@OnError
	public void onError(@PathParam("username") String username, Session session, Throwable t) {
		sessions.remove(username);
		
		t.printStackTrace();
	}
	
	public void closeSessionWhenLoggedOut(String username) {
		sessions.remove(username);
		this.notifyLogOut(username);
	}
	
	
	
	public void sendMessage(String username, UserMessage message) {
		Session session = sessions.get(username);
		System.out.println("Web socket part looking for messages for: " + username);
		System.out.println("Found messsage: " + message.subject);
		if(session != null && session.isOpen()) {
			try {
				session.getBasicRemote().sendText(message.toJson());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void notifyNewRegistration(String username) {
		for(Session session :sessions.values()) {
			if(session != null && session.isOpen()) {
				try {
					session.getBasicRemote().sendText("REGISTRATION%" + username);
					System.out.println("REGISTRATION");
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void notifyNewLogin(String username) {
		for(Session session :sessions.values()) {
			if(session != null && session.isOpen()) {
				try {
					session.getBasicRemote().sendText("LOGIN%" + username);
					System.out.println("LOGIN");

				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void notifyLogOut(String username) {
		for(Session session :sessions.values()) {
			if(session != null && session.isOpen()) {
				try {
					session.getBasicRemote().sendText("LOGOUT%" + username);
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void sendMessageToAllActiveUsers(UserMessage userMessage) {
		for(Session session : sessions.values()) {
			if(session != null && session.isOpen()) {
				try {
					session.getBasicRemote().sendText(userMessage.toJson());
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void sendMessage(String recipient, String message) {
		Session session = sessions.get(recipient);
		if(session != null && session.isOpen()) {
			try {
				System.out.println(message);
				session.getBasicRemote().sendText(message);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
