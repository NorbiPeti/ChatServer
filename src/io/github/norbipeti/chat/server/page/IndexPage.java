package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;
import io.github.norbipeti.chat.server.db.domain.User;

public class IndexPage extends Page {

	@Override
	public void handlePage(HttpExchange exchange) throws IOException { //TODO: Make a base HTML and insert all pages into that
		User user = IOHelper.GetLoggedInUser(exchange);
		if (user == null)
			IOHelper.SendModifiedPage(200, this,
					(doc) -> doc.getElementById("loginbox").attr("style", "display: block").ownerDocument(), exchange);
		else
			IOHelper.SendModifiedPage(200, this,
					(doc) -> doc.getElementById("userbox").attr("style", "display: block").ownerDocument(), exchange);
	}

	@Override
	public String GetName() {
		return "";
	}

}
