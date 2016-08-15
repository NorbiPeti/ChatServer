/**
 * Created by Norbi on 2016-08-05.
 */

$(document).ready(function () {
    var cmsgs = document.getElementById("channelmessages");
    if (cmsgs != null && cmsgs.childElementCount > 0) {
        var nodes = cmsgs.children;
        for (var x = 0; x < nodes.length; x++) {
            var item = nodes[x];
            handlereceivedmessage(item);
        }
    }
});
