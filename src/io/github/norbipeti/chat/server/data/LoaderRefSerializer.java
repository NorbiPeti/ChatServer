package io.github.norbipeti.chat.server.data;

import java.io.IOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import io.github.norbipeti.chat.server.db.domain.SavedData;

// @SuppressWarnings("rawtypes")
public class LoaderRefSerializer<T extends SavedData> extends TypeAdapter<LoaderRef<T>> {

	@Override
	public void write(JsonWriter out, LoaderRef<T> value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}
		out.beginObject();
		out.name("id");
		out.value(value.id);
		out.name("class").value(value.cl.getSimpleName());
		out.endObject();
	}

	// @SuppressWarnings("unchecked")
	@SuppressWarnings("unchecked")
	@Override
	public LoaderRef<T> read(JsonReader in) throws IOException {
		if (in.peek().equals(JsonToken.NULL)) {
			in.nextNull();
			return null;
		}
		in.beginObject();
		in.nextName();
		long id = in.nextLong();
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
		LoaderRef<T> col = new LoaderRef<T>(cl, id);
		in.endObject();
		return col;
	}

}
