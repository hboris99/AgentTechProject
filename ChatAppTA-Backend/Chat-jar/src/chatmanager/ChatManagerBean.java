package chatmanager;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import ConnectionManager.ConnectionManager;
import models.Host;
import models.User;
import models.UserMessage;
import ws.WSChat;

// TODO Implement the rest of Client-Server functionalities 
/**
 * Session Bean implementation class ChatBean
 */
@Singleton
@LocalBean
public class ChatManagerBean implements ChatManagerRemote {

	private List<User> registered = new ArrayList<>();
	private List<User> loggedIn = new ArrayList<>();
	
	private List<User> loggedInRemote = new ArrayList<>();
	private List<UserMessage> messages = new ArrayList<>();
	
	@EJB
	private WSChat ws;
	
	@EJB
	private ConnectionManager connectionManager;
	
	/**
	 * Default constructor.
	 * @throws ParseException 
	 */
	public ChatManagerBean() throws ParseException {
		
		User u1 = new User("boris", "boris");
		User u2 = new User("zoki", "zoki");
		User u3 = new User("majmun", "majmun");
		registered.add(u1);
		registered.add(u2);
		registered.add(u3);
		
		UserMessage um1 = new UserMessage();
		um1.setSender("boris");
		um1.setRecipient("zoki");
		um1.setContent("neki kontent");
		um1.setSubject("neki subject");
		um1.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("24/05/2022"));
		
		UserMessage um2 = new UserMessage();
		um2.setSender("boris");
		um2.setRecipient("majmun");
		um2.setContent("neki kontent");
		um2.setSubject("neki subject");
		um2.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("24/05/2022"));
		UserMessage um3 = new UserMessage();
		um3.setSender("zoki");
		um3.setRecipient("boris");
		um3.setContent("neki kontent");
		um3.setSubject("neki subject");
		um3.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("24/05/2022"));
		UserMessage um4 = new UserMessage();
		um4.setSender("majmun");
		um4.setRecipient("boris");
		um4.setContent("neki kontent");
		um4.setSubject("neki subject");
		um4.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("24/05/2022"));
		messages.add(um1);
		messages.add(um2);
		messages.add(um3);
		messages.add(um4);
	}

	@Override
	public boolean register(User user) {
		boolean exists = registered.stream().anyMatch(u->u.getUsername().equals(user.getUsername()));
		System.out.println(exists);
		if(exists) {
			return false;
		}
		registered.add(user);
		return true;
	}

	@Override
	public String login(String username, String password) {
		boolean exists = registered.stream().anyMatch(u->u.getUsername().equals(username) && u.getPassword().equals(password));
		if(exists) {
			if(!activeUser(username)) {
			loggedIn.add(getRegisteredByUsername(username));
			ws.notifyNewLogin(username);
			sendLogInToNetwork(username);
			return "bravo";
			}
			return "fail";
		}
		return "invalid";
	}
	
	

	private boolean activeUser(String username) {
		System.out.println("Loogin for an active user by the name: " + username);
		for(String activeUser: getActiveUsernames()) {
			if(activeUser.equals(username)) {
				System.out.println("Found the logged in user: " + activeUser);
				return true;
			}
		}
		
		for(User activeUser : loggedInRemote()) {
			if(activeUser.getUsername().equals(username))
				return true;
		}
		return false;
	}

	@Override
	public boolean logOut(String username) {
		System.out.println("Went into log out");
		if (activeUser(username)) {
			System.out.println("Found the active user requestion logout: " + username);
			loggedIn.removeIf(u -> u.username.equals(username));
			sendLogOutToNetwork(username);
			return true;
		} else {
			return false;
		}			
	}



	

	

	private User getRegisteredByUsername(String username) {
	
		for(User u : registered) {
			if(u.getUsername().equals(username))
				return u;
		}
		return null;
	}

	@Override
	public void addRemoteLoggedIn(User user) {

		loggedInRemote.add(new User(user.getUsername(), user.getPassword()));
		ws.notifyNewLogin(user.getUsername());
	}

	@Override
	public void removeRemoteActive(String username) {

		loggedInRemote.removeIf(u -> u.getUsername().equals(username));
		ws.notifyLogOut(username);
	}

	@Override
	public List<String> getActiveUsernames() {
		
		List<String> activeUsernames = new ArrayList<>();
		for(User user : loggedIn) {
			System.out.println("User is logged in: " + user.getUsername());
			activeUsernames.add(user.getUsername());
		}
		return activeUsernames;
	}

	@Override
	public List<String> getRegisteredUsernames() {
		List<String> usernames = new ArrayList<>();
		for(User user : registered) {
			usernames.add(user.getUsername());
		}
		return usernames;
	}

	@Override
	public List<UserMessage> getUserMessages(String username) {
		System.out.println("Getting all of the messages for: " + username);
		List<UserMessage> userMessages = new ArrayList<>();
		for(UserMessage message : messages) {
			if(message.recipient.equals(username))
				userMessages.add(message);
		}
		return userMessages;
	}

	@Override
	public void saveRemoteMessage(UserMessage userMessage) {

		messages.add(userMessage);
		ws.sendMessage(userMessage.recipient, userMessage);
	}

	@Override
	public void saveMessage(UserMessage msg) {
		
		messages.add(msg);
		sendMessageToNetwork(msg);
		
	}

	@Override
	public List<User> getActiveUsers() {
		
		return loggedIn;
	}

	@Override
	public void logOutFromNode(String nodeAlias) {
		for(User u: loggedInRemote) {
			if(u.password.equals(nodeAlias)) {
				ws.notifyLogOut(u.username);
			}
		}
		loggedInRemote.removeIf(u -> u.password.equals(nodeAlias));
	}

	@Override
	public List<User> loggedInRemote() {
		return loggedInRemote;
	}

	@Override
	public void sendLogInToNetwork(String username) {

		connectionManager.notifyAllNewLogin(username);
		
	}
	@Override
	public Host getHost() {
		String address = "";
		try {
			MBeanServer mbean = ManagementFactory.getPlatformMBeanServer();
			ObjectName a = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");
			address = (String) mbean.getAttribute(a, "boundAddress");
		}catch (MalformedObjectNameException | InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException e) {
			e.printStackTrace();
		}	
		return new Host(address, System.getProperty("jboss.node.name") + ":8080" );
	}

	@Override
	public void sendLogOutToNetwork(String username) {
		connectionManager.notifyAllLogout(username);
		
	}

	@Override
	public void sendMessageToNetwork(UserMessage msg) {

		connectionManager.notifyAllNewMessage(msg);
	}

}
