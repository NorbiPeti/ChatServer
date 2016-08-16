var sendmsg = function sendmsg(msginputta) {
    window.jsonobj = JSON.stringify({ "message": msginputta.value, "conversation": window.convid });
    $.ajax({
        url: "/sendmessage", data: window.jsonobj, method: "POST", success: respfunc, error: respfunc
    });
};

var justsentmsgread = false;
var respfunc = function respfunc(result) {
    var msginput = document.getElementById("msginput");
    if (result != "Success") { //on success result is string
        if (result.responseText.indexOf("JSONERROR") != -1) {
            console.log("Got JSON error. Retrying...");
            console.log(result.responseText);
            sendmsg(msginput);
        }
        else {
            showError(result.responseText);
            msginput.disabled = false;
            msginput.focus();
            resetUnread();
        }
    }
    else {
        msginput.value = "";
        msginput.disabled = false;
        msginput.focus();
        justsentmsgread = true;
    }
};

var sendmsgonenter = function sendmsgonenter(e) {
    var code = e.keyCode || e.which;
    if (code != 13 || e.shiftKey) { //Enter keycode
        return;
    }
    e.preventDefault();
    var textarea = e.target;
    if (textarea.value.trim().length == 0)
        return;
    textarea.disabled = true; //msginput
    window.convid = document.getElementById("convidp").innerText * 1;
    sendmsg(textarea);
};

$(document).ready(function () {
    $('#msginput').on("keydown", sendmsgonenter);
});
