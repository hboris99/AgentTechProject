package messagemanager;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import agents.AID;
import agents.Agent;
import agents.CachedAgentsRemote;

/**
 * Message-Driven Bean implementation class for: MDBConsumer
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/topic/publicTopic") })
public class MDBConsumer implements MessageListener {


	@EJB
	private CachedAgentsRemote cachedAgents;
	/**
	 * Default constructor.
	 */
	public MDBConsumer() {

	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		try {
				process(message);
			 }
		catch(JMSException e) {
			e.printStackTrace();
		}
	} 
		
	

	public void process(Message message) throws JMSException{
		ACL acl = (ACL) ((ObjectMessage) message).getObject();
		 for(AID aid : acl.getReceivers()) {
			 System.out.println(aid.getName());
			 Agent agent = cachedAgents.getByAid(aid);
			 if(agent == null) {
				 System.out.println("Ti ne licis ni na jednu");
			 }else {
				 agent.handleMessage(acl);
			 }

		 }
	}
}
