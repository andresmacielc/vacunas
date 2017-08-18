package com.app.tables.facade;

import java.sql.SQLException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.naming.NamingException;

import com.app.domain.Usuarios;
import com.app.tables.dao.UsuariosTableDAO;
import com.app.utils.tables.facade.DatatablesFacadeImpl;


public class UsuariosTableFacade extends
        DatatablesFacadeImpl<Usuarios> {

    @Inject
    UsuariosTableDAO usuariosServiciosDao;

    @PostConstruct
    public void init() {
        if (this.dao == null) {
            this.dao = usuariosServiciosDao;
        }
    }
    public Map<String, Object> getUsuarios( String idFirebase) {
        return usuariosServiciosDao.getUsuarios(idFirebase);
    }
    
    public Usuarios crearUsuarios(Usuarios user) throws SQLException, NamingException {
    	return usuariosServiciosDao.crearUsuarios(user);
        
    }
    
    public String modificarUsuario(Usuarios user) throws SQLException, NamingException {
    	return usuariosServiciosDao.modificarUsuario(user);
        
    }
    public String eliminarUsuario(Long id) throws SQLException, NamingException {
    	return usuariosServiciosDao.eliminarUsuario(id);
        
    }

}
