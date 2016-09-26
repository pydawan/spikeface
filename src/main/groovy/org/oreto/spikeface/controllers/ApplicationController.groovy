package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.core.api.config.view.metadata.ViewConfigResolver
import org.apache.deltaspike.core.api.config.view.navigation.NavigationParameterContext
import org.apache.deltaspike.core.api.config.view.navigation.ViewNavigationHandler
import org.omnifaces.context.OmniPartialViewContext
import org.oreto.spikeface.models.BaseEntity
import org.oreto.spikeface.models.RepoImpl
import org.primefaces.util.ComponentUtils

import javax.faces.application.FacesMessage
import javax.faces.application.ViewHandler
import javax.faces.component.UIViewRoot
import javax.faces.context.ExternalContext
import javax.faces.context.FacesContext
import javax.faces.event.PhaseId
import javax.faces.event.PreRenderViewEvent
import javax.faces.view.ViewDeclarationLanguage
import javax.inject.Inject

trait ApplicationController implements Serializable {
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
        render(Pages.Error.Notfound)
    }
}

trait Scaffolding<E extends BaseEntity, T extends Serializable> extends ApplicationController {

    abstract void setEntity(E entity)
    abstract E getEntity()
    abstract T getId()
    abstract RepoImpl<E> getRepository()
    abstract Class<? extends ViewConfig> getShowView()
    abstract Class<? extends ViewConfig> getListView()
    abstract Class<? extends ViewConfig> getSaveView()

    public getIdName() { 'id' }

    public E get() {
        entity = id ? repository.get(id) : entity
        if(entity) entity
        else {
            notFound()
            null
        }
    }

    public List<E> list() {
        repository.list()
    }

    public Class<? extends ViewConfig> edit() {
        navigationParameterContext.addPageParameter(idName, entity.id)
        saveView
    }

    public Class<? extends ViewConfig> save() {
        entity = repository.save(entity)
        navigationParameterContext.addPageParameter(idName, entity.id)
        listView
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

    static void render(String viewId) {
        FacesContext context = FacesContext.getCurrentInstance()
        if(context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            if(!context.getExternalContext().isResponseCommitted()) {
                reRender(viewId)
            }
        } else {
            ViewHandler viewHandler = context.getApplication().getViewHandler()
            UIViewRoot viewRoot = viewHandler.createView(context, viewId)
            context.setViewRoot(viewRoot)
        }
    }

    static void reRender(String viewId) {
        FacesContext context = FacesContext.getCurrentInstance()
        resetResponse()
        ViewHandler viewHandler = context.getApplication().getViewHandler()
        UIViewRoot viewRoot = viewHandler.createView(context, viewId)
        ViewDeclarationLanguage vdl = viewHandler.getViewDeclarationLanguage(context, viewId)
        vdl.buildView(context, viewRoot)
        context.getApplication().publishEvent(context, PreRenderViewEvent.class, viewRoot)
        vdl.renderView(context, viewRoot)
        context.responseComplete()
    }

    static void resetResponse() {
        FacesContext context = FacesContext.getCurrentInstance()
        //Hacks.removeViewState(context, context.getRenderKit().getResponseStateManager(), context.getViewRoot().viewId)
        context.getAttributes().values().removeAll(Collections.singleton(true))

        ExternalContext externalContext = context.getExternalContext()
        String contentType = externalContext.getResponseContentType()
        String characterEncoding = externalContext.getResponseCharacterEncoding()
        externalContext.responseReset()
        OmniPartialViewContext.getCurrentInstance(context).resetPartialResponse()
        externalContext.setResponseContentType(contentType)
        externalContext.setResponseCharacterEncoding(characterEncoding)
    }

    static String escapeXmlWithBreaks(String text) {
        ComponentUtils.escapeXml(text).replaceAll("(\r\n|\n)", "<br/>")
    }
}