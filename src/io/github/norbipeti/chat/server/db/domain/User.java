package io.github.norbipeti.chat.server.db.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "\"User\"")
public class User extends ChatDatabaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	private String name;
	private String email;
	private String password;
	@ElementCollection(fetch = FetchType.EAGER)
	private List<User> contacts;
	private String salt;
	// @Column(columnDefinition = "CHAR(16) FOR BIT DATA")
	@Column(columnDefinition = "VARCHAR(64)")
	private String sessionid;
	@ElementCollection(fetch = FetchType.EAGER)
	// @ManyToMany(fetch = FetchType.EAGER, mappedBy = "users")
	@ManyToMany(mappedBy = "users", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	// @ManyToMany(mappedBy = "users")
	//@JoinTable(name = "User_Conversation", joinColumns = @JoinColumn(referencedColumnName = "id", unique = false), inverseJoinColumns = @JoinColumn(referencedColumnName = "id", unique = false))
	private Set<Conversation> conversations;

	public List<User> getContacts() {
		if (contacts == null)
			contacts = new ArrayList<>();
		return contacts;
	}

	public void setContacts(List<User> contacts) {
		this.contacts = contacts;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String toString() {
		List<String> c = null;
		if (contacts != null) {
			c = new ArrayList<>();
			for (User u : contacts)
				c.add(u.name);
		}
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", contacts=" + c
				+ ", sessionid=" + sessionid + "]";
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public Set<Conversation> getConversations() {
		if (conversations == null)
			conversations = new HashSet<>();
		return conversations;
	}

	public void setConversations(Set<Conversation> conversations) {
		this.conversations = conversations;
	}

	public User() {

	}
}
