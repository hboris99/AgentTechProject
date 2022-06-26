package chatmanager;

import java.util.List;

import javax.ejb.Remote;

import models.Host;
import models.User;
import models.UserMessage;

@Remote
public interface ChatManagerRemote {

	public String login(String username, String password);
	
	public void addRemoteLoggedIn(User user);
	
	public void removeRemoteActive(String username);
	
	public boolean register(User user);

	public List<String> getActiveUsernames();
	
	public List<String> getRegisteredUsernames();
	
	
	public boolean logOut(String username);

	public List<UserMessage> getUserMessages(String username);
	
	public void saveRemoteMessage(UserMessage userMessage);
	
	public void saveMessage(UserMessage msg);
	
	public List<User> getActiveUsers();
	
	public void logOutFromNode(String nodeAlias);
	
	public List<User> loggedInRemote();
	
	public void sendLogInToNetwork(String username);
	
	public void sendLogOutToNetwork(String username);
	
	public void sendMessageToNetwork(UserMessage msg);
	
	public Host getHost();
	
}
