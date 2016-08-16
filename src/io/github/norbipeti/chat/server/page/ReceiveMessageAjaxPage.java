package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.text.html.HTMLDocument;

import org.apache.logging.log4j.LogManager;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.db.domain.Conversation;
import io.github.norbipeti.chat.server.db.domain.Message;
import io.github.norbipeti.chat.server.db.domain.User;
import io.github.norbipeti.chat.server.io.IOHelper;

public class ReceiveMessageAjaxPage extends Page {
	// http://stackoverflow.com/questions/9242404/javascript-listen-to-server
	// https://techoctave.com/c7/posts/60-simple-long-polling-example-with-javascript-and-jquery
	@Override
	public String GetName() {
		return "receivemessage";
	}

	public static HashMap<User, HttpExchange> exmap = new HashMap<>();
	public static HashMap<User, ArrayList<Message>> unsentmessages = new HashMap<>();

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		User user = IOHelper.GetLoggedInUser(exchange);
		if (user == null) {
			IOHelper.SendResponse(403, "<p>Please log in to receive messages</p>", exchange);
			return;
		}
		exmap.put(user, exchange);
	}

	public static void sendMessageBack(Message msg, Conversation conv) throws IOException {
		for (User user : conv.getUsers()) { // TODO: Load older messages when scrolling up
			if (unsentmessages.containsKey(user) && unsentmessages.get(user).size() > 10) {
				unsentmessages.get(user).clear();
			}
			if (!unsentmessages.containsKey(user))
				unsentmessages.put(user, new ArrayList<Message>());
			unsentmessages.get(user).add(msg);
			if (exmap.containsKey(user)) {
				Iterator<Message> it = unsentmessages.get(user).iterator();
				String finalmsghtml = "";
				while (it.hasNext()) {
					Message entry = it.next();
					Element msgobj = entry.getAsHTML(new Document("")); // TODO: Only send messages if the user's current conversation matches
					finalmsghtml += msgobj.toString() + "\n";
					try {
						it.remove(); // Remove sent message
					} catch (Exception e) { // Remove users even if an error occurs (otherwise they may not be able to send a new/ message due to "headers already sent")
						e.printStackTrace();
					}
				}
				IOHelper.SendResponse(200, finalmsghtml, exmap.get(user));
				exmap.remove(user);
				if (unsentmessages.get(user).size() == 0)
					unsentmessages.remove(user);
			} else {
				LogManager.getLogger().warn("User is not listening: " + user);
			}
		}
	}
}
