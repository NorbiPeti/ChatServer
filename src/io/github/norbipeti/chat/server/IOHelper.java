package io.github.norbipeti.chat.server;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import com.sun.net.httpserver.HttpExchange;

public class IOHelper {
	public static void SendResponse(int code, String content, HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(code, content.length());
		IOUtils.write(content, exchange.getResponseBody());
		exchange.getResponseBody().close();
	}
}
