module.exports = function(app, swig, gestorBD) {

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