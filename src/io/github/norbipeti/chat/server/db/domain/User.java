package io.github.norbipeti.chat.server.db.domain;

import java.io.IOException;
import java.util.List;
import javax.persistence.*;

import io.github.norbipeti.chat.server.data.DataManager;
import io.github.norbipeti.chat.server.data.LoaderCollection;

@Entity
@Table(name = "\"USER\"")
public class User extends SavedData {
	private static final long serialVersionUID = 2862762084164225666L;
	private static Long nextid = 0L;
	private Long id = nextid++;
	private String name;
	private String email;
	private String password;
	@ElementCollection(fetch = FetchType.EAGER)
	@OneToOne(cascade = CascadeType.ALL)
	private LoaderCollection<User> contacts = new LoaderCollection<User>(User.class);
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
	private LoaderCollection<Conversation> conversations = new LoaderCollection<>(Conversation.class);

	/**
	 * Loads all contact data
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<User> getContacts() throws IOException {
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

	public LoaderCollection<Conversation> getConversations() {
		return conversations;
	}

	public User() {
	}

	public static LoaderCollection<User> getUsers() {
		return DataManager.load(User.class);
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}
}
