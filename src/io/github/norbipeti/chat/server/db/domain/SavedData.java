package io.github.norbipeti.chat.server.db.domain;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class SavedData implements Serializable {
	public abstract long getId();

	public abstract void setId(long id);
}
