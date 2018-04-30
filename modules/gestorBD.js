module.exports = {
	mongo : null,
	app : null,
	init : function(app, mongo) {
		this.mongo = mongo;
		this.app = app;
	},
	eliminarPeticionAmistad : function(criterio, funcionCallback) {
		this.mongo.MongoClient.connect(this.app.get('db'), function(err, db) {
			if (err) {
				funcionCallback(null);
			} else {
				var collection = db.collection('peticionesAmistad');
				collection.remove(criterio, function(err, result) {
					if (err) {
						funcionCallback(null);
					} else {
						funcionCallback(result);
					}
					db.close();
				});
			}
		});
	},
	insertarAmistad : function(amistad, funcionCallback) {
		this.mongo.MongoClient.connect(this.app.get('db'), function(err, db) {
			if (err) {
				funcionCallback(null);
			} else {
				var collection = db.collection('amistad');
				collection.insert(amistad, function(err, result) {
					if (err) {
						funcionCallback(null);
					} else {
						funcionCallback(result.ops[0]._id);
					}
					db.close();
				});
			}
		});
	},
	insertarPeticionAmistad : function(peticionAmistad, funcionCallback) {
		this.mongo.MongoClient.connect(this.app.get('db'), function(err, db) {
			if (err) {
				funcionCallback(null);
			} else {
				var collection = db.collection('peticionesAmistad');
				collection.insert(peticionAmistad, function(err, result) {
					if (err) {
						funcionCallback(null);
					} else {
						funcionCallback(result.ops[0]._id);
					}
					db.close();
				});
			}
		});
	},
	crearMensaje : function(mensaje, funcionCallback) {
		this.mongo.MongoClient.connect(this.app.get('db'), function(err, db) {
			if (err) {
				funcionCallback(null);
			} else {
				var collection = db.collection('mensajes');
				collection.insert(mensaje, function(err, result) {
					if (err) {
						funcionCallback(null);
					} else {
						funcionCallback(result.ops[0]._id);
					}
					db.close();
				});
			}
		});
	},
	obtenerAmistad : function(criterio, funcionCallback) {
		this.mongo.MongoClient.connect(this.app.get('db'), function(err, db) {
			if (err) {
				funcionCallback(null);
			} else {
				var collection = db.collection('amistad');
				collection.find(criterio).toArray(function(err, amistad) {
					if (err) {
						funcionCallback(null);
					} else {
						funcionCallback(amistad);
					}
					db.close();
				});
			}
		});
	},
	obtenerMensajes : function(criterio, criterioContar, funcionCallback) {
		this.mongo.MongoClient.connect(this.app.get('db'), function(err, db) {
			if (err) {
				funcionCallback(null);
			} else {
				var collection = db.collection('mensajes');
				collection.count(criterioContar, function(err, count) {
					collection.find(criterio).sort({fecha : -1}).toArray(function(err, mensajes) {
						if (err) {
							funcionCallback(null);
						} else {
							funcionCallback(mensajes, count);
						}
						db.close();
					});
				});
			}

		});
	},
	obtenerPeticionesAmistad : function(peticion, funcionCallback) {
		this.mongo.MongoClient.connect(this.app.get('db'), function(err, db) {
			if (err) {
				funcionCallback(null);
			} else {
				var collection = db.collection('peticionesAmistad');
				collection.find(peticion).toArray(
						function(err, peticionesAmistad) {
							if (err) {
								funcionCallback(null);
							} else {
								funcionCallback(peticionesAmistad);
							}
							db.close();
						});
			}
		});
	},
	obtenerUsuariosPg : function(criterio, pg, funcionCallback) {
		this.mongo.MongoClient.connect(this.app.get('db'), function(err, db) {
			if (err) {
				funcionCallback(null);
			} else {
				var collection = db.collection('usuarios');
				collection.count(criterio, function(err, count) {
					collection.find(criterio).skip((pg - 1) * 5).limit(5)
							.toArray(function(err, usuarios) {
								if (err) {
									funcionCallback(null);
								} else {
									funcionCallback(usuarios, count);
								}
								db.close();
							});
				});

			}
		});
	},
	modificarMensaje : function(criterio, mensaje, funcionCallback) {
		this.mongo.MongoClient.connect(this.app.get('db'), function(err, db) {
			if (err) {
				funcionCallback(null);
			} else {
				var collection = db.collection('mensajes');
				collection.update(criterio, {
					$set : mensaje
				}, function(err, result) {
					if (err) {
						funcionCallback(null);
					} else {
						funcionCallback(result);
					}
					db.close();
				});
			}
		});
	},
	obtenerUsuarios : function(criterio, funcionCallback) {
		this.mongo.MongoClient.connect(this.app.get('db'), function(err, db) {
			if (err) {
				funcionCallback(null);
			} else {
				var collection = db.collection('usuarios');
				collection.find(criterio).toArray(function(err, usuarios) {
					if (err) {
						funcionCallback(null);
					} else {
						funcionCallback(usuarios);
					}
					db.close();
				});
			}
		});
	},
	insertarUsuario : function(usuario, funcionCallback) {
		this.mongo.MongoClient.connect(this.app.get('db'), function(err, db) {
			if (err) {
				funcionCallback(null);
			} else {
				var collection = db.collection('usuarios');
				collection.insert(usuario, function(err, result) {
					if (err) {
						funcionCallback(null);
					} else {
						funcionCallback(result.ops[0]._id);
					}
					db.close();
				});
			}
		});
	},
	borrarBaseDeDatos : function(funcionCallback) {
		this.mongo.MongoClient.connect(this.app.get('db'),function(err, db) {
			if (err) {
				funcionCallback(null);
				} else {
					db.collection('peticionesAmistad').drop(function(err, result) {
						if (err) {funcionCallback(null);
						} else {
							db.collection('amistad').drop(function(err,result) {
								if (err) {
									funcionCallback(null);
									} else {
										db.collection('mensajes').drop(function(err,result) {
											if (err) {
												funcionCallback(null);
												} else {
													funcionCallback(result);
													}db.close();
													});
										}
								});
							}
						});
					}
			});
	}
};