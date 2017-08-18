package com.app.utils;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
public class Resources {
	// use @SuppressWarnings to tell IDE to ignore warnings about field not being referenced directly

	@Produces
	@PersistenceContext
	private EntityManager em;

	  // @Produces
	  // public Logger produceLog(InjectionPoint injectionPoint) {
	   //   return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
	  // }

}
