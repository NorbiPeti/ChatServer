package io.github.norbipeti.chat.server.db.domain;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class SavedData implements Serializable {
	private static long nextID = 0;

	private long id;

	public long getId() {
		return id;
	}

	protected SavedData() {
		id = nextID++;
	}
}
