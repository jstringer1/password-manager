function getUserDetails( callback ) {
	var req = new XMLHttpRequest();
	req.onreadystatechange = function() {
		if(req.readyState == 4) {
			var details = JSON.parse(req.responseText);
			callback(details);
		}
	}
	req.open("GET", "./api/user");
	req.send();
}