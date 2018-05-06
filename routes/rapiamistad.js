module.exports = function (app, gestorBD) {
	app.get('/api/amigos', function (req, res) {
    	var token = req.body.token || req.query.token || req.headers['token'];
    	
    	var filtro = req.query.name;

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
            	var criterioAmistad = {};
            	if(filtro == null){
	                criterioAmistad = {
	                    user1: gestorBD.mongo.ObjectID(usuarios[0]._id)
	                };
            	}else{
            		criterioAmistad = {
            			$and :  [{
    	                    user1: gestorBD.mongo.ObjectID(usuarios[0]._id)
            			},{
            				//user2 : sacar los ids del que corresponda con ese nombre filtrado.
            				name : filtro
            				}]
    	                };
            	}
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