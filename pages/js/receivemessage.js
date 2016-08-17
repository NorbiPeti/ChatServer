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
            msgs[i].classList.add("unreadmsg");
        else
            msgs[i].classList.remove("unreadmsg");
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

var shouldpoll = false;
function poll() {
    setTimeout(function () {
        if (!shouldpoll)
            return;
        $.ajax({
            url: "/receivemessage", data: document.getElementById("convidp").innerText, success: function (data) {
                var msgs = document.getElementById("channelmessages");
                msgs.innerHTML += data;
                var msgelement = msgs.children[msgs.children.length - 1];
                handlereceivedmessage(msgelement);
                if (justsentmsgread)
                    justsentmsgread = false;
                else
                    addUnread();
            }, error: function (data) {
                if (data.responseText) {
                    if (data.responseText.indexOf("ERROR") == -1)
                        showError(data.responseText);
                    else
                        console.log("Got empty string error...");
                }
                else
                    showError("Can't connect to the server!");
            }, dataType: "text", complete: poll, method: "POST"
        });
    }, 100);
};

function startPoll() {
    if (!shouldpoll) {
        shouldpoll = true;
        poll();
    }
}

function stopPoll() {
    shouldpoll = false;
}

var readTimer = null;
$(document).ready(function () {
    $('#msginput').on("focus", function () { readTimer == null ? readTimer = setTimeout(function () { resetUnread(); }, 3000) : readTimer; });
    $('#msginput').on("keydown", resetUnread);
    $('#msginput').on("blur", function () { readTimer != null ? clearTimeout(readTimer) : readTimer; });
});
