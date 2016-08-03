var sendmsg = function sendmsg(msginputta) {
    window.jsonobj = JSON.stringify({"message": msginputta.value, "conversation": window.convid});
    console.log(window.jsonobj);
    $.ajax({
        url: "/message", data: window.jsonobj, method: "POST", success: respfunc, error: respfunc
    });
};

var respfunc = function respfunc(result) {
    if (result != "Success") { //on success result is string
        var msginput = document.getElementById("msginput");
        if (result.responseText == "JSONERROR") {
            console.log("Got JSON error. Retrying...");
            sendmsg(msginput);
        }
        else {
            var errormsg = document.getElementById("errormsg");
            errormsg.innerHTML = result.responseText;
            errormsg.style = "display: block";
            msginput.disabled = false;
        }
    }
    else
        location.reload(true);
};

var sendmsgonenter = function sendmsgonenter(e) {
    var code = e.keyCode || e.which;
    if (code != 13 || e.shiftKey) { //Enter keycode
        return;
    }
    e.preventDefault();
    var textarea = event.target;
    if (textarea.value.trim().length == 0)
        return;
    textarea.disabled = true; //msginput
    window.convid = document.getElementById("convidp").innerText * 1;
    sendmsg(textarea);
};

$(document).ready(function () {
    $('#msginput').on("keydown", sendmsgonenter);
});
