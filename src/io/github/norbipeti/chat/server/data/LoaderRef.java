package io.github.norbipeti.chat.server.data;

import io.github.norbipeti.chat.server.db.domain.SavedData;

/**
 * <p>
 * This class will only store IDs and load the object when calling {@link #get()}
 * </p>
 * <p>
 * And will also only save IDs when serialized with {@link LoaderRefSerializer}
 * </p>
 * 
 * @author Norbi
 * @param <T> The type of the stored object
 */
public class LoaderRef<T extends SavedData> extends Loader {
	private static final long serialVersionUID = 8458570738734235320L;
	Class<T> cl;
	Long id;

	public LoaderRef(Class<T> cl, Long id) {
		this.id = id;
		this.cl = cl;
	}

	@SuppressWarnings("unchecked")
	public LoaderRef(T obj) {
		this.id = obj.getId();
		this.cl = (Class<T>) obj.getClass();
	}

	public T get() {
		return DataManager.load(cl, id, true);
	}
}
