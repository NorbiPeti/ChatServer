/**
 * Created by Norbi on 2016-07-26.
 */
$(document).ready(function () {
    element = document.getElementById("userbox");
    if (element.offsetParent != null) {
        $(element).load("userbox");
    }
});