package com.app.tables.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import com.app.domain.Usuarios;
import com.app.utils.tables.dao.GenericDaoImpl;

public class UsuariosTableDAO extends GenericDaoImpl<Usuarios> {

	@PersistenceUnit(unitName = "app")
    private EntityManagerFactory entityManagerFactory;
	
    public Map<String, Object> getUsuarios(String idFirebase) {
      StringBuilder countQuery = new StringBuilder();
      countQuery.append("select id_usuario, id_firebase, nombres, apellidos, documento,");
      countQuery.append(" fecha_nacimiento, sexo, estado_civil, correo_electronico, fecha_creacion, fecha_modificacion");
      countQuery.append(" from public.usuarios");
      countQuery.append(" where id_firebase ='"+idFirebase+"'");
      List<Object[]> oResultList = em.createNativeQuery(countQuery.toString()).
              getResultList();
      if(oResultList.size()<=0){
    	  return null;
      }
      Map<String, Object> resultlist = new HashMap<>();
      for (Object[] oResultArray : oResultList) {
          Map<String, Object> oMapResult = new HashMap<>();
          oMapResult.put("idUsuario", oResultArray[0]);
          oMapResult.put("idFirebase", oResultArray[1]);
          oMapResult.put("nombres", oResultArray[2]);
          oMapResult.put("apellidos", oResultArray[3]);
          oMapResult.put("documento", oResultArray[4]);
          oMapResult.put("fechaNacimiento", oResultArray[5]);
          oMapResult.put("sexo", oResultArray[6]);
          oMapResult.put("estadoCivil", oResultArray[7]);
          oMapResult.put("correoElectronico", oResultArray[8]);
          oMapResult.put("fechaCreacion", oResultArray[9]);
          oMapResult.put("fechaModificacion", oResultArray[10]);
          resultlist= oMapResult;
      }
      return resultlist;
    }
    
	public Usuarios crearUsuarios(Usuarios user) throws SQLException, NamingException {
		em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		user.setFechaCreacion(new Date());
		em.persist(user);
		em.getTransaction().commit();
		em.close();
		return user;
	}
    
	public String modificarUsuario(Usuarios user) throws SQLException, NamingException {
		em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		em.find(Usuarios.class, user.getIdUsuario());
		em.merge(user);
		em.getTransaction().commit();
		em.close();
		return "OK";
	}
	
	public String eliminarUsuario(Long id) {
		em = entityManagerFactory.createEntityManager();
	    Query q = em.createQuery("DELETE FROM Usuarios WHERE  idUsuario = "
	            + id);
	    try{
	    	em.getTransaction().begin();
	    	q.executeUpdate();
	    	em.getTransaction().commit();
	    	em.close();
	    	return "OK";
	    }catch (PersistenceException pe){
	        pe.printStackTrace();
	        return "ERROR";
	    }
	}
}
