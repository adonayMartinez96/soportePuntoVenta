INSERT INTO ventasdiarias.venta_detalle_plus
( idh, id_plu, cantidad, precio, 
descuento, id_umedida, horatransaccion, propina, minpreparacion, 
minentrega, descripcion, tax1, tax2, tax3,
tax4, tax5, tax6, tax7, tax8,
tax9, tax10, hold, id_enca, idmarcado,
terminal, modificador, idmodificador, idtypemodificador,iduser,
secuencia, borrado, id_user_borro, id_user_autorizo, id_centro_costo, 
peso, untaxable, descripcion2, identificador,precioinicial,
erp, monitor, comision, idhoteldetaservicios, web)
VALUES( 0, '100', 1.0, 0.000000,-- el 100 es el id de mensaje
0.0000, '1', ?, 0.000000, '0',-- hora recuperada de la trasaccion
'0', ?, 0.000000, 0.000000, 0.000000, -- mensaje
0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 
0.000000, 0.000000, 0, ?, ?, '1', -- id recuperado,sequencia( el mensaje siempre sera el ultimo)
1, 0, 0, '3', ?,-- sequencia basica
0, 0, 0, 0, 0.0,
0, '', 'G', 0.0000, 0,
0, 0.0, 0, 0);