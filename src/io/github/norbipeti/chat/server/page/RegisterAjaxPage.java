package io.github.norbipeti.chat.server.page;

import java.io.IOException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import com.sun.net.httpserver.HttpExchange;

import io.github.norbipeti.chat.server.IOHelper;
import io.github.norbipeti.chat.server.db.DataProvider;
import io.github.norbipeti.chat.server.db.domain.User;

public class RegisterPage extends Page {
	@Override
	public void handlePage(HttpExchange exchange) throws IOException {
		JSONObject post = IOHelper.GetPOSTJSON(exchange);
		if (post != null) {
			String errormsg = CheckValues(post, "name", "email", "pass", "pass2");
			if (errormsg.length() > 0) {
				final String msg = errormsg;
				IOHelper.SendResponse(200, (doc) -> doc.html(msg).ownerDocument(), exchange);
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
					IOHelper.SendResponse(200, (doc) -> doc.html(msg).ownerDocument(), exchange);
					return;
				}
				User user = new User();
				user.setName(post.getString("name"));
				user.setEmail(post.getString("email"));
				user.setSalt(BCrypt.gensalt()); // http://www.mindrot.org/projects/jBCrypt/
				user.setPassword(BCrypt.hashpw(post.getString("pass"), user.getSalt()));
				provider.saveUser(user);
				User managedUser = provider.getUser(user.getId());
				IOHelper.LoginUser(exchange, managedUser, provider);
				IOHelper.SendResponse(200, "Success", exchange);
			} catch (Exception e) {
				throw e;
			}
			return;
		}
		IOHelper.Redirect("/", exchange);
	}

	private String CheckValues(JSONObject post, String... values) {
		String errormsg = "";
		for (String value : values)
			if (!CheckValue(post.getString(value)))
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
