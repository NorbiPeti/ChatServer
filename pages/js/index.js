/**
 * Created by Norbi on 2016-08-05.
 */

$(document).ready(function () {
    var cmsgs = document.getElementById("channelmessages");
    if (cmsgs != null && cmsgs.childElementCount > 0) {
        cmsgs.forEach(function (item) {
            var ctime = item.getElementById("converttime");
            ctime.innerText = new Date(ctime.innerText * 1).toDateString();
        });
        cmsgs.lastElementChild.scrollIntoView(false);
    }
});
