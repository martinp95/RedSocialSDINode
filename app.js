// Módulos
var express = require('express');
var app = express();

var expressSession = require('express-session');
app.use(expressSession({
	secret: 'abcdefg',
	resave: true,
	saveUninitialized: true 
}));
var crypto = require('crypto');
var fileUpload = require('express-fileupload');
app.use(fileUpload());
var mongo = require('mongodb');
var swig = require('swig');
var bodyParser = require('express');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

var gestorBD = require("./modules/gestorBD.js");
gestorBD.init(app,mongo);

//Crear el control de acceso por enrutador

app.use(express.static('public'));

//variables
app.set('port', 8081);
app.set('db', 'mongodb://admin:admin@ds231549.mlab.com:31549/redsocial');
app.set('clave','abcdefg'); 
app.set('crypto',crypto);

//Rutas controladores por logica
require("./routes/rusers.js")(app, swig, gestorBD)//(app, param 1, param2)


//lanzar el servidor
app.listen(app.get('port'), function() {
console.log("Servidor activo");
});