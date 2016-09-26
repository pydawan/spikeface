package org.oreto.spikeface.models

import org.apache.deltaspike.data.api.EntityRepository
import org.apache.deltaspike.data.api.QueryResult
import org.primefaces.model.LazyDataModel
import org.primefaces.model.SortOrder


interface Repo<E extends BaseEntity> {
    E save(E entity)
    List<E> list()
    List<E> list(int start, int max)
    void delete(E entity)
    E get(Serializable id)
}

interface BaseEntityRepo<E extends BaseEntity, P extends Serializable> extends EntityRepository<E, P> {
    QueryResult<E> list()
}

abstract class RepoImpl<E extends BaseEntity> extends LazyDataModel<E> implements Repo<E> {

    abstract BaseEntityRepo getEntityRepository()

    @Override E save(E entity) {
        entityRepository.save(entity)
    }

    @Override List<E> list() {
        entityRepository.findAll()
    }

    @Override List<E> list(int start, int max) {
        entityRepository.findAll(start, max)
    }

    @Override void delete(E entity) {
        entityRepository.attachAndRemove(entity)
    }

    @Override E get(Serializable id) {
        entityRepository.findBy(id)
    }

    @Override public List<E> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {
        QueryResult<E> result = entityRepository.list().withPageSize(pageSize).firstResult(first)
        result = sortOrder == sortOrder.ASCENDING ? result.orderAsc(sortField) : result.orderDesc(sortField)
        result.getResultList()
    }
}