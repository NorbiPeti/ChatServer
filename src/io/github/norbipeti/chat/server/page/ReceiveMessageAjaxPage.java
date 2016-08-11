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

public class ReceiveMessageAjaxPage extends Page {
	// http://stackoverflow.com/questions/9242404/javascript-listen-to-server
	@Override
	public String GetName() {
		return "receivemessage";
	}

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		User user = IOHelper.GetLoggedInUser(exchange);
		if (user == null) {
			IOHelper.SendResponse(403, "<p>Please log in to receive messages</p>", exchange);
			return;
		}
		JsonObject obj = new JsonObject();
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
		MessageChunk chunk = ManagedData.create(MessageChunk.class);
		chunk.setConversation(conv);
		Message msg = new Message();
		msg.setSender(user);
		msg.setMessage(message);
		msg.setTime(new Date());
		msg.setMessageChunk(chunk);
		chunk.getMessages().add(msg);
		conv.getMesssageChunks().add(chunk);
		DataManager.save(conv);
		LogManager.getLogger().log(Level.DEBUG,
				"Added conversation's message count: " + conv.getMesssageChunks().size());

		IOHelper.SendResponse(200, "Success", exchange);
	}

}
