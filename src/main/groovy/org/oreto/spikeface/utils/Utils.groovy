package org.oreto.spikeface.utils

import com.ocpsoft.pretty.PrettyContext
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.message.BasicNameValuePair
import org.omnifaces.context.OmniPartialViewContext
import org.primefaces.util.ComponentUtils

import javax.faces.application.FacesMessage
import javax.faces.application.ViewHandler
import javax.faces.component.UIViewRoot
import javax.faces.context.ExternalContext
import javax.faces.context.FacesContext
import javax.faces.event.PhaseId
import javax.faces.event.PreRenderViewEvent
import javax.faces.view.ViewDeclarationLanguage
import javax.servlet.http.HttpServletRequest
import java.nio.charset.Charset

public class Utils {

    public static String getHeader(String header) {
        FacesContext.currentInstance.externalContext.requestHeaderMap.get(header)
    }

    public
    static void addFacesMessage(String summary, String message = '', FacesMessage.Severity severity = FacesMessage.SEVERITY_ERROR) {
        FacesContext facesContext = FacesContext.currentInstance
        FacesMessage facesMessage = summary ? new FacesMessage(severity, summary, message) : new FacesMessage(summary)
        facesContext.addMessage(null, facesMessage)
    }

    static void render(String viewId) {
        FacesContext context = FacesContext.getCurrentInstance()
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            if (!context.getExternalContext().isResponseCommitted()) {
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

    static String getPrettyUrl(FacesContext context, boolean withQueryString = true) {
        def request = context.getExternalContext().request as HttpServletRequest
        def pretty = PrettyContext.currentInstance
        if(withQueryString) "${request.getContextPath()}${pretty.requestURL}${pretty.requestQueryString}"
        else "${request.getContextPath()}$pretty.requestURL"
    }

    static String newQueryString(FacesContext context, Map<String, String> params) {
        def pretty = PrettyContext.currentInstance
        Map<String, String> paramMap = URLEncodedUtils.parse(pretty.requestQueryString.toString(), Charset.defaultCharset()).collectEntries {
            [(it.name.startsWith('?') ? it.name.substring(1) : it.name) : it.value]
        }
        params.each {
            paramMap.put(it.key.toString(), it.value)
        }
        def nameValues = paramMap.collect {
            new BasicNameValuePair(it.key, it.value)
        }
        String newQS = URLDecoder.decode(URLEncodedUtils.format(nameValues, Charset.defaultCharset()), Charset.defaultCharset().name())
        def request = context.getExternalContext().request as HttpServletRequest
        "${request.getContextPath()}${pretty.requestURL}?${newQS}"
    }

    static String newQueryString(FacesContext context, String...exclude) {
        def pretty = PrettyContext.currentInstance
        Map<String, String> paramMap = URLEncodedUtils.parse(pretty.requestQueryString.toString(), Charset.defaultCharset()).collectEntries {
            [(it.name.startsWith('?') ? it.name.substring(1) : it.name) : it.value]
        }
        exclude.each { paramMap.remove(it) }
        def nameValues = paramMap.collect {
            new BasicNameValuePair(it.key, it.value)
        }
        String newQS = URLDecoder.decode(URLEncodedUtils.format(nameValues, Charset.defaultCharset()), Charset.defaultCharset().name())
        def request = context.getExternalContext().request as HttpServletRequest
        "${request.getContextPath()}${pretty.requestURL}?${newQS}"
    }
}
