package io.github.norbipeti.chat.server.db.domain;

import java.util.List;

import javax.persistence.Entity;

@Entity
public class Conversation {
	private List<Message> messsages;
	private List<User> users;

	public List<Message> getMesssages() {
		return messsages;
	}

	public void setMesssages(List<Message> messsages) {
		this.messsages = messsages;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
}
