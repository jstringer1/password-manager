(function() {
  function drawUsersTable() {
    api.admin.getAllUsers( function( users ) {
      api.ui.drawTable( "users", [ "username", "secret", "qr" ], users );
    });
  }

  drawUsersTable();

  document.getElementById("addNewUser").addEventListener('click', function() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    var user = { "username":username, "password":password };
    api.admin.addUser( user, function() { drawUsersTable(); } );
  }, false);
})();