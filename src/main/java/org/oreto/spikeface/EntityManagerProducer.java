package org.oreto.spikeface;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.io.Serializable;

public class EntityManagerProducer implements Serializable {
    @PersistenceUnit
    private EntityManagerFactory emf;

    @Produces
    public EntityManager create() {
        return emf.createEntityManager();
    }

    public void close( @Disposes EntityManager em ) {
        if( em.isOpen() ) { em.close(); }
    }
}
