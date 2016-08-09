package io.github.norbipeti.chat.server.io;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.github.norbipeti.chat.server.data.Loader;
import io.github.norbipeti.chat.server.db.domain.SavedData;

public final class DataType implements ParameterizedType {
	private Class<? extends SavedData> datacl;
	private Class<? extends Loader> loadercl;

	public DataType(Class<? extends Loader> loadercl, Class<? extends SavedData> datacl) {
		this.datacl = datacl;
		this.loadercl = loadercl;
	}

	@Override
	public Type[] getActualTypeArguments() {
		return new Type[] { datacl };
	}

	@Override
	public Type getRawType() {
		return loadercl;
	}

	@Override
	public Type getOwnerType() {
		return null;
	}
}
