package io.github.norbipeti.chat.server.data;

import java.util.Iterator;
import io.github.norbipeti.chat.server.db.domain.SavedData;

public final class LoaderIterator<T extends SavedData> implements Iterator<T> {
	private Iterator<Long> iterator;
	private T lastitem;
	private Class<T> cl;

	LoaderIterator(Iterator<Long> listiterator, Class<T> cl) {
		this.iterator = listiterator;
		this.cl = cl;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public T next() {
		return lastitem = DataManager.load(cl, iterator.next());
	}

	@Override
	public void remove() {
		if (lastitem == null)
			throw new IllegalStateException();
		DataManager.remove(lastitem);
		lastitem = null;
	}
}
