package org.oreto.spikeface.controllers

//import org.apache.deltaspike.core.api.exception.control.ExceptionHandler
//import org.apache.deltaspike.core.api.exception.control.Handles
//import org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent
//import org.omnifaces.context.OmniPartialViewContext
//import org.omnifaces.util.Hacks
//import javax.faces.application.ViewHandler
//import javax.faces.context.ExternalContext
//import javax.faces.event.PhaseId
//import javax.faces.event.PreRenderViewEvent
//import javax.faces.view.ViewDeclarationLanguage
//@ExceptionHandler
//public class ViewExceptionHandler {
//    @Inject private ViewConfigResolver viewConfigResolver
//
//    public void onException(@Handles ExceptionEvent<Exception> event)
//    {
//        FacesContext context = FacesContext.getCurrentInstance()
//        String viewId = viewConfigResolver.getViewConfigDescriptor(Pages.Error.Server).viewId
//        ViewHandler viewHandler = context.getApplication().getViewHandler()
//        UIViewRoot viewRoot = viewHandler.createView(context, viewId)
//        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, event.exception.message, event.exception.toString())
//
//        if(context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
//            resetResponse(context)
//            context.addMessage(null, facesMessage)
//            ViewDeclarationLanguage vdl = viewHandler.getViewDeclarationLanguage(context, viewId)
//            vdl.buildView(context, viewRoot)
//            context.getApplication().publishEvent(context, PreRenderViewEvent.class, viewRoot)
//            vdl.renderView(context, viewRoot)
//            context.responseComplete()
//            event.handled()
//        } else {
//            context.addMessage(null, facesMessage)
//            context.setViewRoot(viewRoot)
//        }
//    }
//
//    static void resetResponse(FacesContext context) {
//        Hacks.removeViewState(context, context.getRenderKit().getResponseStateManager(), context.getViewRoot().viewId)
//        context.getAttributes().values().removeAll(Collections.singleton(true))
//        ExternalContext externalContext = context.getExternalContext()
//        String contentType = externalContext.getResponseContentType()
//        String characterEncoding = externalContext.getResponseCharacterEncoding()
//        externalContext.responseReset()
//        OmniPartialViewContext.getCurrentInstance(context).resetPartialResponse()
//        externalContext.setResponseContentType(contentType)
//        externalContext.setResponseCharacterEncoding(characterEncoding)
//    }
//}

