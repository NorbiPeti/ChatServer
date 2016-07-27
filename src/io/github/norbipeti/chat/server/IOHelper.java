package io.github.norbipeti.chat.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.db.DataProvider;
import io.github.norbipeti.chat.server.db.domain.User;
import io.github.norbipeti.chat.server.page.Page;

public class IOHelper {
	public static void SendResponse(int code, String content, HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(code, content.length());
		IOUtils.write(content, exchange.getResponseBody(), StandardCharsets.UTF_8);
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
		FileInputStream inputStream = new FileInputStream(file);
		String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		return content;
	}

	@Deprecated
	public static HashMap<String, String> GetPOST(HttpExchange exchange) throws IOException {
		if (exchange.getRequestBody().available() == 0)
			return new HashMap<>();
		try {
			String[] content = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.ISO_8859_1).split("\\&");
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

	public static JSONObject GetPOSTJSON(HttpExchange exchange) throws IOException {
		if (exchange.getRequestBody().available() == 0)
			return null;
		try {
			String content = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.ISO_8859_1);
			JSONObject obj = new JSONObject(content);
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

	public static void LoginUser(HttpExchange exchange, User user, DataProvider provider) {
		provider.SetValues(() -> user.setSessionid(UUID.randomUUID()));
		System.out.println("Logging in user: " + user);
		ZonedDateTime expiretime = ZonedDateTime.now(ZoneId.of("GMT")).plus(Period.of(2, 0, 0));
		exchange.getResponseHeaders().add("Set-Cookie",
				"user_id=" + user.getId() + "; expires=" + expiretime.format(DateTimeFormatter.RFC_1123_DATE_TIME));
		exchange.getResponseHeaders().add("Set-Cookie",
				"session_id=" + user.getSessionid() + "; expires=" + expiretime);
	}

	public static void LogoutUser(HttpExchange exchange, User user) {
		user.setSessionid(new UUID(0, 0));
		SendLogoutHeaders(exchange);
	}

	private static void SendLogoutHeaders(HttpExchange exchange) {
		String expiretime = "Sat, 19 Mar 2016 23:33:00 GMT";
		exchange.getResponseHeaders().add("Set-Cookie", "user_id=del; expires=" + expiretime);
		exchange.getResponseHeaders().add("Set-Cookie", "session_id=del; expires=" + expiretime);
	}

	public static void Redirect(String url, HttpExchange exchange) throws IOException {
		exchange.getResponseHeaders().add("Location", url);
		IOHelper.SendResponse(303, "<a href=\"" + url + "\">If you can see this, click here to continue</a>", exchange);
	}

	public static HashMap<String, String> GetCookies(HttpExchange exchange) {
		if (!exchange.getRequestHeaders().containsKey("Cookie"))
			return new HashMap<>();
		HashMap<String, String> map = new HashMap<>();
		for (String cheader : exchange.getRequestHeaders().get("Cookie")) {
			String[] spl = cheader.split("\\;\\s*");
			for (String s : spl) {
				String[] kv = s.split("\\=");
				if (kv.length < 2)
					continue;
				map.put(kv[0], kv[1]);
			}
		}
		return map;
	}

	/**
	 * Get logged in user. It may also send logout headers if the cookies are
	 * invalid.
	 * 
	 * @param exchange
	 * @return The logged in user or null if not logged in.
	 */
	public static User GetLoggedInUser(HttpExchange exchange) {
		HashMap<String, String> cookies = GetCookies(exchange);
		System.out.println("Cookies: " + cookies);
		if (!cookies.containsKey("user_id") || !cookies.containsKey("session_id"))
			return null;
		System.out.println("Cookies found");
		try (DataProvider provider = new DataProvider()) {
			User user = provider.getUser(Long.parseLong(cookies.get("user_id")));
			System.out.println("User: " + user);
			System.out.println("session_id: " + cookies.get("session_id"));
			if (user != null)
				System.out.println("Equals: " + UUID.fromString(cookies.get("session_id")).equals(user.getSessionid()));
			if (user != null && cookies.get("session_id") != null
					&& UUID.fromString(cookies.get("session_id")).equals(user.getSessionid()))
				return user;
			else
				SendLogoutHeaders(exchange);
		}
		return null;
	}

	public static void SendResponse(int code, Function<Document, Document> action, HttpExchange exchange)
			throws IOException {
		Document doc = new Document("");
		doc = action.apply(doc);
		SendResponse(200, doc.html(), exchange);
	}
}
