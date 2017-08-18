package com.app.persistence;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@Stateless
public class GenericDAO<T> {

	@Inject
	EntityManager em;

	public List<T> getItem(Class _class, String identifierName,
			Object identifierValue) {
		System.out.println("++++++++++++++ clazz " + _class);
		Session session = (Session) em.getDelegate();
		Criteria criteria = session.createCriteria(_class);
		criteria.add(Restrictions.eq(identifierName, identifierValue));

		return criteria.list();

	}

	/*
	 * Retorna las disponibilidades por producto y establecimiento siempre que
	 * el producto este disponible y pueda mostrarse en el mapa
	 */
	public List<Object[]> getDisponibilidadPorProducto() {

		Query query = em.createNamedQuery("findDisponibilidad");
		query.setParameter("disponible", "SI");
		//query.setParameter("mapa", true);
		
		return query.getResultList();

	}
}