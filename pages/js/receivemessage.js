function handlereceivedmessage(msgnode) {
    var spans = msgnode.getElementsByTagName("span");
    var ctime = null;
    for (var i = 0; i < spans.length; i++)
        if (spans[i].className.split(' ').indexOf("converttime") > -1)
            ctime = spans[i];
    if (ctime != null)
        ctime.innerText = moment(ctime.innerText, "YYYY-MM-DDTHH:mm:ssZ").fromNow();
    msgnode.scrollIntoView(false);
}

var unreadCount = 0;

var updateUnreadCount = function () {
    if (unreadCount > 0)
        document.title = "(" + unreadCount + ") Chat";
    else
        document.title = "Chat";
    var msgs = document.getElementById("channelmessages").children;
    for (var i = msgs.length - 1; i >= 0; i--) {
        if (i >= msgs.length - unreadCount)
            msgs[i].style.backgroundColor = "darkgray";
        else
            msgs[i].style = "";
    }
};

var addUnread = function addUnread() {
    unreadCount++;
    updateUnreadCount();
};

var resetUnread = function resetUnread() {
    unreadCount = 0;
    updateUnreadCount();
};

(function poll() {
    setTimeout(function () {
        $.ajax({
            url: "/receivemessage", success: function (data) {
                console.log(data);
                var msgelement = document.getElementById("channelmessages").appendChild(document.createElement("div"));
                var header = msgelement.appendChild(document.createElement("p"));
                header.innerText = data.sender.name + " - ";
                var span = header.appendChild(document.createElement("span"));
                span.className = "converttime";
                span.innerText = data.time;
                var body = msgelement.appendChild(document.createElement("p"));
                body.innerText = data.message;
                handlereceivedmessage(msgelement);
                addUnread();
            }, dataType: "json", complete: poll
        });
    }, 100);
})();

$(document).ready(function () {
    $('#msginput').on("focus", resetUnread);
    $('#msginput').on("keydown", resetUnread);
});
