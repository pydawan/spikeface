package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.jpa.api.transaction.Transactional
import org.oreto.spikeface.models.BaseEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository

trait Scaffolding<T extends BaseEntity, ID extends Serializable> extends ApplicationController {

    abstract void setEntity(T entity)

    abstract T getEntity()

    abstract void setEntities(Page<T> entities)

    abstract Page<T> getEntities()

    abstract ID getId()

    abstract JpaRepository<T, ID> getRepository()

    abstract Class<? extends ViewConfig> getShowView()

    abstract Class<? extends ViewConfig> getListView()

    abstract Class<? extends ViewConfig> getSaveView()

    // pagination
    abstract int getPage()

    abstract int getSize()

    abstract String getSort()

    abstract String getDir()

    boolean isReadOnly() { false }

    boolean isReadWrite() { !isReadOnly() }

    public getIdName() { 'id' }

    public void get() {
        if (isReadOnly() && requestUrl == getViewId(saveView)) readOnly()
        else if (id) {
            entity = repository.findOne(id)
            if (!entity) notFound()
        } else if (requestUrl == getViewId(showView) && hasFacesError()) notFound()
    }

    public Page<T> list() {
        int page = page ?: DataPager.defaultPage
        int size = size ?: DataPager.defaultSize
        def direction = DataHeader.ascendingOrder == dir ? Sort.Direction.ASC : Sort.Direction.DESC
        if (sort)
            entities = repository.findAll(new PageRequest(page - 1, size, direction, sort))
        else
            entities = repository.findAll(new PageRequest(page - 1, size))
    }

    public int getCount() {
        repository.count()
    }

    public Class<? extends ViewConfig> edit() {
        navigationParameterContext.addPageParameter(idName, entity.id)
        saveView
    }

    @Transactional
    public Class<? extends ViewConfig> save() {
        if (isReadOnly()) Views.Error.Readonly
        else {
            entity = repository.save(entity)
            navigationParameterContext.addPageParameter(idName, entity.id)
            showView
        }
    }

    @Transactional
    public Class<? extends ViewConfig> delete() {
        if (entity?.isTransient() || isReadOnly()) notFound()
        else if (isReadOnly()) readOnly()
        else {
            repository.delete(entity.id as ID)
            listView
        }
    }

    public Class<? extends ViewConfig> cancel() {
        if (entity?.isTransient()) listView
        else {
            navigationParameterContext.addPageParameter(idName, id)
            showView
        }
    }
}
