package io.github.norbipeti.chat.server.db.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import io.github.norbipeti.chat.server.data.DataManager;
import io.github.norbipeti.chat.server.data.LoaderCollection;

@Entity
@Table(name = "\"USER\"")
public class User extends ChatDatabaseEntity {
	private static final long serialVersionUID = 2862762084164225666L;
	// @Id
	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	// @Column(name = "ID", unique = true, nullable = false)
	// private Long id;
	private String name;
	private String email;
	private String password;
	@ElementCollection(fetch = FetchType.EAGER)
	@OneToOne(cascade = CascadeType.ALL)
	private LoaderCollection<User> contacts;
	private String salt;
	// @Column(columnDefinition = "CHAR(16) FOR BIT DATA")
	@Column(columnDefinition = "VARCHAR(64)")
	private String sessionid;
	@ElementCollection(fetch = FetchType.EAGER)
	// @ManyToMany(fetch = FetchType.EAGER, mappedBy = "users")
	@ManyToMany(mappedBy = "USER", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	// @ManyToMany(mappedBy = "users")
	// @JoinTable(name = "User_Conversation", joinColumns =
	// @JoinColumn(referencedColumnName = "id", unique = false),
	// inverseJoinColumns = @JoinColumn(referencedColumnName = "id", unique =
	// false))
	private Set<Conversation> conversations;

	/**
	 * Loads all contact data
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<User> getContacts() throws IOException {
		if (contacts == null)
			contacts = new LoaderCollection<User>(User.class);
		return contacts;
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
		return "User [id=" + getId() + ", name=" + name + ", email=" + email + ", password=" + password + ", contacts="
				+ contacts + ", sessionid=" + sessionid + "]";
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

	public static LoaderCollection<User> getUsers() {
		return DataManager.load(User.class);
	}
}
