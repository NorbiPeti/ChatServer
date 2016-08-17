/**
 * Created by Norbi on 2016-08-05.
 */

$(document).ready(function () {
    conversationChanged();
});

function conversationChanged() {
    var cmsgs = document.getElementById("channelmessages");
    if (cmsgs != null && cmsgs.childElementCount > 0) {
        var nodes = cmsgs.getElementsByClassName("chmessage");
        for (var x = 0; x < nodes.length; x++) {
            var item = nodes[x];
            handlereceivedmessage(item);
        }
    }
    if (document.getElementById("convidp").innerText == "-1") {
        document.getElementById("msginput").style.display = "none";
        stopPoll();
    }
    else {
        document.getElementById("msginput").style.display = "block";
        startPoll();
    }
}

function changeConversation(convid) {
    var convidp = document.getElementById("convidp");
    if (convidp.innerText == convid + "")
        return;
    if (!canswitchconversations)
        return;
    canswitchconversations = false;
    stopPoll();
    var chmsgse = document.getElementById("channelmessages");
    var chmsgs = chmsgse.getElementsByClassName("chmessage");
    for (var i = 0; i < chmsgs.length; i++)
        chmsgse.removeChild(chmsgs[i]);
    convidp.innerText = convid;
    conversationChanged();
}
