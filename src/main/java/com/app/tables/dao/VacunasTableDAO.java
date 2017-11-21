package com.app.tables.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import com.app.domain.Vacunas;
import com.app.utils.tables.dao.GenericDaoImpl;

public class VacunasTableDAO extends GenericDaoImpl<Vacunas> {

	@PersistenceUnit(unitName = "app")
    private EntityManagerFactory entityManagerFactory;

    
	public Vacunas getVacuna(Long idVacuna) throws SQLException, NamingException {
		em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		Vacunas vacuna = new Vacunas();
		vacuna = em.find(Vacunas.class, idVacuna);		
		em.close();
		return vacuna;
	}
	
    public List<Map<String, Object>> getVacunas(Long idHijo) {
      StringBuilder countQuery = new StringBuilder();
      countQuery.append("select * ");
      countQuery.append(" from public.vacunas");
      countQuery.append(" where id_hijo ='"+idHijo.toString()+"'");
      List<Object[]> oResultList = em.createNativeQuery(countQuery.toString()).
              getResultList();
      if(oResultList.size()<=0){
    	  return null;
      }
      List<Map<String, Object>>  resultlist = new ArrayList<>();
      for (Object[] oResultArray : oResultList) {
          Map<String, Object> oMapResult = new HashMap<>();
          oMapResult.put("idVacuna", oResultArray[0]);
          oMapResult.put("idHijo", oResultArray[1]);
          oMapResult.put("nombreVacuna", oResultArray[2]);
          //System.out.println(oResultArray[3].toString());
          Timestamp timestamp = (Timestamp) oResultArray[3];
          Date fecha = null;
          String prueba = null;
          if(timestamp != null){
        	  fecha = new Date( timestamp.getTime());
              DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
              prueba = df.format(fecha);
          }
          oMapResult.put("fechaAplicacion",prueba );
          oMapResult.put("aplicada", oResultArray[4]);
          resultlist.add(oMapResult);
      }
      return resultlist;
    }
    
	public Vacunas crearVacuna(Vacunas vacunas) throws SQLException, NamingException {
		em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(vacunas);
		em.getTransaction().commit();
		em.close();
		return vacunas;
	}
    
	public String modificarVacuna(Vacunas vacunas) throws SQLException, NamingException {
		em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.find(Vacunas.class, vacunas.getIdVacuna());
		em.merge(vacunas);
		em.getTransaction().commit();
		em.close();
		return "OK";
	}
	
	public String eliminarVacuna(Long id) {
	    try{
			em = entityManagerFactory.createEntityManager();
	    	em.getTransaction().begin();
			Vacunas vacuna = new Vacunas();
			vacuna = em.find(Vacunas.class, id);
			em.remove(vacuna);
	    	em.getTransaction().commit();
	    	em.close();
	    	return "OK";
	    }catch (PersistenceException pe){
	        pe.printStackTrace();
	        return "ERROR";
	    }
	}
}
