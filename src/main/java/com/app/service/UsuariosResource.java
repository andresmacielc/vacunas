package com.app.service;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.app.domain.Usuarios;
import com.app.tables.facade.UsuariosTableFacade;
import com.app.utils.CustomResponse;

@Named
@RequestScoped
@Path("/rest/usuarios")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces(MediaType.APPLICATION_JSON)
public class UsuariosResource {

    @Inject
    UsuariosTableFacade facade;

    @GET
    @Path("/obtener")
    public Response getUsuarios(@QueryParam("idUsuario")  String idFirebase) throws Exception {
    	if(idFirebase== null || idFirebase.equals("")){
    		throw new Exception("Se requiere idFirebase");
    	}
        return Response.ok(facade.getUsuarios(idFirebase)).build();
    }    
    @POST
    @Path("/verificar-usuario")
    public Response verificarUsuario(@QueryParam("correo")  String correo) throws Exception {
    	if(correo== null || correo.equals("")){
    		throw new Exception("Se requiere correo");
    	}
    	CustomResponse respuesta = new CustomResponse();
    	respuesta.setRespuesta(facade.verificarUsuario(correo));
        return Response.ok(respuesta).build();
    }
    
    @POST
    @Path("/agregar")
    @Transactional
    public Response crearUsuarios(Usuarios usuarios)
            throws URISyntaxException, SQLException, NamingException {
    	Map<String, Object> usuario = null;
    	usuario = facade.getUsuarios(usuarios.getIdFirebase());
    	if(usuario != null){
            return Response.ok(usuario).build();
    	}else{
        	Usuarios user = facade.crearUsuarios(usuarios);
            return Response.ok(user).build();
    	}
    }
    
    @POST
    @Path("/modificar")
    @Transactional
    public Response modificarUsuario(Usuarios usuarios)
            throws URISyntaxException, SQLException, NamingException {
    	usuarios.setFechaModificacion(new Date());
        facade.modificarUsuario(usuarios);
        return Response.ok(usuarios).build();
    }
    
    @POST
    @Path("/eliminar")
    public Response eliminarUsuario(@QueryParam("idUsuario")  Long idUsuario) throws SQLException, NamingException{
    	facade.eliminarUsuario(idUsuario);	
    	return  Response.ok().build();
    }

}
