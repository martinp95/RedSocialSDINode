module.exports = function(app, swig, gestorBD) {

	app.get("/registrarse", function(req, res) {
		var respuesta = swig.renderFile('views/bregistro.html', {});
		res.send(respuesta);
	});

	app.post('/usuario', function(req, res) {
		// Comprobar que las contraseñas coinciden
		if (req.body.password == req.body.repetedPassword) {

			// comprobar si ya esta en el sistema
			var criterio = {
				email : req.body.email
			}

			gestorBD.obtenerUsuarios(criterio, function(usuarios) {
				// El usuario no existe, lo creo.
				if (usuarios == null || usuarios.length == 0) {

					var seguro = app.get("crypto").createHmac('sha256',
							app.get('clave')).update(req.body.password).digest(
							'hex');

					var usuario = {
						email : req.body.email,
						password : seguro
					}
					gestorBD.insertarUsuario(usuario, function(id) {
						if (id == null) {
							res.send("Error al insertar ");
						} else {
							// meterlo en sesion se va a llevar a la vista de
							// todos los usuarios de la aplicacion.
							res.send('Usuario Insertado ' + id);
						}
					});
				} else {
					res.send('Error el usuario ya existe');
				}
			});
		} else {
			res.send("Error, las constraseñas no coinciden.");
		}
	});

	app.get("/identificarse", function(req, res) {
		var respuesta = swig.renderFile('views/bidentificacion.html', {});
		res.send(respuesta);
	});

	app.post("/identificarse", function(req, res) {
		var seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
				.update(req.body.password).digest('hex');
		var criterio = {
			email : req.body.email,
			password : seguro
		}
		gestorBD.obtenerUsuarios(criterio, function(usuarios) {
			if (usuarios == null || usuarios.length == 0) {
				req.session.usuario = null;
				res.send("No identificado: ");
			} else {
				req.session.usuario = usuarios[0].email;
				res.send("identificado");
				// falta redirigir a la vista que lista los usuarios de la
				// aplicacion.
			}
		});
	});

	app.get('/desconectarse', function(req, res) {
		req.session.usuario = null;
		res.send("Usuario desconectado");
	});

};