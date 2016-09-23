package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.metadata.ViewConfigResolver
import org.apache.deltaspike.core.api.exception.control.ExceptionHandler
import org.apache.deltaspike.core.api.exception.control.Handles
import org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent
import org.codehaus.groovy.runtime.DateGroovyMethods
import org.omnifaces.context.OmniPartialViewContext
import org.omnifaces.util.Hacks
import org.primefaces.util.ComponentUtils

import javax.enterprise.inject.Model
import javax.faces.application.FacesMessage
import javax.faces.application.ViewHandler
import javax.faces.component.UIViewRoot
import javax.faces.context.ExternalContext
import javax.faces.context.FacesContext
import javax.faces.event.PhaseId
import javax.faces.event.PreRenderViewEvent
import javax.faces.view.ViewDeclarationLanguage
import javax.inject.Inject

@ExceptionHandler
@Model
public class ViewExceptionHandler {
    @Inject private ViewConfigResolver viewConfigResolver

    Throwable exception
    String time
    String type
    String message
    String stackTrace

    public void onException(@Handles ExceptionEvent<Exception> event)
    {
        this.time = DateGroovyMethods.format(new Date(), "MM-dd-yyyy hh:mm:ss a")
        exception = event.exception
        this.type = exception.class.typeName
        this.message = exception.message

        StringWriter sw = new StringWriter()
        PrintWriter pw = new PrintWriter(sw)
        exception.printStackTrace(pw)
        this.stackTrace = ComponentUtils.escapeXml(sw.toString()).replaceAll("(\r\n|\n)", "<br/>")

        FacesContext context = FacesContext.getCurrentInstance()
        String viewId = viewConfigResolver.getViewConfigDescriptor(Pages.Error.Server).viewId
        ViewHandler viewHandler = context.getApplication().getViewHandler()
        UIViewRoot viewRoot = viewHandler.createView(context, viewId)
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, this.message, exception.toString())

        if(context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            resetResponse(context)
            context.addMessage(null, facesMessage)
            ViewDeclarationLanguage vdl = viewHandler.getViewDeclarationLanguage(context, viewId)
            vdl.buildView(context, viewRoot)
            context.getApplication().publishEvent(context, PreRenderViewEvent.class, viewRoot)
            vdl.renderView(context, viewRoot)
            context.responseComplete()
            event.handled()
        } else {
            context.addMessage(null, facesMessage)
            context.setViewRoot(viewRoot)
        }
    }

    static void resetResponse(FacesContext context) {
        Hacks.removeViewState(context, context.getRenderKit().getResponseStateManager(), context.getViewRoot().viewId)
        context.getAttributes().values().removeAll(Collections.singleton(true))
        ExternalContext externalContext = context.getExternalContext()
        String contentType = externalContext.getResponseContentType()
        String characterEncoding = externalContext.getResponseCharacterEncoding()
        externalContext.responseReset()
        OmniPartialViewContext.getCurrentInstance(context).resetPartialResponse()
        externalContext.setResponseContentType(contentType)
        externalContext.setResponseCharacterEncoding(characterEncoding)
    }
}

