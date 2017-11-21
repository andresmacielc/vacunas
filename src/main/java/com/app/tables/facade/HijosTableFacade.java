package com.app.tables.facade;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.naming.NamingException;

import com.app.domain.Hijos;
import com.app.domain.Usuarios;
import com.app.tables.dao.HijosTableDAO;
import com.app.utils.tables.facade.DatatablesFacadeImpl;


public class HijosTableFacade extends
        DatatablesFacadeImpl<Usuarios> {

    @Inject
    HijosTableDAO hijosServiciosDao;

    @PostConstruct
    public void init() {
        if (this.dao == null) {
            this.dao = hijosServiciosDao;
        }
    }
    
    public Hijos getHijo(Long idHijo) throws SQLException, NamingException {
        return hijosServiciosDao.getHijo(idHijo);
    }
    
    public List<Map<String, Object>> getHijos(Long idPadre) {
        return hijosServiciosDao.getHijos(idPadre);
    }  
    
    public List<Map<String, Object>> enviarNotificaciones(Long idPadre) {
        return hijosServiciosDao.enviarNotificaciones(idPadre);
    }
    
    public Hijos crearHijo(Hijos hijos) throws SQLException, NamingException {
    	return hijosServiciosDao.crearHijo(hijos);
        
    }
    
    public String modificarHijo(Hijos hijo) throws SQLException, NamingException {
    	return hijosServiciosDao.modificarHijo(hijo);
        
    }
    public String eliminarHijo(Long id) throws SQLException, NamingException {
    	return hijosServiciosDao.eliminarHijo(id);
        
    }

}
