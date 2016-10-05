package org.oreto.spikeface.services

import javax.enterprise.context.RequestScoped
import javax.enterprise.inject.Default
import javax.enterprise.inject.Disposes
import javax.enterprise.inject.Produces
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit

public class EntityManagerProducer implements Serializable {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory

    @Produces @Default @RequestScoped
    public EntityManager create() {
        return this.entityManagerFactory.createEntityManager()
    }

    public void dispose(@Disposes @Default EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close()
        }
    }
}
