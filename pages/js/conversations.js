function addConversation() {
    var json = JSON.stringify({ "action": "add" });
    $.ajax({
        url: "/conversations", data: json, method: "POST", success: function (result) {
            document.getElementById("conversations").innerHTML += result;
        }, error: function (result) {
            showError(result.responseText);
        }
    });
    return false;
}