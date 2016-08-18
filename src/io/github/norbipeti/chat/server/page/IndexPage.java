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
				Element cide = channelmessages.appendElement("p");
				long convid = -1;
				if (user.getCurrentConversation() != null) {
					Conversation conv = user.getCurrentConversation().get();
					convid = conv.getId();
					if (conv.getMesssageChunks().size() > 0) {
						MessageChunk chunk = conv.getMesssageChunks().get(conv.getMesssageChunks().size() - 1);
						for (Message message : chunk.getMessages()) {
							message.getAsHTML(channelmessages);
						}
					}
					for (int i = 0; i < conv.getUsers().size(); i++)
						doc.getElementById("usersinconv").appendElement("div").attr("id", "convuser" + i)
								.addClass("convuser").appendElement("p").text(conv.getUsers().get(i).getName());
					doc.getElementById("sidebarr").appendElement("div").attr("id", "addconvuser").appendElement("a")
							.text("Add user to conversation").attr("href", "javascript:showAddUserToConv()");
					// TODO: Update users and conversations automatically on conv change
				}
				cide.attr("style", "display: none");
				cide.attr("id", "convidp");
				cide.text(Long.toString(convid));
				Element conversations = doc.getElementById("conversations");
				for (Conversation conv : user.getConversations())
					conv.getAsHtml(conversations);
				conversations.appendElement("div").attr("id", "addconv").appendElement("a").attr("href", "#")
						.attr("onclick", "return addConversation();").text("Add conversation");
				return doc;
			}, exchange);

	} // TODO: Validation at registration (no special chars, etc.)

	@Override
	public String GetName() {
		return "";
	}

}
