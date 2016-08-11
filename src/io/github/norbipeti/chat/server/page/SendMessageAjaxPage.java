package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import java.util.Date;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.data.DataManager;
import io.github.norbipeti.chat.server.data.LoaderCollection;
import io.github.norbipeti.chat.server.db.domain.Conversation;
import io.github.norbipeti.chat.server.db.domain.Message;
import io.github.norbipeti.chat.server.db.domain.MessageChunk;
import io.github.norbipeti.chat.server.db.domain.ManagedData;
import io.github.norbipeti.chat.server.db.domain.User;
import io.github.norbipeti.chat.server.io.IOHelper;

public class SendMessageAjaxPage extends Page {

	@Override
	public String GetName() {
		return "sendmessage";
	}

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		User user = IOHelper.GetLoggedInUser(exchange);
		if (user == null) {
			IOHelper.SendResponse(403, "<p>Please log in to send messages</p>", exchange);
			return;
		}
		JsonObject obj = IOHelper.GetPOSTJSON(exchange);
		if (obj == null) {
			IOHelper.SendResponse(400, "JSONERROR: " + IOHelper.GetPOST(exchange), exchange);
			// IOHelper.SendResponse(400, "JSONERROR", exchange);
			return;
		}
		if (!obj.has("message") || !obj.has("conversation")) {
			IOHelper.SendResponse(400,
					"<h1>400 Bad request</h1><p>Message or conversation not found in JSON response.</p><p>"
							+ IOHelper.GetPOST(exchange) + "</p>",
					exchange);
			return;
		}
		String message = obj.get("message").getAsString().trim();
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
		MessageChunk chunk = ManagedData.create(MessageChunk.class); // TODO: Automatize
		chunk.setConversation(conv);
		Message msg = new Message();
		msg.setSender(user);
		LogManager.getLogger().debug(message);
		msg.setMessage(message);
		msg.setTime(new Date());
		msg.setMessageChunk(chunk); // TODO: Store relations at one side or both
		chunk.getMessages().add(msg);
		conv.getMesssageChunks().add(chunk);
		DataManager.save(conv);
		LogManager.getLogger().log(Level.DEBUG,
				"Added conversation's message count: " + conv.getMesssageChunks().size());

		IOHelper.SendResponse(200, "Success", exchange);
	}

}
