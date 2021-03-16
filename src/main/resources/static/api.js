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
  
  function drawTableCell( row, model, data ) {
    var cell = document.createElement( "td" );
    if( model == "qr" ) {
      var qrImg = document.createElement( "img" );
      qrImg.src = "./admin/api/qr/"+data.secret;
      cell.appendChild( qrImg );
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