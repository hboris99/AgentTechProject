package messagemanager;

import java.util.List;

import javax.ejb.Remote;
import javax.jms.MessageConsumer;
import javax.jms.Session;

@Remote
public interface MessageManagerRemote {
	public void post(ACL msg);
	public Session getSession();
	public MessageConsumer getConsumer();
	public List<String> getPerformatives();
}
