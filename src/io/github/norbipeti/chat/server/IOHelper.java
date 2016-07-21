package io.github.norbipeti.chat.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import com.sun.net.httpserver.HttpExchange;

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

	public static boolean SendModifiedPage(int code, Page page, String replace, String with, HttpExchange exchange)
			throws IOException {
		String content = GetPage(page, exchange);
		if (content == null)
			return false;
		SendResponse(200, content.replace(replace, with), exchange);
		return true;
	}
}
