package io.github.norbipeti.chat.server.db.domain;

import javax.persistence.*;

@Entity
public class Contact {
	@ManyToOne
	private User user1;
	@ManyToOne
	private User user2;

	public User getUser1() {
		return user1;
	}

	public void setUser1(User user1) {
		this.user1 = user1;
	}

	public User getUser2() {
		return user2;
	}

	public void setUser2(User user2) {
		this.user2 = user2;
	}

	@Id
	private Long getId() {
		if (user1 == null)
			return null;
		return user1.getId();
	}

	public Contact() {

	}
}
