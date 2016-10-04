package org.oreto.spikeface.services

import javax.enterprise.context.RequestScoped
import javax.enterprise.inject.Produces
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

public class EntityManagerProducer implements Serializable {

    @PersistenceContext(name = "sdpu")
    EntityManager entityManager

    @Produces
    @RequestScoped
    public EntityManager createEntityManager() {
        entityManager
    }
}
