package rest;

import java.util.Map;

import javax.ejb.Remote;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import agents.AID;
import messagemanager.ACL;
@Remote
public interface AgentRest {
	@GET
	@Path("/classes/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public void getAvailableAgentTypes(@PathParam("username") String username);

	@GET
	@Path("/running/{username}")
	public void getRunningAgents(@PathParam("username") String username);
	
	@PUT
	@Path("/running/{type}/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response startAgent(@PathParam("type") String type, @PathParam("name") String name);
	
	@PUT
	@Path("/running/{username}")
	public Response stopAgent(@PathParam("username") String username);
	
	@POST
	@Path("/messages")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendACL(ACL message);
	
	@GET
	@Path("/messages")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getPerformatives( Map<String, String> username);
}
