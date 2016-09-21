package org.oreto.spikeface.models

import org.apache.deltaspike.data.api.EntityRepository


interface Repo<E extends BaseEntity> {
    EntityRepository entityRepository = null
    E save(E entity)
    List<E> list()
    void delete(E entity)
    E get(Serializable id)
}

abstract class RepoImpl<E extends BaseEntity> implements Repo<E> {
    @Override E save(E entity) {
        entityRepository.save(entity)
    }

    @Override List<E> list() {
        entityRepository.findAll()
    }

    @Override void delete(E entity) {
        entityRepository.attachAndRemove(entity)
    }

    @Override E get(Serializable id) {
        entityRepository.findBy(id)
    }
}