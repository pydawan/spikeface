package org.oreto.spikeface.models

import org.apache.deltaspike.data.api.EntityRepository
import org.primefaces.model.LazyDataModel
import org.primefaces.model.SortOrder


interface Repo<E extends BaseEntity> {
    E save(E entity)
    List<E> list()
    List<E> list(int start, int max)
    void delete(E entity)
    E get(Serializable id)
}

abstract class RepoImpl<E extends BaseEntity> extends LazyDataModel<E> implements Repo<E> {

    abstract EntityRepository getEntityRepository()

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
        List<E> result = list(first, pageSize)
//        if(sortField)
//            result = sortOrder == SortOrder.ASCENDING ? result.orderAsc(sortField) : result.orderDesc(sortField)
        this.setRowCount(entityRepository.count() as int)
        result
    }

    @Override
    public E getRowData(String rowKey) {
        get(rowKey)
    }
}