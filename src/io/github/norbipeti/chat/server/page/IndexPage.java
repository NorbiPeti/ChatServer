package io.github.norbipeti.chat.server.page;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;

public class IndexPage extends Page {

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		if (exchange.getRequestHeaders().containsKey("Cookie"))
			System.out.println(exchange.getRequestHeaders().get("Cookie"));
		IOHelper.SendPage(200, this, exchange);
	}

	@Override
	public String GetName() {
		return "";
	}

}
