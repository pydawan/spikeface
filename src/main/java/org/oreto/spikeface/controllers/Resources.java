package org.oreto.spikeface.controllers;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

public class Resources {

    @Produces @RequestScoped
    public FacesContext produceFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Produces @Default @RequestScoped
    public EntityManager createEntityManager() {
        return this.entityManagerFactory.createEntityManager();
    }

    public void disposeEntityManager(@Disposes @Default EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
