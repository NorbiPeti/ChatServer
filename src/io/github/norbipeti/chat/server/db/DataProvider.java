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
		return get(User.class);
	}

	public List<Message> getMessages() {
		return get(Message.class);
	}

	public List<Conversation> getConversations() {
		return get(Conversation.class);
	}

	private <T> List<T> get(Class<T> cl) {
		EntityManager em = emf.createEntityManager();
		TypedQuery<T> query = em.createQuery("SELECT x FROM " + cl.getSimpleName() + " x", cl);
		List<T> results = query.getResultList();
		em.close();
		return results;
	}

	public void removeUser(User user) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		User managedUser = em.find(User.class, user.getId());
		em.remove(managedUser);
		em.getTransaction().commit();
		em.close();
	}

	public User getUser(Long id) {
		EntityManager em = emf.createEntityManager();
		User managedUser = em.find(User.class, id);
		em.close();
		return managedUser;
	}

	@Override
	public void close() {
		if (emf != null)
			emf.close();
	}
}
