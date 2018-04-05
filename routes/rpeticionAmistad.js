module.exports = function(app, swig, gestorBD) {

	app.get('/peticionAmistad/listar', function(req, res) {

		var criterio = {
			email : req.session.usuario
		};
		// sacar el id del usuario en sesion a traves de su correo.
		gestorBD.obtenerUsuarios(criterio, function(usuarios) {
			if (usuarios == null || usuarios.length == 0) {
				res.send("Error al listar las peticiones de amistad");
			} else {
				var peticion = {
					_idReceiver : gestorBD.mongo.ObjectID(usuarios[0]._id)
				}
				// Coger las peticiones en las que el reciver sea ese id
				gestorBD.obtenerPeticionesAmistad(peticion, function(
						peticionesAmistad) {

					var sender = {}

					var idSenders = [];
					for (x in peticionesAmistad) {
						idSenders.push(gestorBD.mongo
								.ObjectID(peticionesAmistad[x]._idSender));
					}
					sender = {
						_id : {
							$in : idSenders
						}
					}

					var pg = parseInt(req.query.pg);
					if (req.query.pg == null) {
						pg = 1;
					}

					gestorBD.obtenerUsuariosPg(sender, pg, function(
							usariosPeticion, total) {
						var pgUltima = total / 5;
						if (total % 5 > 0) {
							pgUltima = pgUltima + 1;
						}
						var respuesta = swig.renderFile(
								'views/blistaPeticiones.html', {
									usuarios : usariosPeticion,
									pgActual : pg,
									pgUltima : pgUltima
								});
						res.send(respuesta);
					});
				});
			}
		});
	});

	app.get('/peticionAmistad/:idReceiver', function(req, res) {
		var criterio = {
			email : req.session.usuario
		};

		gestorBD.obtenerUsuarios(criterio, function(usuarios) {
			if (usuarios == null || usuarios.length == 0) {
				res.send("Error al enviar la peticion");
			} else {
				var peticionAmistad = {
					_idSender : gestorBD.mongo.ObjectID(usuarios[0]._id),
					_idReceiver : gestorBD.mongo
							.ObjectID(req.params.idReceiver)
				};

				gestorBD.insertarPeticionAmistad(peticionAmistad, function(id) {
					if (id == null) {
						res.send("Error al insertar la peticion");
					} else {
						res.send("Peticion enviada con exito: " + id);
					}
				});
			}
		});
	});

}