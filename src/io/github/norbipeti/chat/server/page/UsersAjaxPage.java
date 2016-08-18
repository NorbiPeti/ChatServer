package io.github.norbipeti.chat.server.page;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.data.DataManager;
import io.github.norbipeti.chat.server.db.domain.Conversation;
import io.github.norbipeti.chat.server.db.domain.ManagedData;
import io.github.norbipeti.chat.server.db.domain.User;
import io.github.norbipeti.chat.server.io.IOHelper;

public class UsersAjaxPage extends Page {

	@Override
	public String GetName() {
		return "users";
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
		case "search": {
			if (!post.has("searchstr")) {
				IOHelper.SendResponse(400, "<h1>400 Bad request</h1><p>Search string missing: " + post + "</p>",
						exchange);
				return;
			}
			String searchstr = post.get("searchstr").getAsString().toLowerCase();
			Document doc = new Document("");
			if (searchstr.length() != 0)
				for (User suser : DataManager.getAll(User.class))
					if (suser.getEmail().toLowerCase().contains(searchstr)
							|| suser.getName().toLowerCase().contains(searchstr))
						doc.appendElement("option").addClass("resuser").attr("value", suser.getId() + "")
								.text(suser.getName());
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
