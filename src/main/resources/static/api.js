var api = (function() {
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
  
  function postJSON( uri, data, callback ) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function() {
      if(req.readyState == 4) {
        callback();
      }
    }
    req.open("POST", uri, true);
    req.setRequestHeader("Content-type", "application/json");
    req.send(JSON.stringify(data));
  }
  
  function postEmpty( uri, callback ) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function() {
      if(req.readyState == 4) {
        callback();
      }
    }
    req.open("POST", uri, true);
    req.send();
  }
  
  var userApi = (function() {
    return {
      getUserDetails: function( callback ) { getForJSON( "./api/user", callback ); },
      resetSecret: function( callback ) { postEmpty( "./api/user/resetsecret", callback ); },
    }
  })();

  var adminApi = (function() {
    return {
      getAllUsers: function( callback ) { getForJSON( "./admin/api/users", callback ); },
      addUser: function( user, callback ) { postJSON( "./admin/api/user", user, callback ); }
    }
  })();

  return { user: userApi, admin: adminApi }
})();