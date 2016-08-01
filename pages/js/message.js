var respfunc = function respfunc(result) {
    if (result != "Success") { //on success result is string
        var errormsg = document.getElementById("errormsg");
        errormsg.innerHTML = result.responseText;
        errormsg.style = "display: block";
    }
    else
        location.reload(true);
};

var sendmsgonenter = function sendmsgonenter(e) { //TODO: Detect Enter
    var code = e.keyCode || e.which;
    if (code != 13 || e.shiftKey) { //Enter keycode
        return;
    }
    e.preventDefault();
    var textarea = event.target;
    if (textarea.value.trim().length == 0)
        return;
    textarea.disabled = true;
    window.convid = 1;
    var json = JSON.stringify({"message": textarea.value, "conversation": window.convid});
    $.ajax({
        url: "/message", data: json, method: "POST", success: respfunc, error: respfunc
    });
};

$(document).ready(function () {
    $('#msginput').on("keydown", sendmsgonenter);
});
