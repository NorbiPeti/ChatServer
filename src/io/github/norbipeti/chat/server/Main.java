package io.github.norbipeti.chat.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.io.IOUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Main {
	// public static final HashMap<String, Page> Pages = new HashMap<String,
	// Page>();

	public static void main(String[] args) { // http://stackoverflow.com/questions/9266632/access-restriction-is-not-accessible-due-to-restriction-on-required-library/10642163#10642163
		try { // rt.jar Javadoc:
				// https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/
				// https://docs.oracle.com/javase/8/docs/api/
			System.out.println("Loading database...");
			Connection conn = DriverManager.getConnection("jdbc:derby:memory:chatserver;create=true");
			Statement statement = conn.createStatement();
			if (statement.execute("CREATE TABLE users ( username varchar(255), password varchar(255), id int )"))
				System.out.println("Created users table");
			else
				System.out.println("Failed to create users table!");
			ResultSet results = statement.executeQuery("SELECT * FROM users");
			while (results.next())
				results.getString(0);
			System.out.println("Starting webserver...");
			HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLocalHost(), 8080), 10);
			server.createContext("/").setHandler(new HttpHandler() {

				@Override
				public void handle(HttpExchange exchange) {
					try {
						System.out.println("Received request from " + exchange.getRemoteAddress());
						System.out.println("Body: " + IOUtils.toString(exchange.getRequestBody(), "ASCII"));
						System.out.println("Headers: ");
						for (Entry<String, List<String>> entry : exchange.getRequestHeaders().entrySet())
							System.out.println(entry.getKey() + " - " + entry.getValue());
						System.out.println(exchange.getRequestURI().getPath());
						/*
						 * String resp =
						 * Pages.get(exchange.getRequestURI().getPath()).Run(
						 * exchange); exchange.sendResponseHeaders(200,
						 * resp.length()); IOUtils.write(resp,
						 * exchange.getResponseBody());
						 * exchange.getResponseBody().close();
						 */
						exchange.sendResponseHeaders(404, 0);
						exchange.getResponseBody().close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			server.createContext("/register").setHandler(new RegisterPage());
			server.start();
			System.out.println("Ready... Press Enter to stop.");
			System.in.read();
			System.out.println("Stopping...");
			server.stop(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Stopped");
	}
}