package io.github.norbipeti.chat.server.page;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;

public class NotFoundPage extends Page {

	@Override
	public String GetName() {
		return "notfound";
	}

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		IOHelper.SendPage(404, this, exchange);
	}

	public NotFoundPage() {
		if (Instance != null)
			throw new UnsupportedOperationException("There can only be one instance of a page.");
		Instance = this;
	}

	public static NotFoundPage Instance;
}
