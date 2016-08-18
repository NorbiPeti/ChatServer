package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.data.DataManager;
import io.github.norbipeti.chat.server.db.domain.ManagedData;
import io.github.norbipeti.chat.server.db.domain.User;
import io.github.norbipeti.chat.server.io.IOHelper;

public class RegisterAjaxPage extends Page {
	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		JsonObject post = IOHelper.GetPOSTJSON(exchange);
		if (post != null) {
			String errormsg = CheckValues(post, "name", "email", "pass", "pass2");
			if (errormsg.length() > 0) {
				final String msg = errormsg;
				IOHelper.SendResponse(200, (doc) -> doc.html(msg).ownerDocument(), exchange);
				return; // TODO: Use JavaScript too, for error checks
			}
			for (User user : DataManager.getAll(User.class)) { // TODO: Optimize
				if (post.get("email").getAsString().equals(user.getEmail())) {
					errormsg += "<p>An user with this E-mail already exists</p>";
					break;
				}
			}
			if (!post.get("pass").getAsString().equals(post.get("pass2").getAsString()))
				errormsg += "<p>The passwords don't match</p>";
			if (errormsg.length() > 0) {
				final String msg = errormsg;
				IOHelper.SendResponse(200, (doc) -> doc.html(msg).ownerDocument(), exchange);
				return; // TODO: Fix: java.io.FileNotFoundException: data\Conversation-1.json (The process cannot access the file because it is being used by another process)
			}
			User user = ManagedData.create(User.class);
			user.setName(post.get("name").getAsString());
			user.setEmail(post.get("email").getAsString());
			user.setSalt(BCrypt.gensalt()); // http://www.mindrot.org/projects/jBCrypt/
			user.setPassword(BCrypt.hashpw(post.get("pass").getAsString(), user.getSalt()));
			IOHelper.LoginUser(exchange, user);
			DataManager.save(user);
			IOHelper.SendResponse(200, "Success", exchange);
			return;
		}
		IOHelper.Redirect("/", exchange);
	}

	private String CheckValues(JsonObject post, String... values) {
		String errormsg = "";
		for (String value : values)
			if (!CheckValue(post.get(value).getAsString()))
				errormsg += "<p>" + value + " can't be empty</p>";
		return errormsg;
	}

	private boolean CheckValue(String val) {
		return val != null && val.length() > 0;
	}

	@Override
	public String GetName() {
		return "register";
	}

}
