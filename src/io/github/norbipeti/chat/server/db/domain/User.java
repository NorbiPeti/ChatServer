package io.github.norbipeti.chat.server.db.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;

@Entity
@Table(name = "\"User\"")
public class User {
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
	@Column(columnDefinition = "CHAR(16) FOR BIT DATA")
	private UUID sessionid;
	
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
				+ ", sessionid=" + sessionid + "]"; // TODO: SessionID null
													// after getting from db
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

	public UUID getSessionid() {
		return sessionid;
	}

	public void setSessionid(UUID sessionid) {
		this.sessionid = sessionid;
	}

	public User() {

	}
}
