package io.github.norbipeti.chat.server.data;

import java.util.ListIterator;

import io.github.norbipeti.chat.server.db.domain.SavedData;

public final class LoaderListIterator<T extends SavedData> implements ListIterator<T> {
	private ListIterator<Long> listiterator;
	private T lastitem;
	private Class<T> cl;

	LoaderListIterator(ListIterator<Long> listiterator, Class<T> cl) {
		this.listiterator = listiterator;
		this.cl = cl;
	}

	@Override
	public boolean hasNext() {
		return listiterator.hasNext();
	}

	@Override
	public T next() {
		return lastitem = DataManager.load(cl, listiterator.next());
	}

	@Override
	public void remove() {
		if (lastitem == null)
			throw new IllegalStateException();
		DataManager.remove(lastitem);
		lastitem = null;
	}

	@Override
	public void add(T e) {
		listiterator.add(e.getId());
	}

	@Override
	public boolean hasPrevious() {
		return listiterator.hasPrevious();
	}

	@Override
	public int nextIndex() {
		return listiterator.nextIndex();
	}

	@Override
	public T previous() {
		return DataManager.load(cl, listiterator.previous());
	}

	@Override
	public int previousIndex() {
		return listiterator.previousIndex();
	}

	@Override
	public void set(T e) {
		listiterator.set(e.getId());
	}
}
