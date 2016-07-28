/**
 * Created by Norbi on 2016-07-27.
 */
function register(form) {
    var json = JSON.stringify(getFormData($(form)));
    $.ajax({
        url: "/register", data: json, method: "POST", success: function (result) {
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
