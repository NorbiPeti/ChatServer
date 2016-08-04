package io.github.norbipeti.chat.server.data;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.github.norbipeti.chat.server.db.domain.SavedData;

//@SuppressWarnings("rawtypes")
public class LoaderRefSerializer extends TypeAdapter<LoaderRef<? extends SavedData>> {

	@Override
	public void write(JsonWriter out, LoaderRef<? extends SavedData> value) throws IOException {
		out.beginObject();
		out.name("items");
		new Gson().toJson(value.id, new TypeToken<List<Long>>() {
		}.getType(), out);
		out.name("class").value(value.cl.getName());
		out.endObject();
	}

	// @SuppressWarnings("unchecked")
	@SuppressWarnings("unchecked")
	@Override
	public LoaderRef<? extends SavedData> read(JsonReader in) throws IOException {
		in.beginObject();
		in.nextName();
		Long id = new Gson().fromJson(in, new TypeToken<Long>() {
		}.getType());
		if (!in.nextName().equals("class")) {
			new Exception("Error: Next isn't \"class\"").printStackTrace();
			return null;
		}
		Class<? extends SavedData> cl;
		try {
			cl = (Class<? extends SavedData>) Class.forName(in.nextString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		LoaderRef<? extends SavedData> col = new LoaderRef<SavedData>(
				(Class<SavedData>) cl, id); // TODO
		in.endObject();
		return col;
	}

}
