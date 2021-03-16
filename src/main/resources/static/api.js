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
  
  function drawTableHeader( table, model ) {
    var headerRow = document.createElement( "tr" );
    for( var i=0; i<model.length; i++ ) {
      var cell = document.createElement( "th" );
      cell.innerHTML = model[i];
      headerRow.appendChild( cell );
    }
    table.appendChild( headerRow );
  }
  
  function drawPasswordCell( cell, password ) {
      var passwd = document.createElement( "span" );
      passwd.innerHTML = password;
      passwd.style.display = "none";
      var show = document.createElement( "button" );
      var copy = document.createElement( "button" );
      show.innerHTML = "SHOW";
      copy.innerHTML = "COPY";
      cell.appendChild( passwd );
      cell.appendChild( show );
      cell.appendChild( copy );
      show.addEventListener('click', function() {
        if( show.innerHTML == "SHOW" ) {
          show.innerHTML = "HIDE";
          passwd.style.display = "inline";
        } else {
          show.innerHTML = "SHOW";
          passwd.style.display = "none";
        }
      }, false);
      copy.addEventListener('click', function() {
        var clipboard = document.createElement("input");
        clipboard.value = password;
        document.body.appendChild(clipboard);
        clipboard.select();
        clipboard.setSelectionRange(0, 99999);
        document.execCommand("copy");
        document.body.removeChild(clipboard);
      }, false);
  }
  
  function drawTableCell( row, model, data ) {
    var cell = document.createElement( "td" );
    if( model == "qr" ) {
      var qrImg = document.createElement( "img" );
      qrImg.src = "./admin/api/qr/"+data.secret;
      cell.appendChild( qrImg );
    } else if( model == "password" ) {
      drawPasswordCell( cell, data[model]);
    } else {
      cell.innerHTML = data[model];
    }
    row.appendChild( cell );
  }
  
  function drawTableRow( table, model, data ) {
    var row = document.createElement( "tr" );
    for( var i=0; i<model.length; i++ ) {
      drawTableCell( row, model[i], data );
    }
    table.appendChild( row );
  }
  
  function drawTable( id, model, data ) {
    var table = document.createElement( "table" );
    drawTableHeader( table, model );
    for( var i=0; i<data.length; i++ ) {
      drawTableRow( table, model, data[i] );
    }
    document.getElementById( id ).innerHTML = "";
    document.getElementById( id ).appendChild( table );
  }
  
  var userApi = (function() {
    return {
      getUserDetails: function( callback ) { getForJSON( "./api/user", callback ); },
      resetSecret: function( callback ) { postEmpty( "./api/user/resetsecret", callback ); },
      getCredentials: function( callback ) { getForJSON( "./api/user/credentials", callback ); },
      saveCredentials: function( credentials, callback ) { postJSON( "./api/user/credentials", credentials, callback ); }
    }
  })();

  var adminApi = (function() {
    return {
      getAllUsers: function( callback ) { getForJSON( "./admin/api/users", callback ); },
      addUser: function( user, callback ) { postJSON( "./admin/api/user", user, callback ); }
    }
  })();

  return { user: userApi, admin: adminApi, ui: { drawTable: drawTable } }
})();