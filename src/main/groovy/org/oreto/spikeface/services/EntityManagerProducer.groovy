package org.oreto.spikeface.services;

import javax.enterprise.inject.Disposes
import javax.enterprise.inject.Produces
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit

public class EntityManagerProducer implements Serializable {
    @PersistenceUnit
    private EntityManagerFactory emf

    @Produces
    public EntityManager create() {
        emf.createEntityManager()
    }

    public void close( @Disposes EntityManager em ) {
        if( em.isOpen() ) { em.close() }
    }
}
