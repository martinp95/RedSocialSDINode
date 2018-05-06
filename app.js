// Módulos
var express = require('express');
var app = express();

app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Credentials", "true");
    res.header("Access-Control-Allow-Methods", "POST, GET, DELETE, UPDATE, PUT");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, token");
    // Debemos especificar todas las headers que se aceptan. Content-Type , token
    next();
});


var jwt = require('jsonwebtoken');
app.set('jwt', jwt);

var log4js = require('log4js');
log4js.configure({
    appenders: {cheese: {type: 'file', filename: 'miLogger.log'}},
    categories: {default: {appenders: ['cheese'], level: 'error'}}
});
var logger = log4js.getLogger();
logger.level = "all";
//app.set('log', logger);

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
app.use(bodyParser.urlencoded({extended: true}));

var gestorBD = require("./modules/gestorBD.js");
gestorBD.init(app, mongo);

// routerUsuarioToken
var routerUsuarioToken = express.Router();
routerUsuarioToken.use(function (req, res, next) {
    // obtener el token, puede ser un parámetro GET , POST o HEADER
    var token = req.body.token || req.query.token || req.headers['token'];
    if (token != null) {
        // verificar el token
        jwt.verify(token, 'secreto', function (err, infoToken) {
            if (err || (Date.now() / 1000 - infoToken.tiempo) > 24000) {
                res.status(403); // Forbidden
                res.json({
                    acceso: false,
                    error: 'Token invalido o caducado'
                });
                // También podríamos comprobar que intoToken.usuario existe
                return;

            } else {
                // dejamos correr la petición
                res.usuario = infoToken.usuario;
                next();
            }
        });

    } else {
        res.status(403); // Forbidden
        res.json({
            acceso: false,
            mensaje: 'No hay Token'
        });
    }
});
// Aplicar routerUsuarioToken
app.use('/api/amigos', routerUsuarioToken);
app.use('/api/mensajes', routerUsuarioToken);
app.use('/api/mensajes/*', routerUsuarioToken);

// routerUsuarioSession
var routerUsuarioSession = express.Router();
routerUsuarioSession.use(function (req, res, next) {
    console.log("routerUsuarioSession");
    if (req.session.usuario) {
        next();
    } else {
        console.log("va a : " + req.session.destino)
        res.redirect("/identificarse");
    }
});

// Aplicar router UserSession
app.use("/user/list", routerUsuarioSession);
app.use("/peticionAmistad/listar", routerUsuarioSession);
app.use("/amistad/listar", routerUsuarioSession);

app.use(express.static('public'));

// variables
app.set('port', 8081);
app.set('db', 'mongodb://admin:admin@ds231549.mlab.com:31549/redsocial');
app.set('clave', 'abcdefg');
app.set('crypto', crypto);

// Rutas controladores por logica
require("./routes/rusers.js")(app, swig, gestorBD, logger)// (app, param 1, param2)
require("./routes/rpeticionAmistad.js")(app, swig, gestorBD)
require("./routes/ramistad.js")(app, swig, gestorBD)
require("./routes/rapiusers.js")(app, gestorBD);
require("./routes/rapiamistad.js")(app, gestorBD);
require("./routes/rapimensajes.js")(app, gestorBD);

app.use(function (err, req, res, next) {
    console.log("Error producido: " + err);
    if (!res.headersSent) {
        res.status(400);
        res.send("Recurso no disponible");
    }
});

app.get('/', function (req, res) {
    var respuesta = swig.renderFile('views/bindex.html', {usuarioSesion: req.session.usuario });
    res.send(respuesta);
});

// lanzar el servidor
app.listen(app.get('port'), function () {
    console.log("Servidor activo");
});

