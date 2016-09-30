package org.oreto.spikeface.models

import org.apache.deltaspike.data.api.EntityRepository
import org.oreto.spikeface.controllers.DataPager
import org.primefaces.model.LazyDataModel
import org.primefaces.model.SortOrder


interface Repo<E extends BaseEntity> {
    E save(E entity)
    List<E> list()
    List<E> list(int start, int max)
    List<E> list(int start, int max, String sort, String dir)
    int count()
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

    @Override List<E> list(int start, int max, String sort, String dir = DataPager.defaultDirection) {
        def result = entityRepository.findAll(start, max)
        if(sort) Collections.sort(result, new Sorter<E>(sort, dir ?: DataPager.defaultDirection))
        result
    }

    @Override int count() {
        entityRepository.count()
    }

    @Override void delete(E entity) {
        entityRepository.attachAndRemove(entity)
    }

    @Override E get(Serializable id) {
        entityRepository.findBy(id)
    }

    @Override public List<E> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {
        this.setRowCount(entityRepository.count() as int)
        list(first, pageSize, sortField, sortOrder == SortOrder.ASCENDING ? DataPager.ascendingOrder : DataPager.defaultDirection)
    }

    @Override
    public E getRowData(String rowKey) {
        get(rowKey)
    }
}

public class Sorter<T> implements Comparator<T> {

    String sortField
    String sortOrder

    Sorter(String sortField, String sortOrder) {
        this.sortField = sortField
        this.sortOrder = sortOrder
    }

    public int compare(T o1, T o2) {
        String getter = "get${sortField.capitalize()}"
        Object value1 = o1.class.getMethod(getter).invoke(o1)
        Object value2 = o2.class.getMethod(getter).invoke(o2)

        int value = ((Comparable) value1) <=> value2
        sortOrder == DataPager.ascendingOrder ? value : -1 * value
    }
}