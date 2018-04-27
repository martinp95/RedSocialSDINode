module.exports = function(app, swig, gestorBD) {

	app.get('/peticionAmistad/listar', function(req, res) {

		var criterio = {
			email : req.session.usuario
		};
		gestorBD.obtenerUsuarios(criterio, function(usuarios) {
			if (usuarios == null || usuarios.length == 0) {
				res.send("Error al listar las peticiones de amistad");
			} else {
				var peticion = {
					_idReceiver : gestorBD.mongo.ObjectID(usuarios[0]._id)
				}

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
									pgUltima : pgUltima,
									usuarioSesion: req.session.usuario
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
				gestorBD.obtenerPeticionesAmistad(peticionAmistad, function(
						peticionesAmistad) {
					if( peticionesAmistad.length != 0){
						res.redirect("/listUsers"
								+ "?mensaje=Error, peticion ya enviada."
								+ "&tipoMensaje=alert-danger ");
					}else{
						var relacionAmistad = {
								user1 : gestorBD.mongo.ObjectID(usuarios[0]._id),
								user2 : gestorBD.mongo
										.ObjectID(req.params.idReceiver)
							};
						gestorBD.obtenerAmistad(relacionAmistad, function(amistad){
							if(amistad.length != 0){
								res.redirect("/listUsers"
										+ "?mensaje=Error, los usuarios ya son amigos."
										+ "&tipoMensaje=alert-danger ");
							}else{
								gestorBD.insertarPeticionAmistad(peticionAmistad, function(id) {
									if (id == null) {
										res.send("Error al insertar la peticion");
									} else {
										res.redirect("/listUsers"
												+ "?mensaje=Se ha enviado la petición de amistad"
												+ "&tipoMensaje=alert-success ");
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