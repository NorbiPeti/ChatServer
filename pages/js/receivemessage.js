(function poll() {
   setTimeout(function() {
       $.ajax({ url: "/receivemessage", success: function(data) {
            
       }, dataType: "json", complete: poll });
    }, 100);
})();
