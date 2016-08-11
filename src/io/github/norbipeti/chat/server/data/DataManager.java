package io.github.norbipeti.chat.server.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.io.Files;
import io.github.norbipeti.chat.server.Main;
import io.github.norbipeti.chat.server.db.domain.SavedData;

public final class DataManager {
	private DataManager() {
	}

	private static final File datafolder = new File("data");

	public static <T extends SavedData> void save(T object) {
		try {
			File file = new File(datafolder, getFileName(object.getClass(), object.getId()));
			cache.put(file, object);
			Files.write(Main.gson.toJson(object), file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static <T extends SavedData> T load(Class<T> cl, long id) {
		return loadFromFile(new File(datafolder, getFileName(cl, id)), cl);
	}

	public static <T extends SavedData> LoaderCollection<T> getAll(Class<T> cl) {
		String[] filenames = datafolder.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(getFileName(cl, null));
			}
		});
		LoaderCollection<T> rets = new LoaderCollection<T>(cl, filenames.length);
		for (int i = 0; i < filenames.length; i++) {
			try {
				rets.idlist.add(Long.parseLong(filenames[i].split("\\-")[1].split("\\.")[0]));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return rets;
	}

	private static Map<File, Object> cache = new HashMap<>();
	// TODO: Remove objects from the cache over time
	// TODO: Save the object when it gets removed from cache and when the app stops
	// TODO: Handle unloading of used objects (prevent detached objects)

	@SuppressWarnings("unchecked")
	private static <T extends SavedData> T loadFromFile(File file, Class<T> cl) {
		try {
			if (!file.exists()) {
				T obj = SavedData.create(cl);
				return obj;
			}
			if (cache.containsKey(file))
				return (T) cache.get(file);
			BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8);
			String objstr = "";
			String line;
			while ((line = reader.readLine()) != null)
				objstr += line;
			T obj = Main.gson.fromJson(objstr, cl);
			cache.put(file, obj);
			return obj;
		} catch (

		Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T extends SavedData> boolean remove(T obj) {
		if (cache.containsValue(obj))
			cache.values().remove(obj);
		return new File(obj.getClass().getSimpleName() + "-" + obj.getId()).delete();
	}

	public static <T extends SavedData> boolean remove(Class<T> cl, Long id) {
		return new File(cl.getName() + "-" + id).delete();
	}

	public static void init() {
		packagename = SavedData.class.getPackage().getName();
		datafolder.mkdir();
		nextids = loadNextIDs();
	}

	private static String packagename;

	public static String getPackageName() {
		return packagename;
	}

	public static void save() {
		saveNextIDs(nextids);
		for (Entry<File, Object> item : cache.entrySet()) {
			DataManager.save((SavedData) item.getValue());
		}
	}

	private static HashMap<Class<? extends SavedData>, Long> nextids;

	public static Map<Class<? extends SavedData>, Long> getNextIDs() {
		return Collections.unmodifiableMap(nextids);
	}

	public static void setNextID(Class<? extends SavedData> cl, Long id) {
		nextids.put(cl, id);
	}

	@SuppressWarnings("unchecked")
	private static HashMap<Class<? extends SavedData>, Long> loadNextIDs() {
		try {
			File file = new File("data", "idlist.ini");
			if (!file.exists())
				return new HashMap<>();
			BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8);
			String line;
			HashMap<Class<? extends SavedData>, Long> ret = new HashMap<>();
			while ((line = reader.readLine()) != null) {
				String[] spl = line.split("\\=");
				ret.put((Class<? extends SavedData>) Class.forName(packagename + "." + spl[0]), Long.parseLong(spl[1]));
			}
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashMap<>();
	}

	private static void saveNextIDs(HashMap<Class<? extends SavedData>, Long> ids) {
		try {
			File file = new File("data", "idlist.ini");
			String contents = "";
			for (Entry<Class<? extends SavedData>, Long> item : ids.entrySet()) {
				contents += item.getKey().getSimpleName() + "=" + item.getValue() + "\n";
			}
			Files.write(contents, file, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static <T extends SavedData> String getFileName(Class<T> cl, Long id) {
		if (id != null)
			return cl.getSimpleName() + "-" + id + ".json";
		else
			return cl.getSimpleName() + "-";
	}
}
