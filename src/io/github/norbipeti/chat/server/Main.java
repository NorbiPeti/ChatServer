package io.github.norbipeti.chat.server;

import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.sun.net.httpserver.HttpServer;
import io.github.norbipeti.chat.server.db.*;
import io.github.norbipeti.chat.server.db.domain.*;
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
				User user = new User();
				user.setName("asd");
				user.setEmail("test@test.com");
				User user2 = new User();
				user2.setName("Teszt");
				user2.setEmail("test2@test.com");
				user2.getContacts().add(user);
				provider.saveUser(user);
				List<User> users = provider.getUsers();
				user = users.get(0);
				user.getContacts().add(user2);
				provider.saveUser(user2);
				users = provider.getUsers();
				user2 = users.get(1);
				System.out.println(users);
				System.out.println("1st's contact: " + user.getContacts().get(0));
				System.out.println("2nd's contact: " + user2.getContacts().get(0));
				Conversation convo = new Conversation();
				convo.getUsers().add(user);
				convo.getUsers().add(user2);
				Message msg = new Message();
				msg.setSender(user);
				msg.setTime(new Date());
				msg.setMessage("Teszt 1");
				convo.getMesssages().add(msg);
				Message msg2 = new Message();
				msg2.setSender(user2);
				msg2.setTime(new Date());
				msg2.setMessage("Teszt 2");
				convo.getMesssages().add(msg2);
				provider.saveConversation(convo);
			}
			System.out.println("Starting webserver...");
			HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLocalHost(), 8080), 10);
			Reflections rf = new Reflections(
					new ConfigurationBuilder().setUrls(ClasspathHelper.forClassLoader(Page.class.getClassLoader()))
							.addClassLoader(Page.class.getClassLoader()).addScanners(new SubTypesScanner())
							.filterInputsBy((String pkg) -> pkg.contains("io.github.norbipeti.chat.server.page")));
			Set<Class<? extends Page>> pages = rf.getSubTypesOf(Page.class);
			for (Class<? extends Page> page : pages) {
				try {
					if (Modifier.isAbstract(page.getModifiers()))
						continue;
					Page p = page.newInstance();
					addPage(server, p);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
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