package com.app.tables.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import com.app.domain.Hijos;
import com.app.domain.Usuarios;
import com.app.utils.tables.dao.GenericDaoImpl;

public class HijosTableDAO extends GenericDaoImpl<Usuarios> {

	@PersistenceUnit(unitName = "app")
    private EntityManagerFactory entityManagerFactory;

    
	public Hijos getHijo(Long idHijo) throws SQLException, NamingException {
		em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		Hijos hijo = new Hijos();
		hijo = em.find(Hijos.class, idHijo);		
		em.close();
		return hijo;
	}
	
    public List<Map<String, Object>> getHijos(Long idPadre) {
      StringBuilder countQuery = new StringBuilder();
      countQuery.append("select * ");
      countQuery.append(" from public.hijos");
      countQuery.append(" where id_padre ='"+idPadre.toString()+"'");
      List<Object[]> oResultList = em.createNativeQuery(countQuery.toString()).
              getResultList();
      if(oResultList.size()<=0){
    	  return null;
      }
      List<Map<String, Object>> resultlist = new ArrayList<>();
      for (Object[] oResultArray : oResultList) {
          Map<String, Object> oMapResult = new HashMap<>();
          oMapResult.put("idHijo", oResultArray[0]);
          oMapResult.put("IdPadre", oResultArray[1]);
          oMapResult.put("nombres", oResultArray[2]);
          oMapResult.put("apellidos", oResultArray[3]);
          oMapResult.put("edad", oResultArray[4]);
          oMapResult.put("documento", oResultArray[5]);
          oMapResult.put("fechaNacimiento", oResultArray[6]);
          oMapResult.put("sexo", oResultArray[7]);
          resultlist.add(oMapResult);
      }
      return resultlist;
    }
    
    public List<Map<String, Object>> enviarNotificaciones(Long idPadre) {
        StringBuilder countQuery = new StringBuilder();
        countQuery.append(" select DISTINCT h.* from public.usuarios u ");
		countQuery.append(" JOIN public.hijos h on u.id_usuario = h.id_padre ");
		countQuery.append(" JOIN public.vacunas v on h.id_hijo = v.id_hijo	 ");	
		countQuery.append(" WHERE v.fecha_aplicacion <= now() + interval '2 day'  ");
		countQuery.append(" and v.notificado = false ");
		countQuery.append(" and u.id_usuario = "+idPadre.toString());
		
        List<Object[]> oResultList = em.createNativeQuery(countQuery.toString()).
                getResultList();
        if(oResultList.size()<=0){
      	  return null;
        }
        List<Map<String, Object>> resultlist = new ArrayList<>();
        for (Object[] oResultArray : oResultList) {
            Map<String, Object> oMapResult = new HashMap<>();
            oMapResult.put("idHijo", oResultArray[0]);
            oMapResult.put("IdPadre", oResultArray[1]);
            oMapResult.put("nombres", oResultArray[2]);
            oMapResult.put("apellidos", oResultArray[3]);
            oMapResult.put("edad", oResultArray[4]);
            oMapResult.put("documento", oResultArray[5]);
            oMapResult.put("fechaNacimiento", oResultArray[6]);
            oMapResult.put("sexo", oResultArray[7]);
            resultlist.add(oMapResult);
        }
        actualizarNotificaciones(idPadre);
        return resultlist;
      }
    
	private void actualizarNotificaciones(Long idPadre) {

	    try{        
	    	String countQuery = " UPDATE vacunas SET notificado = true where id_hijo in ( select DISTINCT h.id_hijo from public.usuarios u "
				+" JOIN public.hijos h on u.id_usuario = h.id_padre "
				+" JOIN public.vacunas v on h.id_hijo = v.id_hijo	 "
				+" WHERE v.fecha_aplicacion <= now() + interval '2 day'  "
				+" and v.notificado = false "
				+" and u.id_usuario = "+idPadre.toString() +" ) ";
	    	em.getTransaction().begin();
	    	Query q = em.createNativeQuery(countQuery);
		    q.executeUpdate();
	    	em.getTransaction().commit();
	    }catch (PersistenceException pe){
	        pe.printStackTrace();
	    }

		
	}

	public Hijos crearHijo(Hijos hijos) throws SQLException, NamingException {
		em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(hijos);
		em.getTransaction().commit();
		em.close();
		return hijos;
	}
    
	public String modificarHijo(Hijos hijos) throws SQLException, NamingException {
		em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.find(Hijos.class, hijos.getIdHijo());
		em.merge(hijos);
		em.getTransaction().commit();
		em.close();
		return "OK";
	}
	
	public String eliminarHijo(Long id) {
	    try{
			em = entityManagerFactory.createEntityManager();
	    	em.getTransaction().begin();
	    	Hijos hijos = new Hijos();
	    	hijos = em.find(Hijos.class, id);
			em.remove(hijos);
	    	em.getTransaction().commit();
	    	em.close();
	        return "OK";
	    }catch (PersistenceException pe){
	        pe.printStackTrace();
	        return "ERROR";
	    }
	}
}
