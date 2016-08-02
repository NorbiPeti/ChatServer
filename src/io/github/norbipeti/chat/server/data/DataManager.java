package io.github.norbipeti.chat.server.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import io.github.norbipeti.chat.server.db.domain.ChatDatabaseEntity;

public final class DataManager {
	private DataManager() {
	}

	public static <T extends ChatDatabaseEntity> void save(T object) throws IOException {
		Gson gson = new Gson();
		Files.write(gson.toJson(object), new File(object.getClass().getName() + "-" + object.getId()),
				StandardCharsets.UTF_8);
	}

	public static <T extends ChatDatabaseEntity> T load(Class<T> cl, long id) {
		return loadFromFile(new File(cl.getName() + "-" + id), cl);
	}

	public static <T extends ChatDatabaseEntity> LoaderCollection<T> load(Class<T> cl) {
		String[] filenames = new File(".").list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(cl.getName() + "-");
			}
		});
		LoaderCollection<T> rets = new LoaderCollection<T>(cl, filenames.length);
		for (int i = 0; i < filenames.length; i++) {
			rets.add(loadFromFile(new File(filenames[i]), cl));
		}
		return rets;
	}

	private static <T extends ChatDatabaseEntity> T loadFromFile(File file, Class<T> cl) {
		try {
			if (!file.exists())
				return cl.newInstance();
			BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8);
			String objstr = "";
			String line;
			while ((line = reader.readLine()) != null)
				objstr += line;
			Gson gson = new Gson();
			return gson.fromJson(objstr, cl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T extends ChatDatabaseEntity> boolean remove(T obj) {
		return new File(obj.getClass().getName() + "-" + obj.getId()).delete();
	}
}
