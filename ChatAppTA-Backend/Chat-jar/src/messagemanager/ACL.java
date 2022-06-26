package messagemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agents.AID;
import agents.Performative;

public class ACL implements Serializable {
	private Performative performative;
	private AID sender;
	public List<AID> receivers;
	private AID replyTo;
	private String content;
	private Object contentObject;
	public HashMap<String, Object> userArgs;
	private String language;
	private String encoding;
	private String ontology;
	private String protocol;
	private String conversationId;
	private String replyWith;
	private String inReplyTo;
	public Performative getPerformative() {
		return performative;
	}
	public void setPerformative(Performative performative) {
		this.performative = performative;
	}
	public AID getSender() {
		return sender;
	}
	public void setSender(AID sender) {
		this.sender = sender;
	}
	public List<AID> getReceivers() {
		return receivers;
	}
	public void setReceivers(List<AID> receivers) {
		this.receivers = receivers;
	}
	public AID getReplyTo() {
		return replyTo;
	}
	public void setReplyTo(AID replyTo) {
		this.replyTo = replyTo;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Object getContentObject() {
		return contentObject;
	}
	public void setContentObject(Object contentObject) {
		this.contentObject = contentObject;
	}
	public Object getUserArgs(String key) {
		return userArgs.get(key);
	}
	public void setUserArgs(HashMap<String, Object> userArgs) {
		this.userArgs = userArgs;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getOntology() {
		return ontology;
	}
	public void setOntology(String ontology) {
		this.ontology = ontology;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getConversationId() {
		return conversationId;
	}
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	public String getReplyWith() {
		return replyWith;
	}
	public void setReplyWith(String replyWith) {
		this.replyWith = replyWith;
	}
	public String getInReplyTo() {
		return inReplyTo;
	}
	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}
	public Long getReplyBy() {
		return replyBy;
	}
	public void setReplyBy(Long replyBy) {
		this.replyBy = replyBy;
	}
	public ACL(Performative performative, AID sender,List<AID> receivers, AID replyTo, String content,
			Object contentObject, HashMap<String, Object> userArgs, String language, String encoding, String ontology,
			String protocol, String conversationId, String replyWith, String inReplyTo, Long replyBy) {
		super();
		this.performative = performative;
		this.sender = sender;
		this.receivers = receivers;
		this.replyTo = replyTo;
		this.content = content;
		this.contentObject = contentObject;
		this.userArgs = userArgs;
		this.language = language;
		this.encoding = encoding;
		this.ontology = ontology;
		this.protocol = protocol;
		this.conversationId = conversationId;
		this.replyWith = replyWith;
		this.inReplyTo = inReplyTo;
		this.replyBy = replyBy;
	}
	public ACL() {
		super();
		this.receivers = new ArrayList<>();
		this.userArgs = new  HashMap<>();
	}
	private Long replyBy;
}
