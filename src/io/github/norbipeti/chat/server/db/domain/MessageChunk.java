package io.github.norbipeti.chat.server.db.domain;

import java.util.ArrayList;
import java.util.List;

import io.github.norbipeti.chat.server.data.LoaderRef;

public class MessageChunk extends SavedData {
	private static final long serialVersionUID = -1665300779209348467L;

	private List<Message> messages = new ArrayList<>();
	private LoaderRef<Conversation> conversation;

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public LoaderRef<Conversation> getConversation() {
		return conversation;
	}

	public void setConversation(LoaderRef<Conversation> conversation) {
		this.conversation = conversation;
	}
}
