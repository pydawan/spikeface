package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.core.api.config.view.metadata.ViewConfigResolver
import org.apache.deltaspike.core.api.config.view.navigation.NavigationParameterContext
import org.apache.deltaspike.core.api.config.view.navigation.ViewNavigationHandler
import org.apache.deltaspike.jpa.api.transaction.Transactional
import org.oreto.spikeface.models.BaseEntity
import org.oreto.spikeface.utils.Utils
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository

import javax.faces.application.FacesMessage
import javax.faces.context.FacesContext
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest

class ApplicationController implements Serializable {
    @Inject ViewNavigationHandler viewNavigationHandler
    @Inject ViewConfigResolver viewConfigResolver
    @Inject NavigationParameterContext navigationParameterContext

    public String getViewId(Class<? extends ViewConfig> view) {
        viewConfigResolver.getViewConfigDescriptor(view).viewId
    }

    public void render(Class<? extends ViewConfig> view) {
        Utils.render(getViewId(view))
    }

    public void navigate(Class<? extends ViewConfig> view) {
        viewNavigationHandler.navigateTo(view)
    }

    public void notFound() {
        render(Views.Error.Notfound)
    }

    public String getRequestUrl() {
        HttpServletRequest req = FacesContext.currentInstance.externalContext.request as HttpServletRequest
        req.servletPath
    }

    public boolean hasFacesError() {
        for(FacesMessage message : FacesContext.currentInstance.messageList) {
            if(message.severity == FacesMessage.SEVERITY_ERROR) return true
        }
        false
    }
}

abstract class Scaffolding<T extends BaseEntity, ID extends Serializable> extends ApplicationController {

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

    public getIdName() { 'id' }

    public void get() {
        if(requestUrl == getViewId(listView)) list()
        else if(id)  {
            entity = repository.getOne(id)
            if(!entity) notFound()
        } else if(hasFacesError()) notFound()
    }

    public void list() {
        int page = page ?: DataPager.defaultPage
        int size = size ?: DataPager.defaultSize
        int first = ((page - 1) * size)
        def direction = DataHeader.ascendingOrder == dir ? Sort.Direction.ASC : Sort.Direction.DESC
        if(sort) entities = repository.findAll(new PageRequest(first, size, direction, sort))
        else entities = repository.findAll(new PageRequest(first, size))
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
        entity = repository.save(entity)
        navigationParameterContext.addPageParameter(idName, entity.id)
        showView
    }

    @Transactional
    public Class<? extends ViewConfig> delete() {
        if(entity?.isTransient()) notFound()
        else {
            repository.delete(entity.id as ID)
            listView
        }
    }

    public Class<? extends ViewConfig> cancel() {
        if(entity?.isTransient()) listView
        else {
            navigationParameterContext.addPageParameter(idName, id)
            showView
        }
    }
}

