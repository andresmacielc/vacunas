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

import com.app.domain.Vacunas;
import com.app.tables.facade.VacunasTableFacade;

@Named
@RequestScoped
@Path("/rest/vacunas")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces(MediaType.APPLICATION_JSON)
public class VacunasResource {

    @Inject
    VacunasTableFacade facade;

    @GET
    @Path("/obtener")
    public Response getVacuna(@QueryParam("idVacuna")  Long idVacuna) throws Exception {
    	if(idVacuna== null){
    		throw new Exception("Se requiere idVacuna");
    	}
        return Response.ok(facade.getVacuna(idVacuna)).build();
    }

    @GET
    @Path("/obtener-por-hijo")
    public Response getVacunaHijo(@QueryParam("idHijo")  Long idHijo) throws Exception {
    	if(idHijo== null){
    		throw new Exception("Se requiere idHijo");
    	}
        return Response.ok(facade.getVacunas(idHijo)).build();
    }
    
    @POST
    @Path("/agregar")
    @Transactional
    public Response crearUsuarios(Vacunas vacunas)
            throws URISyntaxException, SQLException, NamingException {
        	Vacunas vacuna = facade.crearVacuna(vacunas);
            return Response.ok(vacuna).build();
    }
    
    @POST
    @Path("/modificar")
    @Transactional
    public Response modificarUsuario(Vacunas vacunas)
            throws URISyntaxException, SQLException, NamingException {
        facade.modificarVacuna(vacunas);
        return Response.ok(vacunas).build();
    }
    
    @POST
    @Path("/eliminar")
    public Response eliminarVacuna(@QueryParam("idVacuna")  Long idVacuna) throws SQLException, NamingException{
    	facade.eliminarVacuna(idVacuna);	
    	return  Response.ok().build();
    }

}
