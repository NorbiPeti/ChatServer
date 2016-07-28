package io.github.norbipeti.chat.server.page;

import java.io.IOException;

import org.jsoup.nodes.Element;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.ssl.internal.ssl.Provider;

import io.github.norbipeti.chat.server.IOHelper;
import io.github.norbipeti.chat.server.db.DataProvider;
import io.github.norbipeti.chat.server.db.domain.Conversation;
import io.github.norbipeti.chat.server.db.domain.Message;
import io.github.norbipeti.chat.server.db.domain.User;

public class IndexPage extends Page {

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		// final User user = IOHelper.GetLoggedInUser(exchange); - TODO
		final User user = new User();
		user.setEmail("test@test.com");
		user.setName("Norbi");
		user.setId(3L);
		if (user == null)
			IOHelper.SendModifiedPage(200, this, (doc) -> {
				doc.getElementById("userbox").remove();
				doc.getElementById("usercontent").remove();
				return doc;
			}, exchange);
		else
			IOHelper.SendModifiedPage(200, this, (doc) -> {
				doc.getElementById("loginbox").remove();
				doc.getElementById("registerbox").remove();
				Element userbox = doc.getElementById("userbox");
				userbox.html(userbox.html().replace("<username />", user.getName()));
				Element channelmessages = doc.getElementById("channelmessages");
				try (DataProvider provider = new DataProvider()) {
					Conversation convo = provider.getConversations().get(0); //TODO
					for (Message message : convo.getMesssages()) {
						Element msgelement = channelmessages.appendElement("div");
						Element header = msgelement.appendElement("p");
						header.text(message.getSender().getName() + " - " + message.getTime());
						Element body = msgelement.appendElement("p");
						body.text(message.getMessage());
					}
				}
				return doc;
			}, exchange);
	} // TODO:
		// Validation
		// at
		// registration

	@Override
	public String GetName() {
		return "";
	}

}
