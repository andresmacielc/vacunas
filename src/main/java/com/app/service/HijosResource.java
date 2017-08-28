package com.app.service;

import java.net.URISyntaxException;
import java.sql.SQLException;

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

import com.app.domain.Hijos;
import com.app.tables.facade.HijosTableFacade;

@Named
@RequestScoped
@Path("/rest/hijos")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces(MediaType.APPLICATION_JSON)
public class HijosResource {

    @Inject
    HijosTableFacade facade;

    @GET
    @Path("/obtener")
    public Response getHijos(@QueryParam("idHijo")  Long idHijo) throws Exception {
    	if(idHijo== null){
    		throw new Exception("Se requiere idHijo");
    	}
        return Response.ok(facade.getHijo(idHijo)).build();
    }

    @GET
    @Path("/obtener-por-padre")
    public Response gethijosPorPadre(@QueryParam("idPadre")  Long idPadre) throws Exception {
    	if(idPadre== null){
    		throw new Exception("Se requiere idHijo");
    	}
        return Response.ok(facade.getHijos(idPadre)).build();
    }
    
    @POST
    @Path("/agregar")
    @Transactional
    public Response crearUsuarios(Hijos hijos)
            throws URISyntaxException, SQLException, NamingException {
        	Hijos hijo = facade.crearHijo(hijos);
            return Response.ok(hijo).build();
    }
    
    @POST
    @Path("/modificar")
    @Transactional
    public Response modificarUsuario(Hijos hijos)
            throws URISyntaxException, SQLException, NamingException {
        facade.modificarHijo(hijos);
        return Response.ok(hijos).build();
    }
    
    @POST
    @Path("/eliminar")
    public Response eliminarUsuario(@QueryParam("idHijo")  Long idHijo) throws SQLException, NamingException{
    	facade.eliminarHijo(idHijo);	
    	return  Response.ok().build();
    }

}
