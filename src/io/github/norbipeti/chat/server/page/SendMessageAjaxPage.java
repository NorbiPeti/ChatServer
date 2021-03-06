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
		for (Conversation con : convos) {
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
		Message msg = ManagedData.create(Message.class);
		msg.setConversation(conv);
		msg.setSender(user);
		msg.setMessage(message);
		msg.setTime(new Date());
		DataManager.save(conv);

		ReceiveMessageAjaxPage.sendMessageBack(msg, conv);

		IOHelper.SendResponse(200, "Success", exchange);
	}

}
