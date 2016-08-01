package io.github.norbipeti.chat.server.db.domain;

import java.util.Date;

import javax.persistence.*;

@Entity
public class Message extends ChatDatabaseEntity {
	private static final long serialVersionUID = 6345941601716826570L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	// @JoinTable(name="user_message")
	private User sender;
	private Date time;
	private String message;
	@ManyToOne(fetch = FetchType.EAGER)
	// @JoinTable(name="conversation_message")
	private Conversation conversation;

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
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

	public Conversation getConversation() {
		return conversation;
	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
