package io.github.norbipeti.chat.server.db.domain;

import java.util.List;

import javax.persistence.*;

@Entity
public class Conversation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	@ElementCollection
	@OneToMany
	private List<Message> messsages;
	@ElementCollection
	@OneToMany
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
