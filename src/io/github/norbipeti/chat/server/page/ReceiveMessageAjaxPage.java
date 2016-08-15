package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.Main;
import io.github.norbipeti.chat.server.data.LoaderRef;
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

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		User user = IOHelper.GetLoggedInUser(exchange);
		if (user == null) {
			IOHelper.SendResponse(403, "<p>Please log in to receive messages</p>", exchange);
			return;
		}
		exmap.put(user, exchange);
		/*
		 * String message = obj.get("message").getAsString().trim(); int conversation = obj.get("conversation").getAsInt(); if (message.trim().length() == 0) { IOHelper.SendResponse(400,
		 * "<h1>400 Bad request</h1><p>The message cannot be empty.</p>", exchange); return; } LoaderCollection<Conversation> convos = user.getConversations(); Conversation conv = null;
		 * LogManager.getLogger().log(Level.DEBUG, "Len: " + convos.size()); for (Conversation con : convos) { LogManager.getLogger().log(Level.DEBUG, con.getId()); if (con.getId() == conversation) {
		 * conv = con; break; } } if (conv == null) { IOHelper.SendResponse(400, "<h1>400 Conversation not found</h1><p>The conversation with the id " + conversation + " is not found.</p>", exchange);
		 * return; } MessageChunk chunk = ManagedData.create(MessageChunk.class); chunk.setConversation(conv); Message msg = new Message(); msg.setSender(user); msg.setMessage(message);
		 * msg.setTime(new Date()); msg.setMessageChunk(chunk); chunk.getMessages().add(msg); conv.getMesssageChunks().add(chunk); DataManager.save(conv); LogManager.getLogger().log(Level.DEBUG,
		 * "Added conversation's message count: " + conv.getMesssageChunks().size());
		 */

	}

	public static void sendMessageBack(Message msg, Conversation conv) throws IOException {
		for (User user : conv.getUsers()) {
			LogManager.getLogger().debug("User: " + user);
			if (exmap.containsKey(user)) {
				LogManager.getLogger().debug("Exmap contains user");
				JsonObject msgobj = msg.getAsJson();
				IOHelper.SendResponse(200, msgobj.toString(), exmap.get(user));
				exmap.remove(user);
			} else
				LogManager.getLogger().warn("User is not listening: " + user);
		}
	}
}
