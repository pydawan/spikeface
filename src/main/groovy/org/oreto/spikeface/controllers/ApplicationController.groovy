package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.core.api.config.view.metadata.ViewConfigResolver
import org.apache.deltaspike.core.api.config.view.navigation.NavigationParameterContext
import org.apache.deltaspike.core.api.config.view.navigation.ViewNavigationHandler

import javax.faces.application.FacesMessage
import javax.faces.component.UIViewRoot
import javax.faces.context.FacesContext
import javax.inject.Inject

trait ApplicationController {
    @Inject ViewNavigationHandler viewNavigationHandler
    @Inject NavigationParameterContext navigationParameterContext
    @Inject private ViewConfigResolver viewConfigResolver;

    def id

    public String getViewId(Class<? extends ViewConfig> view) {
        viewConfigResolver.getViewConfigDescriptor(view).viewId
    }

    public void render(Class<? extends ViewConfig> view) {
        FacesContext facesContext = FacesContext.getCurrentInstance()
        UIViewRoot viewRoot = facesContext.getApplication().getViewHandler().createView(facesContext, getViewId(view))
        facesContext.setViewRoot(viewRoot)
    }

    public void notFound() {
        render(Pages.Error.PageNotFound)
    }

    public static void addFacesMessage(String message, String summary = '', FacesMessage.Severity severity = FacesMessage.SEVERITY_ERROR) {
        FacesContext facesContext = FacesContext.currentInstance
        FacesMessage facesMessage = summary ? new FacesMessage(severity, summary, message) : new FacesMessage(message)
        facesContext.addMessage(null, facesMessage)
    }

    public static void addInfoMessage(String message) {
        addFacesMessage(message)
    }
}
