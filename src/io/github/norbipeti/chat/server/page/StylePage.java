package io.github.norbipeti.chat.server.page;

import java.io.File;
import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;

public class StylePage extends Page {

	@Override
	public boolean getDo404() {
		return false;
	}

	@Override
	public String GetName() {
		return "css/";
	}

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		if (exchange.getRequestURI().getPath().startsWith("/css/")) {
			File cssfile = new File("pages", exchange.getRequestURI().getPath());
			if (!cssfile.exists())
				IOHelper.SendResponse(404, "<h1>CSS file not found</h1>", exchange);
			else {
				exchange.getResponseHeaders().add("Content-Type", "text/css");
				IOHelper.SendResponse(200, IOHelper.ReadFile(cssfile), exchange);
			}
		} else
			System.out.println(exchange.getRequestURI().getPath());
	}
}
