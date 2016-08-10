package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.sun.net.httpserver.*;

import io.github.norbipeti.chat.server.Main;
import io.github.norbipeti.chat.server.io.IOHelper;

/**
 * Add to {@link Main}.Pages
 */
public abstract class Page implements HttpHandler {
	public abstract String GetName();

	public final String GetHTMLPath() {
		if (GetName().length() == 0)
			return "pages/index.html";
		return new StringBuilder("pages/").append(GetName()).append(".html").toString();
	}

	@Override
	public void handle(HttpExchange exchange) {
		try {
			if (!getDo404() || exchange.getRequestURI().getPath().equals("/" + GetName()))
				handlePage(exchange);
			else {
				IOHelper.SendPage(404, NotFoundPage.Instance, exchange);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream str = new PrintStream(baos);
				str.print("<h1>500 Internal Server Error</h1><pre>");
				e.printStackTrace(str);
				str.print("</pre>");
				IOHelper.SendResponse(500, baos.toString(StandardCharsets.ISO_8859_1), exchange);
			} catch (Exception e1) {
				e1.printStackTrace(); // TODO: Message listener JS
			}
		}
	}

	public abstract void handlePage(HttpExchange exchange) throws IOException;

	public boolean getDo404() {
		return true;
	}
}
