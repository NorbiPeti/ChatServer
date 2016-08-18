package io.github.norbipeti.chat.server;

import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import io.github.norbipeti.chat.server.data.*;
import io.github.norbipeti.chat.server.db.domain.*;
import io.github.norbipeti.chat.server.io.DataType;
import io.github.norbipeti.chat.server.page.*;

public class Main {
	public static Gson gson;

	public static void main(String[] args) { // http://stackoverflow.com/questions/9266632/access-restriction-is-not-accessible-due-to-restriction-on-required-library/10642163#10642163
		try { // rt.jar Javadoc:
				// https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/
				// https://docs.oracle.com/javase/8/docs/api/
			LogManager.getLogger().log(Level.INFO, "Loading files...");
			DataManager.init();
			final GsonBuilder saveGsonBuilder = new GsonBuilder();
			Reflections rf = new Reflections(new ConfigurationBuilder()
					.setUrls(ClasspathHelper.forClassLoader(ManagedData.class.getClassLoader()))
					.addClassLoader(ManagedData.class.getClassLoader()).addScanners(new SubTypesScanner())
					.filterInputsBy((String pkg) -> pkg.contains(ManagedData.class.getPackage().getName())));
			Set<Class<? extends ManagedData>> datas = rf.getSubTypesOf(ManagedData.class);
			for (Class<? extends ManagedData> data : datas) {
				if (Modifier.isAbstract(data.getModifiers()))
					continue;
				saveGsonBuilder.registerTypeAdapter(new DataType(LoaderCollection.class, data),
						new LoaderCollectionSerializer());
				saveGsonBuilder.registerTypeAdapter(new DataType(LoaderRef.class, data), new LoaderRefSerializer());
			}
			gson = saveGsonBuilder.create();
			/*
			 * User user = new User(); user.setName("asd"); user.setEmail("test@test.com"); User user2 = new User(); user2.setName("Teszt"); user2.setEmail("test2@test.com"); // user =
			 * provider.save(user); // user2 = provider.save(user2); user.getContacts().add(user2); user2.getContacts().add(user); LogManager.getLogger().log(Level.DEBUG, "1st's contact: " +
			 * user.getContacts().get(0)); LogManager.getLogger().log(Level.DEBUG, "2nd's contact: " + user2.getContacts().get(0)); Conversation conversation = new Conversation();
			 * conversation.getUsers().add(user); user.getConversations().add(conversation); LogManager.getLogger().debug("User: " + user); conversation.getUsers().add(user2);
			 * LogManager.getLogger().debug("User2: " + user2); user2.getConversations().add(conversation); Message msg = new Message(); msg.setSender(user); msg.setTime(new Date());
			 * msg.setMessage("Teszt 1"); conversation.getMesssages().add(msg); Message msg2 = new Message(); msg2.setSender(user2); msg2.setTime(new Date()); msg2.setMessage("Teszt 2");
			 * conversation.getMesssages().add(msg2); // provider.save(user); // provider.save(user2);s User loggedinuser = new User(); loggedinuser.setName("NorbiPeti");
			 * loggedinuser.setSessionid("093b1395-8c31-4f3b-ba67-828a755af92e") ; loggedinuser.setEmail("sznp@asd.com"); loggedinuser.getContacts().add(user2);
			 * conversation.getUsers().add(loggedinuser); loggedinuser.getConversations().add(conversation); DataManager.save(user); DataManager.save(user2); DataManager.save(loggedinuser);
			 * DataManager.save(conversation);
			 */
			LogManager.getLogger().log(Level.INFO, "Starting webserver...");
			HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLocalHost(), 8080), 10);
			rf = new Reflections(
					new ConfigurationBuilder().setUrls(ClasspathHelper.forClassLoader(Page.class.getClassLoader()))
							.addClassLoader(Page.class.getClassLoader()).addScanners(new SubTypesScanner())
							.filterInputsBy((String pkg) -> pkg.contains(Page.class.getPackage().getName())));
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
			LogManager.getLogger().log(Level.INFO, "Ready... Press Enter to stop.");
			System.in.read();
			LogManager.getLogger().log(Level.INFO, "Stopping...");
			server.stop(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DataManager.save();
		LogManager.getLogger().log(Level.INFO, "Stopped");
	}

	private static void addPage(HttpServer server, Page page) {
		server.createContext("/" + page.GetName(), page);
	}
}