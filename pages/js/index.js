/**
 * Created by Norbi on 2016-08-05.
 */

$(document).ready(function () {
    var cmsgs = document.getElementById("channelmessages");
    if (cmsgs != null && cmsgs.childElementCount > 0)
        cmsgs.lastElementChild.scrollIntoView(false);
});
