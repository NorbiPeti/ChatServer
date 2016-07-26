$('#msginput').keypress(function(e) {
	if (e.which == '\r'.charCodeAt(0))
		document.write(document.getElementById("msginput").value);
});
