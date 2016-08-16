package io.github.norbipeti.chat.server.db.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.*;

import org.jsoup.nodes.Element;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import io.github.norbipeti.chat.server.Main;
import io.github.norbipeti.chat.server.data.LoaderRef;

@Entity
@Table(name = "MESSAGE")
public class Message extends ManagedData {
	private static final int MESSAGE_LIMIT_PER_CHUNK = 50;
	private static final long serialVersionUID = 6345941601716826570L;
	private static Long nextid = 0L;
	// @Id
	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	// @Column(name = "ID", unique = true, nullable = false)
	private Long id = nextid++;
	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	// @JoinTable(name="user_message")
	private LoaderRef<User> sender;
	private Date time;
	private String message;
	@ManyToOne(fetch = FetchType.EAGER)
	// @JoinTable(name="conversation_message")
	private LoaderRef<MessageChunk> messagechunk;

	public LoaderRef<User> getSender() {
		return sender;
	}

	public void setSender(LoaderRef<User> sender) {
		this.sender = sender;
	}

	public void setSender(User sender) {
		this.sender = new LoaderRef<User>(sender);
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LoaderRef<MessageChunk> getMessageChunk() {
		return messagechunk;
	}

	public void setConversation(LoaderRef<Conversation> conversation) {
		setConv(conversation.get());
	}

	public void setConversation(Conversation conversation) {
		setConv(conversation);
	}

	private void setConv(Conversation parent) {
		int size = parent.getMesssageChunks().size();
		MessageChunk chunk;
		if (size == 0 || parent.getMesssageChunks().get(size - 1).getMessages().size() >= MESSAGE_LIMIT_PER_CHUNK) {
			chunk = ManagedData.create(MessageChunk.class);
			chunk.setConversation(parent);
			parent.getMesssageChunks().add(chunk);
		} else
			chunk = parent.getMesssageChunks().get(size - 1);
		this.messagechunk = new LoaderRef<MessageChunk>(chunk);
		chunk.getMessages().add(this);
	}

	public long getId() {
		return id;
	}

	protected void setId(long id) {
		this.id = id;
	}

	private Message() {
	}

	@Override
	protected void init() {
	}

	public Element getAsHTML(Element channelmessages) {
		Element msgelement = channelmessages.appendElement("div");
		msgelement.addClass("chmessage");
		Element header = msgelement.appendElement("p");
		header.text(getSender().get().getName() + " - ");
		header.appendElement("span").addClass("converttime").attr("data-val", formatDate());
		Element body = msgelement.appendElement("p");
		body.text(getMessage());
		return msgelement;
	}

	/**
	 * 
	 * @Deprecated Why send it as JSON then convert it to HTML?
	 */
	@Deprecated
	public JsonObject getAsJson() {
		JsonObject msgobj = new JsonObject();
		msgobj.add("sender", Main.gson.toJsonTree(getSender().get()));
		msgobj.add("message", new JsonPrimitive(getMessage()));
		msgobj.add("time", new JsonPrimitive(formatDate()));
		return msgobj;
	}

	private String formatDate() {
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return isoFormat.format(getTime()) + "+00:00";
	}
}
