function getForJSON( uri, callback ) {
	var req = new XMLHttpRequest();
	req.onreadystatechange = function() {
		if(req.readyState == 4) {
			var details = JSON.parse(req.responseText);
			callback(details);
		}
	}
	req.open("GET", uri);
	req.send();
}

function getUserDetails( callback ) {
	getForJSON( "./api/user", callback );
}

function getAllUsers( callback ) {
	getForJSON( "./admin/api/users", callback );
}

function addUser( user, callback ) {
	var req = new XMLHttpRequest();
	req.onreadystatechange = function() {
		if(req.readyState == 4) {
			callback();
		}
	}
	req.open("POST", "./admin/api/user", true);
	req.setRequestHeader("Content-type", "application/json");
    req.send(JSON.stringify(user));
}

function resetSecret( callback ) {
	var req = new XMLHttpRequest();
	req.onreadystatechange = function() {
		if(req.readyState == 4) {
			callback();
		}
	}
	req.open("POST", "./api/user/resetsecret", true);
    req.send();
}

function drawUsersTable( id ) {
	getAllUsers( function( users ) {
	  var table = document.createElement("table");
	  var headerRow = document.createElement("tr");
	  var usernameHeader = document.createElement("th");
	  var secretHeader = document.createElement("th");
	  var qrHeader = document.createElement("th");
	  usernameHeader.innerHTML = "username";
	  secretHeader.innerHTML = "secret";
	  headerRow.appendChild(usernameHeader);
	  headerRow.appendChild(secretHeader);
	  headerRow.appendChild(qrHeader);
	  table.appendChild(headerRow);
	  for( var i=0; i<users.length; i++ ) {
	    var row = document.createElement("tr");
	    var username = document.createElement("td");
	    var secret = document.createElement("td");
	    var qr = document.createElement("td");
	    var qrImg = document.createElement("img");
	    username.innerHTML = users[i].username;
	    secret.innerHTML = users[i].secret;
	    qrImg.src = "./admin/api/qr/"+users[i].secret;
	    qr.appendChild(qrImg);
	    row.appendChild(username);
	    row.appendChild(secret);
	    row.appendChild(qr);
	    table.appendChild(row);
	  }
	  var div = document.getElementById( id );
	  div.innerHTML = "";
	  div.appendChild(table);
	} );
}