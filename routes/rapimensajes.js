module.exports = function (app, gestorBD) {
	
	app.put('/api/mensajes/:id', function(req, res){
		
		var token = req.body.token || req.query.token || req.headers['token'];
		var criterio = {
	            email: app.get('jwt').decode(token, 'secreto').usuario
	        };
		gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                res.status(500);
                res.json({
                    error: "Error al listar las peticiones de amistad"
                })
            }else{		
				var criterioMensaje = {
						$and :[{
							"_id": gestorBD.mongo.ObjectID(req.params.id)
						},
						{
							"destino._id" : usuarios[0]._id
						}]
				};
				
				var mensaje = {
						leido : true
				};
				gestorBD.modificarMensaje(criterioMensaje, mensaje,function(result){
					if(result == null){
						res.status(500);
		                res.json({
		                    error: "se ha producido un error"
		                })
					}else{
						res.status(200);
		                res.json({
		                    mensaje: "mensaje leido",
		                    _id: req.params.id
		                })
					}
				});
            }
		});
	});
	
	app.get('/api/mensajes/:id', function(req, res){
		var token = req.body.token || req.query.token || req.headers['token'];
		var identificado = {
				email : app.get('jwt').decode(token, 'secreto').usuario
		};
		gestorBD.obtenerUsuarios(identificado, function (usuarios) {
			 if (usuarios == null || usuarios.length == 0) {
	             res.status(500);
	             res.json({
	              error: "No se puede acceder a los mensajes"
	             })
			 }else{
				 var criterio = {
						 $or : [{
							 "emisor._id" : gestorBD.mongo.ObjectID(usuarios[0]._id),
							 "destino._id" : gestorBD.mongo.ObjectID(req.params.id)
						 },{
							 "destino._id" : gestorBD.mongo.ObjectID(usuarios[0]._id),
							 "emisor._id" : gestorBD.mongo.ObjectID(req.params.id)
						 }]
				 };
				 gestorBD.obtenerMensajes(criterio, function(mensajes){
					 res.status(200);
					 
					 res.send(JSON.stringify(mensajes));
				 });
			 }
		});
	});

    app.post('/api/mensajes', function (req, res) {
        var token = req.body.token || req.query.token || req.headers['token'];
        // Saco el usuario identificado.
        var identificado = {
            email: app.get('jwt').decode(token, 'secreto').usuario
        };
        //sacar usuario en sesion
        gestorBD.obtenerUsuarios(identificado, function (usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                res.status(500);
                res.json({
                    error: "No se puede crear el mensaje"
                })
            } else {
                //sacar la id del segundo user
                var amigo = {
                    _id: gestorBD.mongo.ObjectID(req.body.destino)
                };
                gestorBD.obtenerUsuarios(amigo, function (usuarioAmigo) {
                    if (usuarioAmigo == null || usuarioAmigo.length == 0) {
                        res.status(500);
                        res.json({
                            error: "El receptor del mensaje:" + amigo._id + " no existe."
                        })
                    } else {
                        //hasta aqui ya funciona en el caso favorable
                        var criterioAmistad = {
                            user1: gestorBD.mongo.ObjectID(usuarios[0]._id),
                            user2: gestorBD.mongo.ObjectID(usuarioAmigo[0]._id)
                        };
                        gestorBD.obtenerAmistad(criterioAmistad, function (amistad) {
                            if (amistad == null || amistad.length == 0) {
                                res.status(500);
                                res.json({
                                    error: "Los usuarios no son amigos."
                                })
                            } else {
                                //crear el comentario con las ids de los dos usuarios y los demas datos
                                var mensaje = {
                                    emisor: usuarios[0],
                                    destino: usuarioAmigo[0],
                                    texto: req.body.texto,
                                    leido: false
                                };
                                gestorBD.crearMensaje(mensaje, function (id) {
                                    if (id == null) {
                                        res.status(500);
                                        res.json({
                                            error: "Error al insertar el mensaje"
                                        })
                                    } else {
                                        res.status(201);
                                        res.json({
                                            mensajes: "mensajes insertado",
                                            _id: id
                                        })
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    });

}