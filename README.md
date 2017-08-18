Vacunas-BackEnd
============
#Trabajo práctico IS2 - Marcos Daniel Peralta Núñez, 4.776.313

# Jboss AS 7.1.1 Final: <br>
-Descargar el servidor del siguiente link: http://download.jboss.org/jbossas/7.1/jboss-as-7.1.1.Final/jboss-as-7.1.1.Final.zip


# JDBC Postgres 9.3-1103 : <br>
-Descargar el JDBC del siguiente link: https://jdbc.postgresql.org/download/postgresql-9.3-1103.jdbc41.jar


# Agregar module.xml:
1. Descomprimir el servidor
2. En el directorio del servidor ir a: modules/org/postgresql/main (si no existe el fichero, crearlo)
3. Copiar el JDBC en el fichero anterior.
4. Crear el archivo module.xml
5. Copiar los siguiente en el module.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>
	<module xmlns="urn:jboss:module:1.0" name="org.postgresql">
	 <resources>
	 <resource-root path="postgresql-9.3-1103.jdbc41.jar"/>
	 </resources>
	 <dependencies>
	 <module name="javax.api"/>
	 <module name="javax.transaction.api"/>
	 </dependencies>
	</module>
```


# JAVA:
-http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase7-521261.html




#SERVICIOS
#### USUARIOS

* OBTENER DATOS DE UN USUARIO

```
GET
https://localhost:8080/vacunas/rest/usuarios/obtener
-> Params
QueryParam: idUsuario

RETURN:
{
    "idFirebase": "2",
    "correoElectronico": "prueba",
    "nombres": "prueba",
    "idUsuario": 24,
    "estadoCivil": "S",
    "apellidos": "prueba",
    "sexo": "M",
    "documento": "prueba",
    "fechaCreacion": "2017-06-19T16:03:37.736+0000",
    "fechaModificacion": null,
    "fechaNacimiento": "1994-06-24"
}

```

* CREAR USUARIOS

```
POST
https://localhost:8080/vacunas/rest/usuarios/agregar
-> Param
OBJECT
	{
		"idFirebase": "prueba",
		"nombres": "prueba",
		"apellidos": "prueba",
		"documento": "prueba",
		"estadoCivil": "S",
		"correoElectronico": "prueba",
		"sexo": "M",
		"fechaNacimiento": "1994-06-25"
	}

RETURN: USUARIO CREADO

```

* MODIFICAR USUARIOS

```
POST
https://localhost:8080/vacunas/rest/usuarios/modificar
-> Param
OBJECT
	{
		"idUsuario": 3,
		"idFirebase": "3",
		"nombres": "Jose Luis",
		"apellidos": "Perez",
		"documento": "4896752",
		"fechaNacimiento": "1994-04-15T00:00:00.000+0000",
		"sexo": "M",
		"estadoCivil": "S",
		"correoElectronico": "jose_perez@gmail.com",
		"fechaCreacion": "2017-08-18T22:22:38.229+0000"
	}
RETURN: USUARIO MODIFICADO

```

* ELIMINAR USUARIOS

```
POST
https://localhost:8080/vacunas/rest/usuarios/eliminar
-> Params
queryParam: Long idUsuario
Example: https://localhost:8080/vacunas/rest/usuarios/eliminar?idUsuario=1

```

