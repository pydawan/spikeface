package org.oreto.spikeface.models

import org.oreto.spikeface.controllers.DataHeader
import org.primefaces.model.LazyDataModel
import org.primefaces.model.SortOrder
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.PagingAndSortingRepository


interface Repo<E extends BaseEntity> {
    E save(E entity)
    Iterable<E> list()
    Iterable<E> list(int start, int max)
    Iterable<E> list(int start, int max, String sort, String dir)
    int count()
    void delete(Serializable id)
    E get(Serializable id)
}

abstract class RepoImpl<E extends BaseEntity> extends LazyDataModel<E> implements Repo<E> {

    abstract PagingAndSortingRepository getEntityRepository()

    @Override E save(E entity) {
        entityRepository.save(entity)
    }

    @Override Iterable<E> list() {
        entityRepository.findAll()
    }

    @Override Iterable<E> list(int start, int max) {
        entityRepository.findAll(new PageRequest(start, max)).content
    }

    @Override Iterable<E> list(int start, int max, String sort, String dir) {
        def direction = DataHeader.ascendingOrder == dir ? Sort.Direction.ASC : Sort.Direction.DESC
        def page = new PageRequest(start, max, direction, sort)
        entityRepository.findAll(page)
    }

    @Override int count() {
        entityRepository.count()
    }

    @Override void delete(Serializable id) {
        entityRepository.delete(id)
    }

    @Override E get(Serializable id) {
        entityRepository.findOne(id)
    }

    @Override public List<E> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {
        this.setRowCount(entityRepository.count() as int)
        list(first, pageSize, sortField, sortOrder == SortOrder.ASCENDING ? DataHeader.ascendingOrder : DataHeader.defaultDirection).toList()
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
        sortOrder == DataHeader.ascendingOrder ? value : -1 * value
    }
}