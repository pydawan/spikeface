package org.oreto.spikeface.services

import javax.enterprise.context.Dependent
import javax.enterprise.context.RequestScoped
import javax.enterprise.inject.Disposes
import javax.enterprise.inject.Produces
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Dependent
public class EntityManagerProducer implements Serializable {

    @PersistenceContext(name = "sdpu")
    EntityManager entityManager

    @Produces
    @RequestScoped
    public EntityManager createEntityManager() {
        entityManager
    }

    public void close( @Disposes EntityManager em ) {
        if( em.isOpen() ) { em.close() }
    }
}
