package io.github.norbipeti.chat.server.db.domain;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import io.github.norbipeti.chat.server.data.DataManager;

@SuppressWarnings("serial")
public abstract class ManagedData implements Serializable {
	public abstract long getId();

	protected abstract void setId(long id);

	protected abstract void init();

	protected ManagedData() {
	}

	@Override
	public boolean equals(Object object) {
		if (!this.getClass().equals(object.getClass()))
			return false;
		ManagedData data = (ManagedData) object;
		return this.getId() == data.getId();
	}

	public static <T extends ManagedData> T create(Class<T> cl) {
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
		obj.init();
		if (SavedData.class.isAssignableFrom(cl))
			DataManager.save((SavedData) obj);
		return obj;
	}
}
