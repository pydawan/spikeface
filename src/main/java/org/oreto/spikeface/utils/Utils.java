package org.oreto.spikeface.utils;

import com.ocpsoft.pretty.PrettyContext;
import org.apache.deltaspike.core.util.StringUtils;
import org.omnifaces.context.OmniPartialViewContext;
import org.primefaces.util.ComponentUtils;

import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.event.PreRenderViewEvent;
import javax.faces.view.ViewDeclarationLanguage;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;

public class Utils {
    public static String getHeader(String header) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().get(header);
    }

    public static void addFacesMessage(String summary, String message, FacesMessage.Severity severity) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage = StringUtils.isNotEmpty(summary) ? new FacesMessage(severity, summary, message) : new FacesMessage(summary);
        facesContext.addMessage(null, facesMessage);
    }

    public static void addFacesMessage(String summary, String message) {
        addFacesMessage(summary, message, FacesMessage.SEVERITY_ERROR);
    }

    public static void addFacesMessage(String summary) {
        addFacesMessage(summary, "", FacesMessage.SEVERITY_ERROR);
    }

    public static void render(String viewId) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            if (!context.getExternalContext().isResponseCommitted()) {
                reRender(viewId);
            }
        } else {
            ViewHandler viewHandler = context.getApplication().getViewHandler();
            UIViewRoot viewRoot = viewHandler.createView(context, viewId);
            context.setViewRoot(viewRoot);
        }
    }

    public static void reRender(String viewId) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        resetResponse();
        ViewHandler viewHandler = context.getApplication().getViewHandler();
        UIViewRoot viewRoot = viewHandler.createView(context, viewId);
        ViewDeclarationLanguage vdl = viewHandler.getViewDeclarationLanguage(context, viewId);
        vdl.buildView(context, viewRoot);
        context.getApplication().publishEvent(context, PreRenderViewEvent.class, viewRoot);
        vdl.renderView(context, viewRoot);
        context.responseComplete();
    }

    public static void resetResponse() {
        FacesContext context = FacesContext.getCurrentInstance();
        //Hacks.removeViewState(context, context.getRenderKit().getResponseStateManager(), context.getViewRoot().viewId)
        context.getAttributes().values().removeAll(Collections.singleton(true));

        ExternalContext externalContext = context.getExternalContext();
        String contentType = externalContext.getResponseContentType();
        String characterEncoding = externalContext.getResponseCharacterEncoding();
        externalContext.responseReset();
        OmniPartialViewContext.getCurrentInstance(context).resetPartialResponse();
        externalContext.setResponseContentType(contentType);
        externalContext.setResponseCharacterEncoding(characterEncoding);
    }

    public static String escapeXmlWithBreaks(String text) {
        return ComponentUtils.escapeXml(text).replaceAll("(\r\n|\n)", "<br/>");
    }

    public static String getPrettyUrl(FacesContext context, boolean withQueryString) {
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        PrettyContext pretty = PrettyContext.getCurrentInstance();
        if(withQueryString)
            return String.format("%s%s%s",
                    request.getContextPath(), pretty.getRequestURL(), pretty.getRequestQueryString());
        else
            return String.format("%s%s",
                    request.getContextPath(), pretty.getRequestURL());
    }

    public static String getPrettyUrl(FacesContext context) {
        return getPrettyUrl(context, true);
    }

    public static UrlEncodedQueryString newQueryString(FacesContext context) {
        return UrlEncodedQueryString.parse(getPrettyUrl(context));
    }
}
