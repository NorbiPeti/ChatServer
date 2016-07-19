package io.github.norbipeti.chat.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
		File file = new File(page.GetHTMLPath());
		if (!file.exists()) {
			SendResponse(501,
					"<h1>501 Not Implemented</h1><p>The page \"" + page.GetName() + "\" cannot be found on disk.</h1>",
					exchange);
			return false;
		}
		FileInputStream inputStream = new FileInputStream(file);
		String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		SendResponse(code, content, exchange);
		return true;
	}
}
