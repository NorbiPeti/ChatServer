package io.github.norbipeti.chat.server.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import io.github.norbipeti.chat.server.db.domain.ManagedData;

/**
 * <p>
 * This list wil only load it's items when directly accessed
 * </p>
 * <p>
 * And it will only save IDs of it's items
 * </p>
 * 
 * @author Norbi
 *
 * @param <T>
 */
public class LoaderCollection<T extends ManagedData> extends Loader implements List<T> {
	private static final long serialVersionUID = 5426152406394894301L;
	List<Long> idlist;
	Class<T> cl;

	/**
	 * Only used for serialization
	 */
	@Deprecated
	public LoaderCollection() {
		idlist = new ArrayList<>();
	}

	public LoaderCollection(Class<T> cl) {
		this.cl = cl;
		idlist = new ArrayList<>();
	}

	public LoaderCollection(LoaderCollection<T> parentofsub, int fromIndex, int toIndex) {
		this.cl = parentofsub.cl;
		idlist = parentofsub.idlist.subList(fromIndex, toIndex);
	}

	public LoaderCollection(Class<T> cl, int capacity) {
		this.cl = cl;
		idlist = new ArrayList<>(capacity);
	}

	@Override
	public Iterator<T> iterator() {
		return new LoaderIterator<T>(idlist.iterator(), cl);
	}

	@Override
	public boolean add(T e) {
		DataManager.save(e);
		return idlist.add(e.getId());
	}

	@Override
	public void add(int index, T element) {
		DataManager.save(element);
		idlist.add(index, element.getId());
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return idlist.addAll(c.stream().map((data) -> {
			ManagedData cde = ((ManagedData) data);
			DataManager.save(cde);
			return cde.getId();
		}).collect(Collectors.toList()));
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return idlist.addAll(index, c.stream().map((data) -> {
			ManagedData cde = ((ManagedData) data);
			DataManager.save(cde);
			return cde.getId();
		}).collect(Collectors.toList()));
	}

	@Override
	public void clear() {
		idlist.clear();
	}

	@Override
	public boolean contains(Object o) {
		return idlist.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return idlist.containsAll(c);
	}

	@Override
	public T get(int index) {
		return DataManager.load(cl, idlist.get(index));
	}

	@Override
	public int indexOf(Object o) {
		return idlist.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return idlist.isEmpty();
	}

	@Override
	public int lastIndexOf(Object o) {
		return idlist.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return new LoaderListIterator<T>(idlist.listIterator(), cl);
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new LoaderListIterator<T>(idlist.listIterator(index), cl);
	}

	/**
	 * Remove an object from this collection
	 * 
	 * @param o
	 *            Either the object of type T or the ID
	 */
	@Override
	public boolean remove(Object o) {
		if (ManagedData.class.isAssignableFrom(o.getClass())) {
			DataManager.remove((ManagedData) o);
			return idlist.remove(((ManagedData) o).getId());
		}
		if (Long.class.isAssignableFrom(o.getClass()))
			DataManager.remove(cl, (Long) o);
		return idlist.remove(o);
	}

	@Override
	public T remove(int index) {
		return DataManager.load(cl, idlist.remove(index));
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean success = false;
		for (Object item : c) {
			if (ManagedData.class.isAssignableFrom(item.getClass())) {
				if (idlist.remove(((ManagedData) item).getId())) {
					success = true;
					break;
				}
			} else {
				if (idlist.remove(item)) {
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
			if (ManagedData.class.isAssignableFrom(item.getClass())) {
				list.add(((ManagedData) item).getId());
			} else if (Long.class.isAssignableFrom(item.getClass())) {
				list.add((Long) item);
			}
		}
		return idlist.retainAll(list);
	}

	@Override
	public T set(int index, T element) {
		return DataManager.load(cl, idlist.set(index, element.getId()));
	}

	@Override
	public int size() {
		return idlist.size();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return new LoaderCollection<T>(this, fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return idlist.stream().map((data) -> {
			return DataManager.load(cl, data);
		}).collect(Collectors.toList()).toArray();
	}

	@Override
	public <U> U[] toArray(U[] a) {
		return idlist.stream().map((data) -> {
			return DataManager.load(cl, data);
		}).collect(Collectors.toList()).toArray(a);
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean loaditems) {
		StringBuilder sb = new StringBuilder("[");
		for (Long item : idlist) {
			if (loaditems)
				sb.append(DataManager.load(cl, item));
			else
				sb.append(item);
		}
		sb.append("]");
		return sb.toString();
	}
}
