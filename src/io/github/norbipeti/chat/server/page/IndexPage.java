package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.jsoup.nodes.Element;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.data.DataManager;
import io.github.norbipeti.chat.server.data.LoaderCollection;
import io.github.norbipeti.chat.server.db.domain.Conversation;
import io.github.norbipeti.chat.server.db.domain.Message;
import io.github.norbipeti.chat.server.db.domain.MessageChunk;
import io.github.norbipeti.chat.server.db.domain.ManagedData;
import io.github.norbipeti.chat.server.db.domain.User;
import io.github.norbipeti.chat.server.io.IOHelper;

public class IndexPage extends Page {

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		final User user = IOHelper.GetLoggedInUser(exchange);
		LogManager.getLogger().debug("Logged in user: " + user);
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
				LogManager.getLogger().log(Level.DEBUG,
						"Conversations: " + DataManager.getAll(Conversation.class).size());
				LogManager.getLogger().log(Level.DEBUG, "User conversations: " + user.getConversations().size());
				LogManager.getLogger().log(Level.DEBUG, "Username: " + user.getName());
				if (user.getConversations().size() == 0) {
					LoaderCollection<Conversation> convs = DataManager.getAll(Conversation.class);
					if (convs.size() == 0) {
						Conversation c = ManagedData.create(Conversation.class);
						convs.add(c); // TODO: Handle no conversation open
					}
					user.getConversations().add(convs.get(0));
					convs.get(0).getUsers().add(user);
				}
				Conversation conv = user.getConversations().get(0);
				Element cide = channelmessages.appendElement("p");
				cide.attr("style", "display: none");
				cide.attr("id", "convidp");
				cide.text(Long.toString(conv.getId()));
				LogManager.getLogger().log(Level.DEBUG, "Messagechunks: " + conv.getMesssageChunks().size());
				if (conv.getMesssageChunks().size() > 0) {
					MessageChunk chunk = conv.getMesssageChunks().get(conv.getMesssageChunks().size() - 1);
					for (Message message : chunk.getMessages()) {
						message.getAsHTML(channelmessages);
					}
				}
				return doc;
			}, exchange);
	} // TODO: Validation at registration (no special chars, etc.)

	@Override
	public String GetName() {
		return "";
	}

}
