package io.github.norbipeti.chat.server.page;

import java.io.IOException;

import com.sun.net.httpserver.*;

import io.github.norbipeti.chat.server.IOHelper;
import io.github.norbipeti.chat.server.Main;

/**
 * Add to {@link Main}.Pages
 *
 */
public abstract class Page implements HttpHandler {
	public abstract String GetName();

	public final String GetHTMLPath() {
		if (GetName().length() == 0)
			return "pages/index.html";
		return new StringBuilder("pages/").append(GetName().length() == 0).append(".html").toString();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if (exchange.getRequestURI().getPath().equals("/" + GetName()))
			handlePage(exchange);
		else {
			if (!IOHelper.SendPage(404, NotFoundPage.Instance, exchange))
				;
		}
	}

	public abstract void handlePage(HttpExchange exchange) throws IOException;

	public boolean getDo404() {
		return true;
	}
}
