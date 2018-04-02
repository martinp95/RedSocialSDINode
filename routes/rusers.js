module.exports = function(app, swig, gestorBD) {

	app.get("/registrarse", function(req, res) {
		var respuesta = swig.renderFile('views/bregistro.html', {});
		res.send(respuesta);
	});

	app.post('/usuario', function(req, res) {
		//Comprobar que las contraseñas coinciden
		if (req.body.password == req.body.repetedPassword) {
			var seguro = app.get("crypto").createHmac('sha256',
					app.get('clave')).update(req.body.password).digest('hex');
			var usuario = {
				email : req.body.email,
				password : seguro
			}
			gestorBD.insertarUsuario(usuario, function(id) {
				if (id == null) {
					res.send("Error al insertar ");
				} else {
					res.send('Usuario Insertado ' + id);
				}
			});
		}else{
			res.send("Error, las constraseñas no coinciden.");
		}
	});

};