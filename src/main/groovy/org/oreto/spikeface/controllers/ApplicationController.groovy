package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.core.api.config.view.metadata.ViewConfigResolver
import org.apache.deltaspike.core.api.config.view.navigation.NavigationParameterContext
import org.apache.deltaspike.core.api.config.view.navigation.ViewNavigationHandler
import org.oreto.spikeface.models.BaseEntity
import org.oreto.spikeface.models.RepoImpl

import javax.faces.application.FacesMessage
import javax.faces.component.UIViewRoot
import javax.faces.context.FacesContext
import javax.inject.Inject

trait ApplicationController implements Serializable {
    @Inject ViewNavigationHandler viewNavigationHandler
    @Inject ViewConfigResolver viewConfigResolver
    @Inject NavigationParameterContext navigationParameterContext

    public String getViewId(Class<? extends ViewConfig> view) {
        viewConfigResolver.getViewConfigDescriptor(view).viewId
    }

    public void render(Class<? extends ViewConfig> view) {
        FacesContext facesContext = FacesContext.getCurrentInstance()
        UIViewRoot viewRoot = facesContext.getApplication().getViewHandler().createView(facesContext, getViewId(view))
        facesContext.setViewRoot(viewRoot)
    }

    public void navigate(Class<? extends ViewConfig> view) {
        viewNavigationHandler.navigateTo(view)
    }

    public void notFound() {
        render(Pages.Error.Notfound)
    }
}

trait Scaffolding<E extends BaseEntity, T extends Serializable> extends ApplicationController {

    abstract void setEntity(E entity)
    abstract E getEntity()
    abstract T getId()
    abstract void setId(T id)
    abstract RepoImpl<E> getRepository()
    abstract Class<? extends ViewConfig> getShowView()
    abstract Class<? extends ViewConfig> getListView()
    abstract Class<? extends ViewConfig> getEditView()

    public getIdName() { 'id' }

    public void get() {
        entity = id ? repository.get(id) : null
        if(!entity) notFound()
    }

    public List<E> list() {
        repository.list()
    }

    public Class<? extends ViewConfig> edit() {
        get()
        navigationParameterContext.addPageParameter(idName, entity.id)
        editView
    }

    public Class<? extends ViewConfig> save() {
        entity = repository.save(entity)
        navigationParameterContext.addPageParameter(idName, entity.id)
        showView
    }

    public Class<? extends ViewConfig> delete() {
        if(id) {
            repository.delete(repository.get(id))
            listView
        } else notFound()
    }

    public Class<? extends ViewConfig> cancel() {
        if(id) {
            navigationParameterContext.addPageParameter(idName, id)
            showView
        } else {
            listView
        }
    }
}

class Utils {
    public static String getHeader(String header) {
        FacesContext.currentInstance.externalContext.requestHeaderMap.get(header)
    }

    public static void addFacesMessage(String summary, String message = '', FacesMessage.Severity severity = FacesMessage.SEVERITY_ERROR) {
        FacesContext facesContext = FacesContext.currentInstance
        FacesMessage facesMessage = summary ? new FacesMessage(severity, summary, message) : new FacesMessage(summary)
        facesContext.addMessage(null, facesMessage)
    }
}