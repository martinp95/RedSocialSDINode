module.exports = function (app, gestorBD) {
    app.post("/api/autenticar/", function (req, res) {
        var seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');
        var criterio = {
            email: req.body.email,
            password: seguro
        }

        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                res.status(401); // Unauthorized
                res.json({
                    autenticado: false
                })
            } else {
                var token = app.get('jwt').sign(
                    {
                        usuario: criterio.email,
                        tiempo: Date.now() / 1000
                    },
                    "secreto");
                res.status(200);
                res.json({
                        autenticado: true,
                        token: token
                    }
                )
            }

        });
    });

    app.get("/api/amigos", function (req, res) {
        var token = req.headers['token'];

        var criterio = {
            email: app.get('jwt').decode(token, 'secreto').usuario
        };
        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                res.status(500);
                res.json({
                    error: "Error al listar las peticiones de amistad"
                })
            } else {
                var criterioAmistad = {
                    user1: gestorBD.mongo.ObjectID(usuarios[0]._id)
                };
                gestorBD.obtenerAmistad(criterioAmistad, function (amistad) {
                    var user2 = {};
                    var idUser2 = [];
                    for (x in amistad) {
                        idUser2.push(gestorBD.mongo
                            .ObjectID(amistad[x].user2))
                    }
                    user2 = {
                        _id: {
                            $in: idUser2
                        }
                    }
                    gestorBD.obtenerUsuarios(user2, function (
                        usariosAmigos) {
                        res.status(200);
                        res.send(JSON.stringify(usariosAmigos));
                    });
                });
            }
        });
    });
}