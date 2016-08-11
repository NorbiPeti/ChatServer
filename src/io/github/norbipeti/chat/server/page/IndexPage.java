package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.jsoup.nodes.Element;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.data.DataManager;
import io.github.norbipeti.chat.server.data.LoaderCollection;
import io.github.norbipeti.chat.server.db.domain.Conversation;
import io.github.norbipeti.chat.server.db.domain.Message;
import io.github.norbipeti.chat.server.db.domain.MessageChunk;
import io.github.norbipeti.chat.server.db.domain.SavedData;
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
				LogManager.getLogger().log(Level.INFO,
						"Conversations: " + DataManager.getAll(Conversation.class).size());
				LogManager.getLogger().log(Level.INFO, "User conversations: " + user.getConversations().size());
				LogManager.getLogger().log(Level.INFO, "Username: " + user.getName());
				if (user.getConversations().size() == 0) {
					LoaderCollection<Conversation> convs = DataManager.getAll(Conversation.class);
					if (convs.size() == 0) {
						Conversation c = SavedData.create(Conversation.class);
						convs.add(c); // TODO: Handle no conversation open
					}
					user.getConversations().add(convs.get(0));
				}
				Conversation conv = user.getConversations().get(0);
				Element cide = channelmessages.appendElement("p");
				cide.attr("style", "display: none");
				cide.attr("id", "convidp");
				cide.text(Long.toString(conv.getId()));
				LogManager.getLogger().log(Level.INFO, "Messages: " + conv.getMesssageChunks().size());
				for (MessageChunk chunk : conv.getMesssageChunks()) { // TODO: Reverse
					for (Message message : chunk.getMessages()) {
						Element msgelement = channelmessages.appendElement("div");
						Element header = msgelement.appendElement("p");
						header.text(message.getSender().get().getName() + " - ");
						SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
						isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
						header.appendElement("span").addClass("converttime").text(isoFormat.format(message.getTime()));
						Element body = msgelement.appendElement("p");
						body.text(message.getMessage()); // TODO: Use JavaScript to convert time
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
