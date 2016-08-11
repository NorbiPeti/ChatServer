package io.github.norbipeti.chat.server.db.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import io.github.norbipeti.chat.server.data.LoaderRef;

@Entity
@Table(name = "MESSAGE")
public class Message implements Serializable {
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

	public void setMessageChunk(LoaderRef<MessageChunk> messagechunk) {
		this.messagechunk = messagechunk;
	}

	public void setMessageChunk(MessageChunk messagechunk) {
		this.messagechunk = new LoaderRef<MessageChunk>(messagechunk);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Message() {
	}

	public static Message create(Conversation parent) {
		Message msg = new Message();
		int size = parent.getMesssageChunks().size();
		MessageChunk chunk;
		if (size == 0 || parent.getMesssageChunks().get(size - 1).getMessages().size() >= MESSAGE_LIMIT_PER_CHUNK) {
			chunk = ManagedData.create(MessageChunk.class);
			chunk.setConversation(parent);
			parent.getMesssageChunks().add(chunk);
		} else
			chunk = parent.getMesssageChunks().get(size - 1);
		msg.setMessageChunk(chunk);
		return msg;
	}
}
