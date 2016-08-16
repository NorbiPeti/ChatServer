/**
 * Created by Norbi on 2016-07-27.
 */
function getFormData($form) {
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};

    $.map(unindexed_array, function (n, i) {
        indexed_array[n['name']] = n['value'];
    });

    return indexed_array;
}

function showError(message) {
    var errormsg = document.getElementById("errormsg");
    errormsg.innerHTML = message;
    errormsg.style = "display: block";
    setTimeout(function () { errormsg.style.display = "none"; }, 2000);
}
