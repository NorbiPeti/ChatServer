/**
 * Created by Norbi on 2016-07-26.
 */
function login(form) { //TODO: Detect Enter
    var json = JSON.stringify(getFormData($(form)));
    $.ajax({
        url: "/login", data: json, method: "POST", success: function (result) {
            if (result != "Success") {
                var errormsg = document.getElementById("errormsg");
                errormsg.innerHTML = result;
                errormsg.style = "display: block";
            }
            else
                location.reload(true);
        }
    });
}
