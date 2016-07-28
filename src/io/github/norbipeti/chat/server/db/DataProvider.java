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

	public DataProvider() {
		emf = Persistence.createEntityManagerFactory("ChatServerPU");
	}

	public void saveUser(User user) {
		save(user);
	}

	public void saveConversation(Conversation convo) {
		save(convo);
	}

	private void save(Object object) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Session s = em.unwrap(Session.class);
		s.saveOrUpdate(object);
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
		Hibernate.initialize(results);
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
		return get(User.class, id);
	}

	private <T> T get(Class<T> cl, Long id) {
		EntityManager em = emf.createEntityManager();
		T result = em.find(cl, id);
		em.close();
		return result;
	}

	@Deprecated
	public void SetValues(Runnable action) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		action.run();
		em.flush();
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public void close() {
		if (emf != null)
			emf.close();
	}

	public boolean isEntityManaged(Object entity) {
		EntityManager em = emf.createEntityManager();
		boolean ret = em.contains(entity);
		em.close();
		return ret;
	}
}
