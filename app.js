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

// routerUsuarioSession
var routerUsuarioSession = express.Router();
routerUsuarioSession.use(function(req, res, next) {
	console.log("routerUsuarioSession");
	if ( req.session.usuario ) {
			next(); 
		} else {
			console.log("va a : " + req.session.destino) 
			res.redirect("/identificarse"); 
		} 
	});

// Aplicar router UserSession
app.use("/listUsers", routerUsuarioSession);

app.use(express.static('public'));

// variables
app.set('port', 8081);
app.set('db', 'mongodb://admin:admin@ds231549.mlab.com:31549/redsocial');
app.set('clave','abcdefg'); 
app.set('crypto',crypto);

// Rutas controladores por logica
require("./routes/rusers.js")(app, swig, gestorBD)// (app, param 1, param2)

app.use(function(err,req,res,next){
	console.log("Error producido: " + err);
	if (! res.headersSent) {
		res.status(400); 
		res.send("Recurso no disponible");
	}
});

app.get('/', function(req, res){
	var respuesta = swig.renderFile('views/bindex.html', {});
	res.send(respuesta);
});

// lanzar el servidor
app.listen(app.get('port'), function() {
console.log("Servidor activo");
});

