package org.oreto.spikeface.controllers.common

import com.ocpsoft.pretty.PrettyContext
import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.core.api.config.view.metadata.ViewConfigResolver
import org.apache.deltaspike.core.api.config.view.navigation.NavigationParameterContext
import org.apache.deltaspike.core.api.config.view.navigation.ViewNavigationHandler
import org.omnifaces.util.Servlets
import org.oreto.spikeface.utils.UrlEncodedQueryString
import org.oreto.spikeface.utils.Utils
import org.picketlink.Identity
import org.picketlink.idm.IdentityManager
import org.picketlink.idm.PermissionManager

import javax.faces.application.FacesMessage
import javax.faces.bean.ManagedProperty
import javax.faces.context.FacesContext
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

trait ApplicationController implements Serializable {
    @Inject ViewNavigationHandler viewNavigationHandler
    @Inject ViewConfigResolver viewConfigResolver
    @Inject NavigationParameterContext navigationParameterContext
    @Inject FacesContext facesContext
    @Inject Identity identity
    @Inject IdentityManager identityManager
    @Inject PermissionManager permissionManager

    @ManagedProperty("#{msg}") ResourceBundle bundle

    public String getLanguage() {
        facesContext.getViewRoot().getLocale().language
    }

    public void changeLanguage(String language) {
        facesContext.viewRoot.locale = new Locale(language)
    }

    public ResourceBundle getBundle() {
        facesContext.application.getResourceBundle(facesContext, 'msg')
    }

    public String getViewId(Class<? extends ViewConfig> view) {
        viewConfigResolver.getViewConfigDescriptor(view).viewId
    }

    public void render(Class<? extends ViewConfig> view) {
        Utils.render(getViewId(view))
    }

    public void navigate(Class<? extends ViewConfig> view) {
        viewNavigationHandler.navigateTo(view)
    }

    public void redirect(Map query) {
        UrlEncodedQueryString queryString = UrlEncodedQueryString.parse(pretty.requestQueryString.toString())
        for (Map.Entry<String, String> entry : query.entrySet()) {
            queryString.append(entry.key, entry.value)
        }
        redirect("${getBaseUrl()}${pretty.requestURL}$queryString")
    }

    public void redirect(Class<? extends ViewConfig> view, Map query) {
        UrlEncodedQueryString queryString = UrlEncodedQueryString.parse(pretty.requestQueryString.toString())
        for (Map.Entry<String, String> entry : query.entrySet()) {
            queryString.append(entry.key, entry.value)
        }
        redirect(view, queryString.toString())
    }

    public void redirect(String url) {
        Servlets.facesRedirect(getRequest(), getResponse(), url)
    }

    public void redirect(Class<? extends ViewConfig> view, String queryString) {
        redirect("${getBaseUrl()}${getViewId(view)}$queryString")
    }

    public void redirect(Class<? extends ViewConfig> view){
        redirect("${getBaseUrl()}${getViewId(view)}")
    }

    public void notFound() {
        render(Views.Error.Notfound)
    }

    public void readOnly() {
        render(Views.Error.Readonly)
    }

    public HttpServletRequest getRequest() {
        facesContext.externalContext.request as HttpServletRequest
    }

    public PrettyContext getPretty() {
        PrettyContext.currentInstance
    }

    public HttpServletResponse getResponse() {
        facesContext.externalContext.response as HttpServletResponse
    }

    public String getRequestUrlWithQueryString() {
        "${getBaseUrl()}${getRequestUrl()}${pretty.requestQueryString.toString()}"
    }

    public String getRequestUrl() {
        pretty.getRequestURL().toString()
    }

    public boolean requestEqualsView(Class<? extends ViewConfig> view) {
        pretty.config.getMappingForUrl(pretty.requestURL).viewId == getViewId(view)
    }

    public String getBaseUrl() {
        getRequest().contextPath
    }

    public boolean hasFacesError() {
        for(FacesMessage message : facesContext.messageList) {
            if(message.severity == FacesMessage.SEVERITY_ERROR) return true
        }
        false
    }

    public Map<String, Object> getSession() {
        facesContext.externalContext.sessionMap
    }

    public Object getSession(String key) {
        getSession().get(key)
    }

    public void putSession(String key, Object val) {
        getSession().put(key, val)
    }

    public String getParam(String name) {
        Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap()
        params.get(name)
    }
}