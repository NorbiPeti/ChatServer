package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;
import io.github.norbipeti.chat.server.data.DataManager;
import io.github.norbipeti.chat.server.db.domain.User;

public class LoginAjaxPage extends Page {

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		JsonObject post = IOHelper.GetPOSTJSON(exchange);
		if (post == null || !post.has("email") || !post.has("pass")) {
			IOHelper.Redirect("/", exchange);
			return;
		}
		User loginuser = null;
		for (User user : DataManager.load(User.class)) {
			if (user.getEmail().equals(post.get("email"))) {
				loginuser = user;
				break;
			}
		}
		if (loginuser == null || !BCrypt.checkpw(post.get("pass").getAsString(), loginuser.getPassword())) {
			IOHelper.SendResponse(200, (doc) -> {
				doc.appendElement("p").text("The username or password is invalid.");
				return doc;
			}, exchange);
			return;
		}
		IOHelper.LoginUser(exchange, loginuser);
		IOHelper.SendResponse(200, "Success", exchange);
	}

	@Override
	public String GetName() {
		return "login";
	}

}
