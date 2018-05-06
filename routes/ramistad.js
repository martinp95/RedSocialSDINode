module.exports = function(app, swig, gestorBD) {
	
	app.get('/amistad/listar', function(req, res){
		
		var criterio = {
				email : req.session.usuario
		};
		gestorBD.obtenerUsuarios(criterio, function(usuarios){
			if (usuarios == null || usuarios.length == 0) {
				res.send("Error al listar las peticiones de amistad");
			} else {
				var criterioAmistad = {
						user1 : gestorBD.mongo.ObjectID(usuarios[0]._id)
				};
				gestorBD.obtenerAmistad(criterioAmistad, function(amistad){
					var user2 = {};
					var idUser2 = [];
					for(x in amistad){
						idUser2.push(gestorBD.mongo
								.ObjectID(amistad[x].user2))
					}
					user2 = {
						_id : {
							$in : idUser2
						}
					};
					var pg = parseInt(req.query.pg);
					if (req.query.pg == null) {
						pg = 1;
					}
					gestorBD.obtenerUsuariosPg(user2, pg, function(
							usariosAmigos, total) {
						var pgUltima = total / 5;
						if (total % 5 > 0) {
							pgUltima = pgUltima + 1;
						}
						var respuesta = swig.renderFile(
								'views/blistaAmigos.html', {
									usuarios : usariosAmigos,
									pgActual : pg,
									pgUltima : pgUltima,
									usuarioSesion: req.session.usuario
								});
						res.send(respuesta);
					});
				});
			}
		});
	});

	app.get('/peticionAmistad/aceptar/:idSender', function(req, res) {
		var criterio = {
			email : req.session.usuario
		};
		gestorBD.obtenerUsuarios(criterio, function(usuarios) {
			if (usuarios == null || usuarios.length == 0) {
				res.send("Error al aceptar la peticion");
			} else {
				var amistadAB = {
					user1 : gestorBD.mongo.ObjectID(usuarios[0]._id),
					user2 : gestorBD.mongo.ObjectID(req.params.idSender)
				}
				var amistadBA = {
					user1 : gestorBD.mongo.ObjectID(req.params.idSender),
					user2 : gestorBD.mongo.ObjectID(usuarios[0]._id)
				}

				var amistad = [ amistadAB, amistadBA ];
				// Crear la relacion de amistad.
				gestorBD.insertarAmistad(amistad, function(id) {
					if (id == null) {
						res.send("Error al insertar");
					} else {
						// Borrar la peticion de amistad
						var peticionAB = {
							_idSender : gestorBD.mongo
									.ObjectID(usuarios[0]._id),
							_idReceiver : gestorBD.mongo
									.ObjectID(req.params.idSender)
						}
						var peticionBA = {
							_idSender : gestorBD.mongo
									.ObjectID(req.params.idSender),
							_idReceiver : gestorBD.mongo
									.ObjectID(usuarios[0]._id)
						}
						// Borrar la peticion de amistad en los dos sentidos.
						gestorBD.eliminarPeticionAmistad(peticionAB, function(
								peticiones) {
							gestorBD.eliminarPeticionAmistad(peticionBA,
									function(peticiones) {
								res.redirect("/peticionAmistad/listar");
									});
						});

					}
				});
			}
		});
	});

}