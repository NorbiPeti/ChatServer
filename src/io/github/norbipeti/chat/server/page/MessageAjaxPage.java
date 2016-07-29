package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;
import io.github.norbipeti.chat.server.db.DataProvider;
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
		JSONObject obj = IOHelper.GetPOSTJSON(exchange);
		if (obj == null) {
			IOHelper.SendResponse(400, "<h1>400 Bad request</h1><p>Not a JSON string!</p>", exchange);
			return;
		}
		String message = obj.getString("message");
		int conversation = obj.getInt("conversation");
		Set<Conversation> convos = user.getConversations();
		Conversation conv = null;
		System.out.println("Len: " + convos.size());
		for (Conversation con : convos) {
			System.out.println(con.getId());
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
		try (DataProvider provider = new DataProvider()) {
			Message msg = new Message();
			msg.setSender(user);
			msg.setMessage(message);
			msg.setTime(new Date());
			conv.getMesssages().add(msg);
			provider.saveConversation(conv);
			System.out.println(conv.getMesssages().size());
		} catch (Exception e) {
			throw e;
		}

		IOHelper.SendResponse(200, "Success", exchange);
	}

}
