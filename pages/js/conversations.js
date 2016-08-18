function addConversation() {
    var json = JSON.stringify({ "action": "add" });
    $.ajax({
        url: "/conversations", data: json, method: "POST", success: function (result) {
            document.getElementById("conversations").innerHTML += result;
            showAddUserToConv();
        }, error: function (result) {
            showError(result.responseText);
        }
    });
    return false;
}

function showAddUserToConv() {
    var json = JSON.stringify({ "action": "adduserdialog" });
    $.ajax({
        url: "/conversations", data: json, method: "POST", success: function (result) {
            document.getElementById("hoverdialog").innerHTML = result;
            document.getElementById("hoverdialogcont").style.display = "table";
        }, error: function (result) {
            showError(result.responseText);
        }
    });
}

function addUserToConv() {
    var liste = document.getElementById("searchuserlist");
    var json = JSON.stringify({ "action": "adduser", "userid": liste.options[liste.selectedIndex].value });
    $.ajax({
        url: "/conversations", data: json, method: "POST", success: function (result) {
            document.getElementById("hoverdialogcont").style.display = "none";
        }, error: function (result) {
            showError(result.responseText);
        }
    });
}
