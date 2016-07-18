package io.github.norbipeti.chat.server;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public class IndexPage extends Page {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		IOHelper.SendResponse(200, "<h1>Index</h1>", exchange);
	}

}
