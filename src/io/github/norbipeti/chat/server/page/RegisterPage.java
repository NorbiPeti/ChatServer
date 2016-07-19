package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;

public class RegisterPage extends Page {
	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		/*for(String line : IOHelper.GetPOST(exchange))
			System.out.println(line);*/
		IOHelper.SendPage(200, this, exchange);
	}

	@Override
	public String GetName() {
		return "register";
	}

}
