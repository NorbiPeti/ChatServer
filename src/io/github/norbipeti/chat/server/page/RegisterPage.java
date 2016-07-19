package io.github.norbipeti.chat.server.page;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;

public class RegisterPage extends Page {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		//exchange.getRequestURI().getPath()
		IOHelper.SendResponse(200, "<h1>Register</h1>", exchange);
	}

}
