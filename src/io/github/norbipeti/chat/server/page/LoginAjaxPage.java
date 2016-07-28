package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;
import io.github.norbipeti.chat.server.db.DataProvider;
import io.github.norbipeti.chat.server.db.domain.User;

public class LoginPage extends Page {

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		JSONObject post = IOHelper.GetPOSTJSON(exchange);
		if (post == null || !post.has("email") || !post.has("pass")) {
			IOHelper.Redirect("/", exchange);
			return;
		}
		try (DataProvider provider = new DataProvider()) {
			User loginuser = null;
			for (User user : provider.getUsers()) {
				if (user.getEmail().equals(post.get("email"))) {
					loginuser = user;
					break;
				}
			}
			if (loginuser == null || !BCrypt.checkpw(post.getString("pass"), loginuser.getPassword())) {
				IOHelper.SendResponse(200, (doc) -> {
					doc.appendElement("p").text("The username or password is invalid.");
					return doc;
				}, exchange);
				return;
			}
			IOHelper.LoginUser(exchange, loginuser, provider);
			IOHelper.SendResponse(200, "Success", exchange);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public String GetName() {
		return "login";
	}

}
