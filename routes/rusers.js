module.exports = function(app, swig, gestorBD) {

	app.get("/registrarse", function(req, res) {
		var respuesta = swig.renderFile('views/bregistro.html', {});
		res.send(respuesta);
	});

	app.post("/registrarse", function(req, res) {
		// Comprobar que las contraseñas coinciden
		if (req.body.password == req.body.repetedPassword) {

			var criterio = {
				email : req.body.email
			}
			gestorBD.obtenerUsuarios(criterio, function(usuarios) {
				if (usuarios == null || usuarios.length == 0) {

					var seguro = app.get("crypto").createHmac('sha256',
							app.get('clave')).update(req.body.password).digest(
							'hex');

					var usuario = {
						email : req.body.email,
						password : seguro,
						name : req.body.name
					}
					gestorBD.insertarUsuario(usuario, function(id) {
						if (id == null) {
							res.redirect("/registrarse"
									+ "?mensaje=Error al crear el usuario."
									+ "&tipoMensaje=alert-danger ");
						} else {
							// meterlo en sesion se va a llevar a la vista de
							// todos los usuarios de la aplicacion.
							res.send('Usuario Insertado ' + id);
						}
					});
				} else {
					res.redirect("/registrarse"
							+ "?mensaje=El usuario ya existe."
							+ "&tipoMensaje=alert-danger ");
				}
			});
		} else {
			res.redirect("/registrarse"
					+ "?mensaje=Las constraseñas no coinciden."
					+ "&tipoMensaje=alert-danger ");
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
				res.redirect("/identificarse"
						+ "?mensaje=Email o password incorrecto"
						+ "&tipoMensaje=alert-danger ");
			} else {
				req.session.usuario = usuarios[0].email;
				// res.send("identificado");
				res.redirect("/listUsers");
			}
		});
	});

	app.get('/desconectarse', function(req, res) {
		req.session.usuario = null;
		res.redirect("/identificarse");
	});

	app.get('/listUsers', function(req, res) {
		var criterio = {};

		if (req.query.busqueda != null) {
			criterio = {
				$and : [ {
					"email" : {
						$ne : req.session.usuario
					}
				}, {
					$or : [ {
						"email" : {
							$regex : ".*" + req.query.busqueda + ".*"
						}
					}, {
						"name" : {
							$regex : ".*" + req.query.busqueda + ".*"
						}
					} ]
				} ]
			}
		} else {
			criterio = {
				email : {
					$ne : req.session.usuario
				}
			}
		}

		var pg = parseInt(req.query.pg);
		if (req.query.pg == null) {
			pg = 1;
		}

		gestorBD.obtenerUsuariosPg(criterio, pg, function(usuarios, total) {
			if (usuarios == null) {
				// en el futuro borrar ya que puede haber solo un usuario y la
				// lista deberia de estar vacia
				res.send("Error al insertar");
			} else {

				var pgUltima = total / 5;
				if (total % 5 > 0) {
					pgUltima = pgUltima + 1;
				}

				var respuesta = swig.renderFile('views/blistaUsers.html', {
					usuarios : usuarios,
					pgActual : pg,
					pgUltima : pgUltima
				});
				res.send(respuesta);
			}
		});
	});

};