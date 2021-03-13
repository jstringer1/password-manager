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

function drawUsersTable( id ) {
	getAllUsers( function( users ) {
	  var table = document.createElement("table");
	  var headerRow = document.createElement("tr");
	  var headerCell = document.createElement("th");
	  headerCell.innerHTML = "USERS";
	  headerRow.appendChild(headerCell);
	  table.appendChild(headerRow);
	  for( var i=0; i<users.length; i++ ) {
	    var row = document.createElement("tr");
	    var cell = document.createElement("td");
	    cell.innerHTML = users[i];
	    row.appendChild(cell);
	    table.appendChild(row);
	  }
	  var div = document.getElementById( id );
	  div.innerHTML = "";
	  div.appendChild(table);
	} );
}