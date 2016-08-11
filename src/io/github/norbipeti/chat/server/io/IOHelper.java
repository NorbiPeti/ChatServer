package io.github.norbipeti.chat.server.io;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.data.DataManager;
import io.github.norbipeti.chat.server.db.domain.User;
import io.github.norbipeti.chat.server.page.Page;

public class IOHelper {
	public static void SendResponse(int code, String content, HttpExchange exchange) throws IOException {
		try (BufferedOutputStream out = new BufferedOutputStream(exchange.getResponseBody())) {
			try (ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
				exchange.sendResponseHeaders(code, bis.available());
				byte[] buffer = new byte[512];
				int count;
				while ((count = bis.read(buffer)) != -1) {
					out.write(buffer, 0, count);
				}
			}
		}
		exchange.getResponseBody().close();
	}

	public static boolean SendPage(int code, Page page, HttpExchange exchange) throws IOException {
		String content = GetPage(page, exchange);
		SendResponse(code, content, exchange);
		return true;
	}

	public static String GetPage(Page page, HttpExchange exchange) throws IOException {
		File file = new File(page.GetHTMLPath());
		if (!file.exists()) {
			SendResponse(501,
					"<h1>501 Not Implemented</h1><p>The page \"" + page.GetName() + "\" cannot be found on disk.</h1>",
					exchange);
			return null;
		}
		return ReadFile(file);
	}

	public static String ReadFile(File file) throws FileNotFoundException, IOException {
		String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8); // TODO: FIx UTF-8 file reading
		return content;
	}

	public static String GetPOST(HttpExchange exchange) {
		try {
			if (exchange.getRequestBody().available() == 0)
				return "";
			String content = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@Deprecated
	public static HashMap<String, String> GetPOSTKeyValues(HttpExchange exchange) {
		try {
			String[] content = GetPOST(exchange).split("\\&");
			HashMap<String, String> vars = new HashMap<>();
			for (String var : content) {
				String[] spl = var.split("\\=");
				if (spl.length == 1)
					vars.put(spl[0], "");
				else
					vars.put(spl[0], spl[1]);
			}
			return vars;
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}

	public static JsonObject GetPOSTJSON(HttpExchange exchange) {
		try {
			String content = GetPOST(exchange);
			JsonObject obj = new JsonParser().parse(content).getAsJsonObject();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean SendModifiedPage(int code, Page page, Function<Document, Document> modifyfunc,
			HttpExchange exchange) throws IOException {
		String content = GetPage(page, exchange);
		if (content == null)
			return false;
		Document doc = Jsoup.parse(content);
		doc = modifyfunc.apply(doc);
		SendResponse(200, doc.html(), exchange);
		return true;
	}

	/**
	 * Sends login headers and sets the session id on the user
	 * 
	 * @param exchange
	 * @param user
	 */
	public static void LoginUser(HttpExchange exchange, User user) {
		LogManager.getLogger().log(Level.DEBUG, "Logging in user: " + user);
		// provider.SetValues(() ->
		// user.setSessionid(UUID.randomUUID().toString()));
		user.setSessionid(UUID.randomUUID().toString());
		new Cookies(2).add(new Cookie("user_id", user.getId() + "")).add(new Cookie("session_id", user.getSessionid()))
				.SendHeaders(exchange);
		LogManager.getLogger().debug("Logged in user.");
	}

	public static void LogoutUser(HttpExchange exchange, User user) {
		user.setSessionid(new UUID(0, 0).toString());
		SendLogoutHeaders(exchange);
	}

	private static void SendLogoutHeaders(HttpExchange exchange) {
		String expiretime = "Sat, 19 Mar 2016 23:33:00 GMT";
		new Cookies(expiretime).add(new Cookie("user_id", "del")).add(new Cookie("session_id", "del"))
				.SendHeaders(exchange);
	}

	public static void Redirect(String url, HttpExchange exchange) throws IOException {
		exchange.getResponseHeaders().add("Location", url);
		IOHelper.SendResponse(303, "<a href=\"" + url + "\">If you can see this, click here to continue</a>", exchange);
	}

	public static Cookies GetCookies(HttpExchange exchange) {
		if (!exchange.getRequestHeaders().containsKey("Cookie"))
			return new Cookies();
		Map<String, String> map = new HashMap<>();
		for (String cheader : exchange.getRequestHeaders().get("Cookie")) {
			String[] spl = cheader.split("\\;\\s*");
			for (String s : spl) {
				String[] kv = s.split("\\=");
				if (kv.length < 2)
					continue;
				map.put(kv[0], kv[1]);
			}
		}
		if (!map.containsKey("expiretime"))
			return new Cookies();
		Cookies cookies = null;
		try {
			cookies = new Cookies(map.get("expiretime"));
			for (Entry<String, String> item : map.entrySet())
				if (!item.getKey().equalsIgnoreCase("expiretime"))
					cookies.put(item.getKey(), new Cookie(item.getKey(), item.getValue()));
		} catch (Exception e) {
			return new Cookies();
		}
		return cookies;
	}

	/**
	 * Get logged in user. It may also send logout headers if the cookies are invalid, or login headers to keep the user logged in.
	 * 
	 * @param exchange
	 * @return The logged in user or null if not logged in.
	 * @throws IOException
	 */
	public static User GetLoggedInUser(HttpExchange exchange) throws IOException {
		Cookies cookies = GetCookies(exchange);
		if (!cookies.containsKey("user_id") || !cookies.containsKey("session_id"))
			return null;
		User user = DataManager.load(User.class, Long.parseLong(cookies.get("user_id").getValue()));
		if (user != null && cookies.get("session_id") != null
				&& cookies.get("session_id").getValue().equals(user.getSessionid())) {
			if (cookies.getExpireTimeParsed().minusYears(1).isBefore(ZonedDateTime.now(ZoneId.of("GMT"))))
				LoginUser(exchange, user);
			return user;
		} else
			SendLogoutHeaders(exchange);
		return null;
	}

	public static void SendResponse(int code, Function<Document, Document> action, HttpExchange exchange)
			throws IOException {
		Document doc = new Document("");
		doc = action.apply(doc);
		SendResponse(200, doc.html(), exchange);
	}
}
