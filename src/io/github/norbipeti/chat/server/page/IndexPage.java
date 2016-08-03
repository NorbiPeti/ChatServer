package io.github.norbipeti.chat.server.page;

import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.jsoup.nodes.Element;

import com.sun.net.httpserver.HttpExchange;
import io.github.norbipeti.chat.server.IOHelper;
import io.github.norbipeti.chat.server.data.DataManager;
import io.github.norbipeti.chat.server.db.domain.Conversation;
import io.github.norbipeti.chat.server.db.domain.Message;
import io.github.norbipeti.chat.server.db.domain.User;

public class IndexPage extends Page {

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		final User user = IOHelper.GetLoggedInUser(exchange);
		/*
		 * final User user = new User(); user.setEmail("test@test.com");
		 * user.setName("Norbi"); user.setId(3L);
		 */
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
				LogManager.getLogger().log(Level.INFO, "Conversations: " + DataManager.load(Conversation.class).size());
				LogManager.getLogger().log(Level.INFO, "User conversations: " + user.getConversations().size());
				Conversation convo = user.getConversations().get(0);
				LogManager.getLogger().log(Level.INFO, "Messages: " + convo.getMesssages().size());
				for (Message message : convo.getMesssages()) {
					Element msgelement = channelmessages.appendElement("div");
					Element header = msgelement.appendElement("p");
					header.text(message.getSender().getName() + " - " + message.getTime());
					Element body = msgelement.appendElement("p");
					body.text(message.getMessage());
				}
				return doc;
			}, exchange);
	} // TODO: Validation at registration (no special chars, etc.)

	@Override
	public String GetName() {
		return "";
	}

}
