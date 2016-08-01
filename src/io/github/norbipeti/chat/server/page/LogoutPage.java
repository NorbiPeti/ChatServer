package io.github.norbipeti.chat.server.page;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;
import io.github.norbipeti.chat.server.db.domain.User;

public class LogoutPage extends Page {

	@Override
	public String GetName() {
		return "logout";
	}

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		User user = IOHelper.GetLoggedInUser(exchange);
		if (user != null)
			IOHelper.LogoutUser(exchange, user);
		IOHelper.Redirect("/", exchange);
	}

}
