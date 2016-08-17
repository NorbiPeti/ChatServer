package io.github.norbipeti.chat.server.page;

import java.io.IOException;

import org.jsoup.nodes.Document;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.db.domain.Conversation;
import io.github.norbipeti.chat.server.db.domain.ManagedData;
import io.github.norbipeti.chat.server.db.domain.User;
import io.github.norbipeti.chat.server.io.IOHelper;

public class ConversationsAjaxPage extends Page {

	@Override
	public String GetName() {
		return "conversations";
	}

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		JsonObject post = IOHelper.GetPOSTJSON(exchange);
		User user = IOHelper.GetLoggedInUser(exchange);
		if (user == null) {
			IOHelper.SendResponse(403, "<h1>403 Login required</h1>", exchange);
			return;
		}
		if (post == null) {
			IOHelper.SendResponse(400,
					"<h1>400 Bad request</h1><p>Not a JSON string: " + IOHelper.GetPOST(exchange) + "</p>", exchange);
			return;
		}
		if (!post.has("action")) {
			IOHelper.SendResponse(400, "<h1>400 Bad request</h1><p>Action missing: " + post + "</p>", exchange);
			return;
		}
		switch (post.get("action").getAsString()) {
		case "add": {
			Conversation conv = ManagedData.create(Conversation.class);
			conv.getUsers().add(user); // TODO: Option to invite people
			user.getConversations().add(conv);
			Document doc = new Document("");
			conv.getAsHtml(doc);
			IOHelper.SendResponse(200, doc.toString(), exchange);
			break;
		}
		default: {
			IOHelper.SendResponse(400,
					"<h1>400 Bad request</h1><p>Unknown action: " + post.get("action").getAsString() + "</p>",
					exchange);
			break;
		}
		}
	}

}
