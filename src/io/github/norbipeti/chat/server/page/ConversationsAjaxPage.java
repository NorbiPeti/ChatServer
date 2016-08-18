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
			conv.getUsers().add(user);
			user.getConversations().add(conv);
			Document doc = new Document("");
			conv.getAsHtml(doc);
			IOHelper.SendResponse(200, doc.toString(), exchange);
			break;
		}
		case "adduserdialog": {
			Document doc = new Document("");
			Element form = doc.appendElement("form");
			form.attr("method", "POST");
			form.appendElement("h3").text("Add user to conversation");
			form.appendElement("input").attr("type", "text").attr("id", "searchtext");
			form.appendElement("input").attr("type", "button").attr("value", "Search").attr("onclick", "userSearch()");
			form.appendElement("br");
			form.appendElement("select").addClass("list").attr("id", "searchuserlist").attr("size", "10");
			form.appendElement("br");
			form.appendElement("input").attr("type", "button").attr("value", "Add user").attr("onclick",
					"addUserToConv()");
			form.appendElement("input").attr("type", "button").attr("value", "Close").attr("onclick",
					"document.getElementById(\"hoverdialogcont\").style.display = \"none\";");
			IOHelper.SendResponse(200, doc.toString(), exchange);
			break;
		}
		case "adduser": {
			if (user.getCurrentConversation() == null) {
				IOHelper.SendResponse(200, "<p>Not in a conversation!</p>", exchange);
				return;
			}
			if (!post.has("userid")) {
				IOHelper.SendResponse(200, "<p>User ID not found: " + post + "</p>", exchange);
				return;
			}
			long userid = post.get("userid").getAsLong();
			User adduser = DataManager.load(User.class, userid, false);
			if (adduser == null) {
				IOHelper.SendResponse(200, "<p>User not found: " + userid + "</p>", exchange);
				return;
			}
			Conversation currentconversation = user.getCurrentConversation().get();
			if (currentconversation.getUsers().contains(userid)) {
				IOHelper.SendResponse(200, "<p>The user is already in the conversation</p>", exchange);
				return;
			}
			currentconversation.getUsers().add(adduser);
			adduser.getConversations().add(currentconversation);
			IOHelper.SendResponse(200, doc -> currentconversation.getAsHtml(doc).ownerDocument(), exchange);
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
