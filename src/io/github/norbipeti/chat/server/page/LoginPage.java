package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.nodes.Element;
import org.mindrot.jbcrypt.BCrypt;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;
import io.github.norbipeti.chat.server.db.DataProvider;
import io.github.norbipeti.chat.server.db.domain.User;

public class LoginPage extends Page {

	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		HashMap<String, String> post = IOHelper.GetPOST(exchange);
		if (post.size() == 0 || !post.containsKey("email") || !post.containsKey("pass")) {
			IOHelper.SendPage(200, this, exchange);
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
			if (loginuser == null || !BCrypt.checkpw(post.get("pass"), loginuser.getPassword())) {
				IOHelper.SendModifiedPage(200, this, (doc) -> {
					Element errorelement = doc.getElementById("errormsg");
					errorelement.appendElement("p").text("The username or password is invalid.");
					errorelement.attr("style", "display: block");
					return doc; // TODO: Automatically redirect on every
								// request, load HTML file directly for login
				}, exchange);
				return;
			}
			IOHelper.LoginUser(exchange, loginuser);
			IOHelper.Redirect("/", exchange);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public String GetName() {
		return "login";
	}

}
