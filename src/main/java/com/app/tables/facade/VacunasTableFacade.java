package com.app.tables.facade;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.naming.NamingException;

import com.app.domain.Vacunas;
import com.app.tables.dao.VacunasTableDAO;
import com.app.utils.tables.facade.DatatablesFacadeImpl;


public class VacunasTableFacade extends
        DatatablesFacadeImpl<Vacunas> {

    @Inject
    VacunasTableDAO vacunasServiciosDao;

    @PostConstruct
    public void init() {
        if (this.dao == null) {
            this.dao = vacunasServiciosDao;
        }
    }
    
    public Vacunas getVacuna(Long idVacuna) throws SQLException, NamingException {
        return vacunasServiciosDao.getVacuna(idVacuna);
    }
    
    public List<Map<String, Object>> getVacunas(Long idHijo) {
        return vacunasServiciosDao.getVacunas(idHijo);
    }
    
    public Vacunas crearVacuna(Vacunas vacunas) throws SQLException, NamingException {
    	return vacunasServiciosDao.crearVacuna(vacunas);
        
    }
    
    public String modificarVacuna(Vacunas vacunas) throws SQLException, NamingException {
    	return vacunasServiciosDao.modificarVacuna(vacunas);
        
    }
    public String eliminarVacuna(Long id) throws SQLException, NamingException {
    	return vacunasServiciosDao.eliminarVacuna(id);
        
    }

}
