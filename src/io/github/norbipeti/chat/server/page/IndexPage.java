package io.github.norbipeti.chat.server.page;

import java.io.IOException;

import org.jsoup.nodes.Element;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;
import io.github.norbipeti.chat.server.db.domain.User;

public class IndexPage extends Page {

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		User user = IOHelper.GetLoggedInUser(exchange);
		if (user == null)
			IOHelper.SendModifiedPage(200, this, (doc) -> {
				doc.getElementById("userbox").remove();
				return doc;
			}, exchange);
		else
			IOHelper.SendModifiedPage(200, this, (doc) -> {
				doc.getElementById("loginbox").remove();
				Element userbox = doc.getElementById("userbox");
				userbox.html(userbox.html().replace("<username />", user.getName()));
				return doc;
			}, exchange);
	} // TODO: Validation at registration

	@Override
	public String GetName() {
		return "";
	}

}
