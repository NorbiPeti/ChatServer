package io.github.norbipeti.chat.server.page;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.io.IOHelper;

public class ScriptsPage extends Page {

	@Override
	public boolean getDo404() {
		return false;
	}

	@Override
	public String GetName() {
		return "js/";
	}

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		if (exchange.getRequestURI().getPath().startsWith("/js/")) {
			File jsfile = new File("pages", exchange.getRequestURI().getPath());
			if (!jsfile.exists())
				IOHelper.SendResponse(404, "<h1>JavaScript file not found</h1>", exchange);
			else
				IOHelper.SendResponse(200, IOHelper.ReadFile(jsfile), exchange);
		} else
			LogManager.getLogger().log(Level.DEBUG, exchange.getRequestURI().getPath());
	}
}
