function userSearch() {
    var json = JSON.stringify({ "action": "search", "searchstr": document.getElementById("searchtext").value });
    $.ajax({
        url: "/users", data: json, method: "POST", success: function (result) {
            document.getElementById("searchuserlist").innerHTML = result;
        }, error: function (result) {
            showError(result.responseText);
        }
    });
}
