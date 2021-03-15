(function() {
  function drawUsersTable( id ) {
    api.admin.getAllUsers( function( users ) {
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

  drawUsersTable("users");

  document.getElementById("addNewUser").addEventListener('click', function() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    var user = { "username":username, "password":password };
    api.admin.addUser( user, function() { drawUsersTable("users"); } );
  }, false);
})();