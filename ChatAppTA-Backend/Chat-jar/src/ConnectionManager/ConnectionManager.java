package ConnectionManager;

import java.util.List;

import javax.ws.rs.Produces; 
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import agents.AID;
import models.AgentType;
import models.User;
import models.UserMessage;

public interface ConnectionManager {
	
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<String> registerNewnode(String nodeAlias);
	
	@POST
	@Path("/node")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void addNode(String nodeAlias);
	
	@POST
	@Path("/nodes")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<String> getNodes();

	@DELETE
	@Path("/node")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteNode(String node);
	
	
	@GET
	@Path("/ping")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean ping();
	
	@POST
	@Path("/users/loggedIn")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void addRemoteLogin(User user);
	
	
	@DELETE
	@Path("/users/loggedIn")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeRemoteLogin(String node);
	
	@POST
	@Path("/message")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void addRemoteMessage(UserMessage msg);
	
	@POST
	@Path("/notify/message")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void notifyAllNewMessage(UserMessage msg);
	
	@POST
	@Path("/notify/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void notifyAllNewLogin(String user);
	
	@POST
	@Path("/notify/logout")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void notifyAllLogout(String user);
	
	@POST
	@Path("/agents/runningAgents")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void runningAgentsForNodes(List<AID> agents);
	
	@POST
	@Path("/agents/agentTypes")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void agentTypesForNodes(List<AgentType> agentTypes);

	@POST
	@Path("/agents/removeAgentType")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	void removeAgentType(String nodeAlias);
	
	@POST
	@Path("/node/startAgent")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void agentRunningNofityNodes();
	
	@POST
	@Path("/node/agentTypes")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void agentTypesNofityNodes();
	
	@GET
	@Path("/agents/agentTypes")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<AgentType> getAgentTypes();
	@DELETE
	@Path("/agent/{alias}")
	public void removeAgent(@PathParam("alias") String nodeAlias);
}
