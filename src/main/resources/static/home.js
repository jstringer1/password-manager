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