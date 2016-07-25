package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import java.util.HashMap;

import org.mindrot.jbcrypt.BCrypt;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;
import io.github.norbipeti.chat.server.db.DataProvider;
import io.github.norbipeti.chat.server.db.domain.User;

public class RegisterPage extends Page {
	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		HashMap<String, String> post = IOHelper.GetPOST(exchange);
		if (post.size() > 0) {
			String errormsg = CheckValues(post, "name", "email", "pass", "pass2");
			if (errormsg.length() > 0) {
				final String msg = errormsg;
				IOHelper.SendModifiedPage(200, this, (doc) -> doc.getElementById("errormsg").text(msg).ownerDocument(),
						exchange);
				return; // TODO: Use JavaScript too, for error checks
			}
			try (DataProvider provider = new DataProvider()) {
				for (User user : provider.getUsers()) {
					if (post.get("email").equals(user.getEmail())) {
						errormsg += "<p>An user with this name already exists</p>";
						break;
					}
				}
				if (!post.get("pass").equals(post.get("pass2")))
					errormsg += "<p>The passwords don't match</p>";
				if (errormsg.length() > 0) {
					final String msg = errormsg;
					IOHelper.SendModifiedPage(200, this,
							(doc) -> doc.getElementById("errormsg").text(msg).ownerDocument(), exchange);
					return;
				}
				User user = new User();
				user.setName(post.get("name"));
				user.setEmail(post.get("email"));
				user.setSalt(BCrypt.gensalt()); // http://www.mindrot.org/projects/jBCrypt/
				user.setPassword(BCrypt.hashpw(post.get("pass"), user.getSalt()));
				provider.addUser(user);
				IOHelper.LoginUser(exchange, user);
				IOHelper.Redirect("/", exchange);
			}
			return; // TODO: Only show tag when needed
		}
		IOHelper.SendPage(200, this, exchange);
	}

	private String CheckValues(HashMap<String, String> post, String... values) {
		String errormsg = "";
		for (String value : values)
			if (!CheckValue(post.get(value)))
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
