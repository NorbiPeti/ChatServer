package io.github.norbipeti.chat.server.db.domain;

import javax.persistence.*;

import org.jsoup.nodes.Element;

import io.github.norbipeti.chat.server.data.LoaderCollection;

@Entity
@Table(name = "CONVERSATION")
public class Conversation extends SavedData {
	private static final long serialVersionUID = 5058682475353799722L;
	// @Id
	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	// @Column(name = "ID", unique = true, nullable = false)
	private Long id;
	@ElementCollection(fetch = FetchType.EAGER)
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "conversation")
	private LoaderCollection<MessageChunk> messsagechunks = new LoaderCollection<>(MessageChunk.class);
	@ElementCollection(fetch = FetchType.EAGER)
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	// @JoinTable(name = "User_Conversation", joinColumns = @JoinColumn(name =
	// "conversation_id", referencedColumnName = "id", unique = false),
	// inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName =
	// "id", unique = false), uniqueConstraints = @UniqueConstraint(name =
	// "USER_CONV_UN", columnNames = {"user_id", "conversation_id" }))
	// @JoinTable(name = "User_Conversation", joinColumns = @JoinColumn(name =
	// "conversation_id", referencedColumnName = "id"), inverseJoinColumns =
	// @JoinColumn(name = "user_id", referencedColumnName = "id"))
	// @JoinTable(name = "User_Conversation")
	private LoaderCollection<User> users = new LoaderCollection<>(User.class);

	public LoaderCollection<MessageChunk> getMesssageChunks() {
		return messsagechunks;
	}

	public void setMesssageChunks(LoaderCollection<MessageChunk> messsages) {
		this.messsagechunks = messsages;
	}

	public LoaderCollection<User> getUsers() {
		return users;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	protected void setId(long id) {
		this.id = id;
	}

	private Conversation() {
	}

	@Override
	protected void init() {
	}

	public Element getAsHtml(Element conversations) {
		Element conversation = conversations.appendElement("div");
		conversation.addClass("conversation");
		String users = "";
		StringBuilder sb = new StringBuilder();
		for (User item : getUsers()) {
			sb.append(item.getName()).append(", ");
		}
		if (sb.length() > 2)
			sb.replace(sb.length() - 2, sb.length() - 1, "");
		users = sb.toString();
		conversation.appendElement("a").text(users).attr("href", "javascript:changeConversation(" + getId() + ")");
		return conversation;
	}
}
