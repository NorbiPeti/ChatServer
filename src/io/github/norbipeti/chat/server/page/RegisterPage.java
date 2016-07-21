package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import java.util.HashMap;
import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;

public class RegisterPage extends Page {
	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		HashMap<String, String> post = IOHelper.GetPOST(exchange);
		if (post.size() > 0) {
			String errormsg = CheckValues(post, "name", "email", "pass", "pass2");
			if (errormsg.length() == 0) {
				// Process register
				String successmsg = "";
				IOHelper.SendModifiedPage(200, this, "<successmsg />", successmsg, exchange);
				return; // TODO: Only show tag when needed
			} else
				IOHelper.SendModifiedPage(200, this, "<errormsg />", errormsg, exchange);
			return;
		}
		IOHelper.SendPage(200, this, exchange);
	}

	private String CheckValues(HashMap<String, String> post, String... values) {
		String errormsg = "";
		for (String value : values)
			if (!CheckValue(post.get(value)))
				errormsg += "<p>" + value + " can't be empty</p>";
		return errormsg;
	}

	private boolean CheckValue(String val) {
		return val != null && val.length() > 0;
	}

	@Override
	public String GetName() {
		return "register";
	}

}
