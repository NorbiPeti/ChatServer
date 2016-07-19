package io.github.norbipeti.chat.server.page;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;

public class IndexPage extends Page {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		IOHelper.SendResponse(200, "<h1>Index</h1>", exchange);
	}

}
