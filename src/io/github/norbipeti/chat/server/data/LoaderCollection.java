package io.github.norbipeti.chat.server.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import io.github.norbipeti.chat.server.db.domain.ChatDatabaseEntity;

public class LoaderCollection<T extends ChatDatabaseEntity> implements List<T>, Serializable {
	private static final long serialVersionUID = 5426152406394894301L;
	private List<Long> contacts;
	private Class<T> cl;
	private transient boolean forsave = false;

	/**
	 * Only used for serialization
	 */
	@Deprecated
	public LoaderCollection() {
	}

	public LoaderCollection(Class<T> cl) {
		this.cl = cl;
		contacts = new ArrayList<>();
	}

	public LoaderCollection(LoaderCollection<T> parentofsub, int fromIndex, int toIndex) {
		this.cl = parentofsub.cl;
		contacts = parentofsub.contacts.subList(fromIndex, toIndex);
	}

	public LoaderCollection(Class<T> cl, int capacity) {
		this.cl = cl;
		contacts = new ArrayList<>(capacity);
	}

	@Override
	public Iterator<T> iterator() {
		if (forsave)
			return contacts.iterator(); // TODO: Fix
		else
			return new LoaderIterator<T>(contacts.iterator(), cl);
	}

	@Override
	public boolean add(T e) {
		return contacts.add(e.getId());
	}

	@Override
	public void add(int index, T element) {
		contacts.add(index, element.getId());
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return contacts
				.addAll(c.stream().map((data) -> ((ChatDatabaseEntity) data).getId()).collect(Collectors.toList()));
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return contacts.addAll(index,
				c.stream().map((data) -> ((ChatDatabaseEntity) data).getId()).collect(Collectors.toList()));
	}

	@Override
	public void clear() {
		contacts.clear();
	}

	@Override
	public boolean contains(Object o) {
		return contacts.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return contacts.containsAll(c);
	}

	@Override
	public T get(int index) {
		return DataManager.load(cl, contacts.get(index));
	}

	@Override
	public int indexOf(Object o) {
		return contacts.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return contacts.isEmpty();
	}

	@Override
	public int lastIndexOf(Object o) {
		return contacts.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return new LoaderListIterator<T>(contacts.listIterator(), cl);
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new LoaderListIterator<T>(contacts.listIterator(index), cl);
	}

	/**
	 * Remove an object from this collection
	 * 
	 * @param o
	 *            Either the object of type T or the ID
	 */
	@Override
	public boolean remove(Object o) {
		if (ChatDatabaseEntity.class.isAssignableFrom(o.getClass()))
			return contacts.remove(((ChatDatabaseEntity) o).getId());
		return contacts.remove(o);
	}

	@Override
	public T remove(int index) {
		return DataManager.load(cl, contacts.remove(index));
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean success = false;
		for (Object item : c) {
			if (ChatDatabaseEntity.class.isAssignableFrom(item.getClass())) {
				if (contacts.remove(((ChatDatabaseEntity) item).getId())) {
					success = true;
					break;
				}
			} else {
				if (contacts.remove(item)) {
					success = true;
					break;
				}
			}
		}
		return success;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		List<Long> list = new ArrayList<Long>();
		for (Object item : c) {
			if (ChatDatabaseEntity.class.isAssignableFrom(item.getClass())) {
				list.add(((ChatDatabaseEntity) item).getId());
			} else if (Long.class.isAssignableFrom(item.getClass())) {
				list.add((Long) item);
			}
		}
		return contacts.retainAll(list);
	}

	@Override
	public T set(int index, T element) {
		return DataManager.load(cl, contacts.set(index, element.getId()));
	}

	@Override
	public int size() {
		return contacts.size();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return new LoaderCollection<T>(this, fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return contacts.stream().map((data) -> {
			return DataManager.load(cl, data);
		}).collect(Collectors.toList()).toArray();
	}

	@Override
	public <U> U[] toArray(U[] a) {
		return contacts.stream().map((data) -> {
			return DataManager.load(cl, data);
		}).collect(Collectors.toList()).toArray(a);
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean loaditems) {
		StringBuilder sb = new StringBuilder("[");
		for (Long item : contacts) {
			if (loaditems)
				sb.append(DataManager.load(cl, item));
			else
				sb.append(item);
		}
		sb.append("]");
		return sb.toString();
	}

	public boolean isForsave() {
		return forsave;
	}

	public void setForsave(boolean forsave) {
		this.forsave = forsave;
	}
}
