package io.github.norbipeti.chat.server.data;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.github.norbipeti.chat.server.db.domain.ChatDatabaseEntity;

//@SuppressWarnings("rawtypes")
public class LoaderCollectionSerializer<T extends ChatDatabaseEntity> extends TypeAdapter<LoaderCollection<T>> {

	@Override
	public void write(JsonWriter out, LoaderCollection<T> value) throws IOException {
		out.beginObject();
		out.name("items");
		new Gson().toJson(value.idlist, new TypeToken<List<Long>>() {
		}.getType(), out);
		out.name("class").value(value.cl.getName());
		out.endObject();
	}

	// @SuppressWarnings("unchecked")
	@SuppressWarnings("unchecked")
	@Override
	public LoaderCollection<T> read(JsonReader in) throws IOException {
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
			cl = (Class<T>) Class.forName(in.nextString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		LoaderCollection<T> col = new LoaderCollection<T>(cl); // TODO
		col.idlist.addAll(list);
		in.endObject();
		return col;
	}

}