package io.github.norbipeti.chat.server.data;

import java.io.IOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import io.github.norbipeti.chat.server.db.domain.SavedData;

// @SuppressWarnings("rawtypes")
public class LoaderRefSerializer extends TypeAdapter<LoaderRef<?>> {

	@Override
	public void write(JsonWriter out, LoaderRef<?> value) throws IOException {
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
	public LoaderRef<?> read(JsonReader in) throws IOException {
		if (in.peek().equals(JsonToken.NULL)) {
			in.nextNull();
			return null;
		}
		in.beginObject();
		in.nextName();
		long id = in.nextLong();
		if (!in.nextName().equals("class")) {
			new Exception("Error: Next isn't \"class\"").printStackTrace(); // TODO: Same as at LoaderCollectionSerializer
			return null;
		}
		Class<? extends SavedData> cl;
		try {
			cl = (Class<? extends SavedData>) Class.forName(DataManager.getPackageName() + "." + in.nextString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		LoaderRef<? extends SavedData> ref;
		try {
			ref = LoaderRef.class.getDeclaredConstructor(Class.class).newInstance(cl);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		ref.id = id;
		in.endObject();
		return ref;
	}

}
