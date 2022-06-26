package ConnectionManager;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.ws.rs.Path;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agentmanager.AgentManagerRemote;
import agents.AID;
import agents.Performative;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import chatmanager.ChatManagerRemote;
import messagemanager.ACL;
import models.AgentType;
import models.Host;
import models.User;
import models.UserMessage;
import ws.WSChat;
@Singleton
@Startup
@Remote(ConnectionManager.class)
@Path("/connection")
public class ConnectionManagerBean implements ConnectionManager{
	
	private Host localNode;
	
	private List<String> cluster = new ArrayList<String>();
	
	private static final String HTTP = "http://";
	private static final String PORT = ":8080";
	
	
	@EJB
	private ChatManagerRemote chatManager;
	
	@EJB
	private WSChat ws;
	@EJB
	private AgentManagerRemote agm;
	
	
	@PostConstruct
	public void initialize() {
		String alias = getNodeAlias() + PORT;
		String address = getNodeAddress();

		localNode = new Host(alias, address);
		
		System.out.println("StARTED NODE: " + localNode.alias + " AT " + localNode.address);
		
		String masterAlias = getMasterAlias();
		System.out.println(masterAlias + " this is the master alias");
		boolean isNotNull = masterAlias != null;
		boolean isNotHost = !masterAlias.equals(PORT);
		System.out.println("Master node is not null: " + isNotNull + " and it is not equal to the port: " + isNotHost);
		if(masterAlias != null && !masterAlias.equals(PORT)) {
			System.out.println("I am entering the if in connection manager initalization");
			ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
			ResteasyWebTarget resteasyWebTarget = resteasyClient.target(HTTP + masterAlias + "/Chat-war/api/connection");
			ConnectionManager manager = resteasyWebTarget.proxy(ConnectionManager.class);
			System.out.println("I've pinged the master node: " + masterAlias);
			cluster = manager.registerNewnode(localNode.alias);
			cluster.add(masterAlias);
			cluster.removeIf(n -> n.equals(localNode.alias));
			resteasyClient.close();
			
			
		}
	}
	

	@PreDestroy
	public void nodeShutDown(String alias) {
		tellAllNodesShutDown(alias);
	}
	
	private void tellAllNodesShutDown(String alias) {

		for(String node : cluster) {
			ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = resteasyClient.target(HTTP + node + "/Chat-war/api/connection");
			ConnectionManager manager = target.proxy(ConnectionManager.class);
			manager.deleteNode(alias);
			resteasyClient.close();
		}
	}


	private String getMasterAlias() {
		try {
			InputStream inputStream = ConnectionManagerBean.class.getClassLoader().getResourceAsStream("../properties/connection.properties");
			Properties properties = new Properties();
			properties.load(inputStream);
			inputStream.close();
			return properties.getProperty("master") + PORT;
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
			}
	}


	private String getNodeAlias() {
		return System.getProperty("jboss.node.name");
	}


	private String getNodeAddress() {
		try {
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			ObjectName http = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");
			return (String) mBeanServer.getAttribute(http, "boundAddress");
		} catch (MalformedObjectNameException | InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public List<String> registerNewnode(String nodeAlias) {
		
		addNode(nodeAlias);
		for(String node: cluster) {
			if(!node.equals(nodeAlias)) {
				ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
				ResteasyWebTarget target = resteasyClient.target(HTTP + node + "/Chat-war/api/connection");
				ConnectionManager manager = target.proxy(ConnectionManager.class);
				manager.addNode(nodeAlias);
				resteasyClient.close();
			}
		}
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
				ResteasyWebTarget target = resteasyClient.target(HTTP + nodeAlias + "/Chat-war/api/connection");
				ConnectionManager manager = target.proxy(ConnectionManager.class);
				
				for(String username : chatManager.getActiveUsernames()) {
					User activeUser = new User(username, localNode.alias);
					manager.addRemoteLogin(activeUser);
				}
				for(User user : chatManager.loggedInRemote()) {
					
					manager.addRemoteLogin(user);
				}
				manager.runningAgentsRemote(agm.getRemoteAgents());
				List<AgentType> types = manager.getAgentTypes();
				agm.setRemoteAgentTypes(types);
				resteasyClient.close();
			}
			
		}).start();
		
		return getNodes();
		
	}
	
	
	

	@Override
	public void addNode(String nodeAlias) {
		if (!nodeAlias.equals(localNode.getAlias())) {
			cluster.add(nodeAlias);
		}		
	}

	@Override
	public List<String> getNodes() {
		return cluster;
	}


	@Override
	public void deleteNode(String node) {

		cluster.removeIf(c -> c.equals(node));
		chatManager.logOutFromNode(node);
	}
	
	
	private void notifyShutDown(String node) {
		for(String nodes : cluster) {
			ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = resteasyClient.target(HTTP + nodes + "/Chat-war/api/connection");
			ConnectionManager manager = target.proxy(ConnectionManager.class);
			manager.deleteNode(node);
			resteasyClient.close();
		}
	}
	
	@Schedule(hour="*", minute="*/1", persistent=false)
	private void heartbeat() {
		for(String node : cluster) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
					ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
					ResteasyWebTarget target = resteasyClient.target(HTTP + node + "/Chat-war/api/connection");
					ConnectionManager manager = target.proxy(ConnectionManager.class);
					manager.ping();
					resteasyClient.close();
					}catch(Exception e) {
						try {
							ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
							ResteasyWebTarget target = resteasyClient.target(HTTP + node + "/Chat-war/api/connection");
							ConnectionManager manager = target.proxy(ConnectionManager.class);
							manager.ping();
							System.out.println("Node " + node + " is alive.");
							resteasyClient.close();
						}catch(Exception e2) {
							cluster.remove(node);
							notifyShutDown(node);
						}
					}
					
				}
				
			});
		}
	}
	
	
	@Override
	public boolean ping() {
	
		System.out.println("Pinging");
		return true;
	}

	@Override
	public void addRemoteLogin(User user) {
		chatManager.addRemoteLoggedIn(user);
		
	}

	@Override
	public void removeRemoteLogin(String user) {
		
		chatManager.removeRemoteActive(user);
		
	}

	@Override
	public void addRemoteMessage(UserMessage msg) {
		
		chatManager.saveRemoteMessage(msg);
		
	}

	@Override
	public void notifyAllNewMessage(UserMessage msg) {
		for(String node :cluster) {
			if(!node.equals(localNode.alias)) {
			ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = resteasyClient.target(HTTP + node + "/Chat-war/api/connection");
			ConnectionManager manager = target.proxy(ConnectionManager.class);
			manager.addRemoteMessage(msg);
			resteasyClient.close();
			}
		}
		
		
	}

	@Override
	public void notifyAllNewLogin(String user) {

		User loggedInUser = new User(user, localNode.alias);
		for(String node :cluster) {
			if(!node.equals(localNode.alias)) {
			ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = resteasyClient.target(HTTP + node + "/Chat-war/api/connection");
			ConnectionManager manager = target.proxy(ConnectionManager.class);
			manager.addRemoteLogin(loggedInUser);
			resteasyClient.close();
			}
		}
		
	}

	@Override
	public void notifyAllLogout(String user) {
		for(String node :cluster) {
			if(!node.equals(localNode.alias)) {
			ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = resteasyClient.target(HTTP + node + "/Chat-war/api/connection");
			ConnectionManager manager = target.proxy(ConnectionManager.class);
			manager.removeRemoteLogin(user);
			resteasyClient.close();
			}
		}		
	}


	@Override
	public void runningAgentsRemote(List<AID> agents) {
		
		agm.setRemoteRunningAgents(agents);
	
		
	}


	@Override
	public void agentTypesRemote(List<AgentType> agentTypes) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void removeAgentRemote(String nodeAlias) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void sendNewAgentToNetwork() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void sendAgentTypesToNetwork() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<AgentType> getAgentTypes() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void removeAgent(String nodeAlias) {
		// TODO Auto-generated method stub
		
	}

}
