package io.github.norbipeti.chat.server.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import io.github.norbipeti.chat.server.db.domain.*;

public class DataProvider implements AutoCloseable {
	private EntityManagerFactory emf;

	public DataProvider() {
		emf = Persistence.createEntityManagerFactory("ChatServerPU");
	}

	public void addUser(User user) {
		save(user);
	}

	private void save(Object object) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(object);
		em.getTransaction().commit();
		em.close();
	}

	public List<User> getUsers() {
		EntityManager em = emf.createEntityManager();
		TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
		List<User> users = query.getResultList();
		em.close();
		return users;
	}

	public List<User> getUser(Long id) { //TODO
		EntityManager em = emf.createEntityManager();
		TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
		List<User> users = query.getResultList();
		em.close();
		return users;
	}

	public void removeUser(User user) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		User managedUser = em.find(User.class, user.getId());
		em.remove(managedUser);
		em.getTransaction().commit();
		em.close();
	}

	public EntityManager startTransaction() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		return em;
	}

	public void endTransaction(EntityManager em) {
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public void close() {
		if (emf != null)
			emf.close();
	}
}
