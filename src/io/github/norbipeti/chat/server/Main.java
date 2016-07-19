package io.github.norbipeti.chat.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import io.github.norbipeti.chat.server.db.DataProvider;
import io.github.norbipeti.chat.server.page.*;

public class Main {
	// public static final HashMap<String, Page> Pages = new HashMap<String,
	// Page>();

	public static void main(String[] args) { // http://stackoverflow.com/questions/9266632/access-restriction-is-not-accessible-due-to-restriction-on-required-library/10642163#10642163
		try { // rt.jar Javadoc:
				// https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/
				// https://docs.oracle.com/javase/8/docs/api/
			System.out.println("Loading database...");
			try (DataProvider provider = new DataProvider()) {

			}
			System.out.println("Starting webserver...");
			HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLocalHost(), 8080), 10);
			addPage(server, new IndexPage());
			addPage(server, new RegisterPage());
			addPage(server, new LoginPage());
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

	private static void addPage(HttpServer server, Page page) {
		server.createContext("/" + page.GetName(), page);
	}
}