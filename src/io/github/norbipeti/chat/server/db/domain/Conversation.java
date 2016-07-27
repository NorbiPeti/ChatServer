package io.github.norbipeti.chat.server.db.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
public class Conversation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	@ElementCollection(fetch=FetchType.EAGER)
	@OneToMany(cascade = CascadeType.ALL)
	private List<Message> messsages;
	@ElementCollection(fetch = FetchType.EAGER)
	@ManyToMany(cascade = CascadeType.ALL)
	private List<User> users;
	@Version
	@GeneratedValue
	private int Version;

	public List<Message> getMesssages() {
		if (messsages == null)
			messsages = new ArrayList<>();
		return messsages;
	}

	public void setMesssages(List<Message> messsages) {
		this.messsages = messsages;
	}

	public List<User> getUsers() {
		if (users == null)
			users = new ArrayList<>();
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
