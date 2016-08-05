package io.github.norbipeti.chat.server.data;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import io.github.norbipeti.chat.server.db.domain.SavedData;

// @SuppressWarnings("rawtypes")
public class LoaderCollectionSerializer<T extends SavedData> extends TypeAdapter<LoaderCollection<T>> {

	@Override
	public void write(JsonWriter out, LoaderCollection<T> value) throws IOException {
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

}
