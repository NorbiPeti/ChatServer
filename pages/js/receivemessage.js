var handlereceivedmessage = function handlereceivedmessage(msgnode) {
    var spans = msgnode.getElementsByTagName("span");
    var ctime = null;
    for (var i = 0; i < spans.length; i++)
        if (spans[i].className.split(' ').indexOf("converttime") > -1)
            ctime = spans[i];
    if (ctime != null)
        ctime.innerText = moment(ctime.innerText, "YYYY-MM-DDTHH:mm:ssZ").fromNow();
    msgnode.scrollIntoView(false);
}

    (function poll() {
        setTimeout(function () {
            $.ajax({
                url: "/receivemessage", success: function (data) {
                    console.log(data);
                    var msgelement = document.getElementById("channelmessages").appendChild(document.createElement("div"));
                    var header = msgelement.appendChild(document.createElement("p");
                    header.innerText = data.sender.name + " - ";
                    var isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    header.appendElement("span").addClass("converttime")
                        .value = isoFormat.format(data.time) + "+00:00";
                    var body = msgelement.appendChild(document.createElement("p"));
                    body.innerText = data.message;
                    handlereceivedmessage(msgnode);
                }, dataType: "json", complete: poll
            });
        }, 100);
    })();