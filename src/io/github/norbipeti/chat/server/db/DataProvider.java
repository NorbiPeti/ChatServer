package io.github.norbipeti.chat.server.db;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import io.github.norbipeti.chat.server.db.domain.*;

public class DataProvider implements AutoCloseable {
	private EntityManagerFactory emf;
	private EntityManager em;

	public DataProvider() {
		emf = Persistence.createEntityManagerFactory("ChatServerPU");
		em = emf.createEntityManager();
		em.getTransaction().begin();
	}

	public <T extends ChatDatabaseEntity> T save(T object) {
		T obj = em.merge(object);
		return obj;
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
		TypedQuery<T> query = em.createQuery("SELECT x FROM " + cl.getSimpleName() + " x", cl);
		List<T> results = query.getResultList();
		Hibernate.initialize(results);
		return results;
	}

	public void removeUser(User user) {
		User managedUser = em.find(User.class, user.getId());
		em.remove(managedUser);
	}

	public User getUser(Long id) {
		return get(User.class, id);
	}

	private <T> T get(Class<T> cl, Long id) {
		T result = em.find(cl, id);
		return result;
	}

	@Deprecated
	public void SetValues(Runnable action) {
		action.run();
		em.flush();
	}

	@Override
	public void close() {
		if (em != null) {
			em.flush();
			em.close();
		}
		if (emf != null)
			emf.close();
	}

	public boolean isEntityManaged(Object entity) {
		boolean ret = em.contains(entity);
		return ret;
	}
}
