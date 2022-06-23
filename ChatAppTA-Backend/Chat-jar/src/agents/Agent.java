package agents;

import java.io.Serializable;

import javax.jms.Message;

import messagemanager.ACL;

public interface Agent extends Serializable {

	public AID init(AID aid);
	public void handleMessage(ACL aclMessage);
	public AID getAID();
}
