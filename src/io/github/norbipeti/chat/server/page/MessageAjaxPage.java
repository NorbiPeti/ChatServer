package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import java.util.Date;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;
import io.github.norbipeti.chat.server.data.DataManager;
import io.github.norbipeti.chat.server.data.LoaderCollection;
import io.github.norbipeti.chat.server.db.domain.Conversation;
import io.github.norbipeti.chat.server.db.domain.Message;
import io.github.norbipeti.chat.server.db.domain.User;

public class MessageAjaxPage extends Page {

	@Override
	public String GetName() {
		return "message";
	}

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		User user = IOHelper.GetLoggedInUser(exchange);
		if (user == null) {
			IOHelper.SendResponse(403, "<p>Please log in to send messages</p>", exchange);
			return; // TODO: Fix sending messages
		}
		JsonObject obj = IOHelper.GetPOSTJSON(exchange);
		if (obj == null) {
			IOHelper.SendResponse(400,
					"<h1>400 Bad request</h1><p>Not a JSON string!</p><p>" + IOHelper.GetPOST(exchange) + "</p>",
					exchange);
			return;
		}
		if (!obj.has("message") || !obj.has("conversation")) {
			IOHelper.SendResponse(400,
					"<h1>400 Bad request</h1><p>Message or conversation not found in JSON response.</p><p>"
							+ IOHelper.GetPOST(exchange) + "</p>",
					exchange);
			return;
		}
		String message = obj.get("message").getAsString();
		int conversation = obj.get("conversation").getAsInt();
		if (message.trim().length() == 0) {
			IOHelper.SendResponse(400, "<h1>400 Bad request</h1><p>The message cannot be empty.</p>", exchange);
			return;
		}
		LoaderCollection<Conversation> convos = user.getConversations();
		Conversation conv = null;
		LogManager.getLogger().log(Level.DEBUG, "Len: " + convos.size());
		for (Conversation con : convos) {
			LogManager.getLogger().log(Level.DEBUG, con.getId());
			if (con.getId() == conversation) {
				conv = con;
				break;
			}
		}
		if (conv == null) {
			IOHelper.SendResponse(400, "<h1>400 Conversation not found</h1><p>The conversation with the id "
					+ conversation + " is not found.</p>", exchange);
			return;
		}
		Message msg = new Message();
		msg.setSender(user);
		msg.setMessage(message);
		msg.setTime(new Date());
		msg.setConversation(conv); // TODO: Store relations at one side or both
		DataManager.save(msg);
		conv.getMesssages().add(msg);
		DataManager.save(conv);
		LogManager.getLogger().log(Level.DEBUG, "Added conversation's message count: " + conv.getMesssages().size());

		IOHelper.SendResponse(200, "Success", exchange);
	}

}
