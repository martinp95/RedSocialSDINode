module.exports = function(app, swig, gestorBD) {
	
	app.get("/usuarios", function(req, res) {
		res.send("ver usuarios");
	});//Esta funcion no me vale para nada
};