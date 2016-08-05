package io.github.norbipeti.chat.server.data;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import io.github.norbipeti.chat.server.db.domain.SavedData;

// @SuppressWarnings("rawtypes")
public class LoaderCollectionSerializer extends TypeAdapter<LoaderCollection<?>> {
	private static final Type returnType = getReturnType();

	@Override
	public void write(JsonWriter out, LoaderCollection<?> value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}
		out.beginObject(); // TODO: http://stackoverflow.com/a/17300227
		out.name("items");
		new Gson().toJson(value.idlist, new TypeToken<List<Long>>() {
		}.getType(), out);
		out.name("class").value(value.cl.getSimpleName());
		out.endObject();
	}

	@SuppressWarnings("unchecked")
	@Override
	public LoaderCollection<T> read(JsonReader in) throws IOException {
		if (in.peek().equals(JsonToken.NULL)) {
			in.nextNull();
			return null;
		}
		in.beginObject();
		in.nextName();
		List<Long> list = new Gson().fromJson(in, new TypeToken<List<Long>>() {
		}.getType());
		if (!in.nextName().equals("class")) {
			new Exception("Error: Next isn't \"class\"").printStackTrace();
			return null;
		}
		Class<T> cl;
		try {
			cl = (Class<T>) Class.forName(DataManager.getPackageName() + "." + in.nextString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		LoaderCollection<T> col = new LoaderCollection<T>(cl);
		col.idlist.addAll(list);
		in.endObject();
		return col;
	}

	private static Type getReturnType() {
		try {
			return LoaderCollection.class.getDeclaredMethod("get", Integer.class).getGenericReturnType();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
