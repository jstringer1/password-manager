api.user.getUserDetails( function( details ) {
  document.getElementById("username").innerHTML = details.username;
  document.getElementById("secret").innerHTML = details.secret;
} );

document.getElementById("resetsecret").addEventListener('click', function() {
  api.user.resetSecret( function() {
    api.user.getUserDetails( function( details ) {
      document.getElementById("secret").innerHTML = details.secret;
    } );
	      
    var qr = document.getElementById("qr");
    var timestamp = new Date().getTime(); 
    qr.src = "./api/user/qr?timestamp="+timestamp;
  } );
}, false);
	  
document.getElementById("logout").addEventListener('click', function() {
  window.location.href="./logout";
}, false);

api.user.getCredentials( function( credentials ) {
  api.ui.drawTable( "credentials", [ "service", "username", "password" ], credentials );
} );

document.getElementById("saveCredentialsButton").addEventListener('click', function() {
  var service = document.getElementById("service").value;
  var username = document.getElementById("serviceusername").value;
  var password = document.getElementById("password").value;
  var credentials = { "service":service, "username":username, "password":password };
  api.user.saveCredentials( credentials, function() { window.alert("SAVED"); } );
}, false);