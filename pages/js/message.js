var respfunc = function (result) {
    if (result.responseText != "Success") {
        var errormsg = document.getElementById("errormsg");
        errormsg.innerHTML = result.responseText;
        errormsg.style = "display: block";
    }
    else
        location.reload(true);
}

var sendmsgonenter = function (e) { //TODO: Detect Enter
    console.log("A");
    var code = e.keyCode || e.which;
    if (code != 13) { //Enter keycode
        return;
    }
    console.log("B");
    var textarea = event.target;
    window.convid = 0;
    var json = JSON.stringify({"message": textarea.value, "conversation": window.convid});
    $.ajax({
        url: "/message", data: json, method: "POST", success: respfunc, error: respfunc
    });
};

$(document).bind("ready", function () {
    console.log("X");
    $('#msginput').keypress = sendmsgonenter;
});

console.log(respfunc);
console.log(sendmsgonenter);

$('#msginput').bind("keypress", sendmsgonenter);
