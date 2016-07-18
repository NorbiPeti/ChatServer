package io.github.norbipeti.chat.server.db;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DataProvider implements AutoCloseable {
	private EntityManagerFactory emf;

	public DataProvider() {
		Persistence.createEntityManagerFactory("ChatServerPU");
	}

	@Override
	public void close() {
		if (emf != null)
			emf.close();
	}
}
