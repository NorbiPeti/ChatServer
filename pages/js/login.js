/**
 * Created by Norbert_Szatmari on 2016-07-26.
 */
function getFormData($form) {
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};

    $.map(unindexed_array, function (n, i) {
        indexed_array[n['name']] = n['value'];
    });

    return indexed_array;
}

function check(form) {
    var json = JSON.stringify(getFormData($(form)));
    $.ajax({
        url: "/login", data: json, method: "POST", success: function (result) {
            if (result != "Success") {
                var errormsg = document.getElementById("errormsg");
                errormsg.innerHTML = result;
                errormsg.style = "display: block";
            }
        }
    });
}
