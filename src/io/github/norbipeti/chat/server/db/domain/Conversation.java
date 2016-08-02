package io.github.norbipeti.chat.server.db.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "CONVERSATION")
public class Conversation extends ChatDatabaseEntity {
	private static final long serialVersionUID = 5058682475353799722L;
	//@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@Column(name = "ID", unique = true, nullable = false)
	//private Long id;
	@ElementCollection(fetch = FetchType.EAGER)
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "conversation")
	private List<Message> messsages;
	@ElementCollection(fetch = FetchType.EAGER)
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	// @JoinTable(name = "User_Conversation", joinColumns = @JoinColumn(name =
	// "conversation_id", referencedColumnName = "id", unique = false),
	// inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName =
	// "id", unique = false), uniqueConstraints = @UniqueConstraint(name =
	// "USER_CONV_UN", columnNames = {"user_id", "conversation_id" }))
	// @JoinTable(name = "User_Conversation", joinColumns = @JoinColumn(name =
	// "conversation_id", referencedColumnName = "id"), inverseJoinColumns =
	// @JoinColumn(name = "user_id", referencedColumnName = "id"))
	// @JoinTable(name = "User_Conversation")
	private Set<User> users;

	public List<Message> getMesssages() {
		if (messsages == null)
			messsages = new ArrayList<>();
		return messsages;
	}

	public void setMesssages(List<Message> messsages) {
		this.messsages = messsages;
	}

	public Set<User> getUsers() {
		if (users == null)
			users = new HashSet<>();
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	/*public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}*/
}
