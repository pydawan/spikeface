package org.oreto.spikeface.models

import org.oreto.spikeface.controllers.DataHeader
import org.primefaces.model.LazyDataModel
import org.primefaces.model.SortOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository

interface Repo<E, ID extends Serializable> {
    E save(E entity)
    List<E> list()
    Page<E> list(int start, int max)
    Page<E> list(int start, int max, String sort, String dir)
    int count()
    void delete(E e)
    E get(ID id)
}

abstract class RepoImpl<E, ID extends Serializable> extends LazyDataModel<E> implements Repo<E, ID> {

    abstract JpaRepository<E, ID> getEntityRepository()

    @Override E save(E entity) {
        entityRepository.save(entity)
    }

    @Override List<E> list() {
        entityRepository.findAll()
    }

    @Override Page<E> list(int start, int max) {
        entityRepository.findAll(new PageRequest(start, max))
    }

    @Override Page<E> list(int start, int max, String sort, String dir) {
        def direction = DataHeader.ascendingOrder == dir ? Sort.Direction.ASC : Sort.Direction.DESC
        def page = new PageRequest(start, max, direction, sort)
        entityRepository.findAll(page)
    }

    @Override int count() {
        entityRepository.count()
    }

    @Override void delete(E entity) {
        entityRepository.delete(entity)
    }

    @Override E get(ID id) {
        entityRepository.findOne(id)
    }

    @Override public List<E> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {
        this.setRowCount(entityRepository.count() as int)
        list(first, pageSize, sortField, sortOrder == SortOrder.ASCENDING ? DataHeader.ascendingOrder : DataHeader.defaultDirection).toList()
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