function updatemsgtime(msgnode) {
    var spans = msgnode.getElementsByTagName("span");
    var ctime = null;
    for (var i = 0; i < spans.length; i++)
        if (spans[i].className.split(' ').indexOf("converttime") > -1)
            ctime = spans[i];
    if (ctime != null)
        ctime.innerText = moment($(ctime).data("val"), "YYYY-MM-DDTHH:mm:ssZ").fromNow();
}

function handlereceivedmessage(msgnode) {
    updatemsgtime(msgnode);
    msgnode.scrollIntoView(false);
}

var unreadCount = 0;

var updateUnreadCount = function () {
    if (unreadCount > 0)
        document.title = "(" + unreadCount + ") Chat";
    else
        document.title = "Chat";
    var msgs = document.getElementById("channelmessages").getElementsByClassName("chmessage");
    for (var i = msgs.length - 1; i >= 0; i--) {
        if (i >= msgs.length - unreadCount)
            msgs[i].style.backgroundColor = "darkgray";
        else
            msgs[i].style = "";
        updatemsgtime(msgs[i]);
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

var readTimer = null;
$(document).ready(function () {
    $('#msginput').on("focus", function () { readTimer == null ? readTimer = setTimeout(function () { resetUnread(); }, 3000) : readTimer; });
    $('#msginput').on("keydown", resetUnread);
    $('#msginput').on("blur", function () { readTimer != null ? clearTimeout(readTimer) : readTimer; });

    if (isLoggedIn())
        (function poll() {
            setTimeout(function () {
                $.ajax({
                    url: "/receivemessage", success: function (data) {
                        var msgs = document.getElementById("channelmessages");
                        msgs.innerHTML += data;
                        var msgelement = msgs.children[msgs.children.length - 1];
                        handlereceivedmessage(msgelement);
                        if (justsentmsgread)
                            justsentmsgread = false;
                        else
                            addUnread();
                    }, error: function (data) {
                        showError(data.responseText);
                    }, dataType: "text", complete: poll
                });
            }, 100);
        })();
});
