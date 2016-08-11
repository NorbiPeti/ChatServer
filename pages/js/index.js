/**
 * Created by Norbi on 2016-08-05.
 */

$(document).ready(function () {
    var cmsgs = document.getElementById("channelmessages");
    if (cmsgs != null && cmsgs.childElementCount > 0) {
        var nodes = cmsgs.children;
        for (var x = 0; x < nodes.length; x++) {
            var item = nodes[x];
            console.log(item);
            var spans = item.getElementsByTagName("span");
            var ctime = null;
            for (var i = 0; i < spans.length; i++)
                if (spans[i].className.split(' ').indexOf("converttime") > -1)
                    ctime = spans[i];
            if (ctime != null)
                console.log(ctime.innerText);
            if (ctime != null)
                ctime.innerText = moment(ctime.innerText, "YYYY-MM-DDTHH:mm:ssZ").fromNow(); //.format("lll");
            //ctime.innerText = new Date(ctime.innerText * 1).toDateString();
        }
        cmsgs.lastElementChild.scrollIntoView(false);
    }
});
