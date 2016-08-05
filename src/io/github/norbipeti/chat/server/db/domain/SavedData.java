package io.github.norbipeti.chat.server.db.domain;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import io.github.norbipeti.chat.server.data.DataManager;

@SuppressWarnings("serial")
public abstract class SavedData implements Serializable {
	public abstract long getId();

	public abstract void setId(long id);

	protected SavedData() {
	}

	public static <T extends SavedData> T create(Class<T> cl) {
		T obj;
		try {
			Constructor<T> constructor = cl.getDeclaredConstructor(new Class<?>[0]);
			constructor.setAccessible(true);
			obj = constructor.newInstance();
			constructor.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		obj.setId(DataManager.getNextIDs().getOrDefault(obj.getClass(), 0L));
		DataManager.setNextID(obj.getClass(), obj.getId() + 1);
		DataManager.save(obj);
		return obj;
	}
}
