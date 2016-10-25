package org.oreto.spikeface.controllers;

import com.ocpsoft.pretty.PrettyContext;
import org.apache.deltaspike.core.api.config.view.ViewConfig;
import org.apache.deltaspike.core.api.config.view.metadata.ViewConfigResolver;
import org.apache.deltaspike.core.api.config.view.navigation.NavigationParameterContext;
import org.apache.deltaspike.core.api.config.view.navigation.ViewNavigationHandler;
import org.omnifaces.util.Servlets;
import org.oreto.spikeface.utils.Utils;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PermissionManager;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

abstract class ApplicationController implements Serializable {
    @Inject ViewNavigationHandler viewNavigationHandler;
    @Inject ViewConfigResolver viewConfigResolver;
    @Inject NavigationParameterContext navigationParameterContext;
    @Inject FacesContext facesContext;
    @Inject Identity identity;
    @Inject IdentityManager identityManager;
    @Inject PermissionManager permissionManager;

    @ManagedProperty("#{msg}") ResourceBundle bundle;

    public String getLanguage() {
        return facesContext.getViewRoot().getLocale().getLanguage();
    }

    public void changeLanguage(String language) {
        facesContext.getViewRoot().setLocale(new Locale(language));
    }

    public ResourceBundle getBundle() {
        return facesContext.getApplication().getResourceBundle(facesContext, "msg");
    }

    public String getViewId(Class<? extends ViewConfig> view) {
        return  viewConfigResolver.getViewConfigDescriptor(view).getViewId();
    }

    public void render(Class<? extends ViewConfig> view) {
        Utils.render(getViewId(view));
    }

    public void navigate(Class<? extends ViewConfig> view) {
        viewNavigationHandler.navigateTo(view);
    }

    public void redirect(String url) throws IOException {
        Servlets.facesRedirect(getRequest(), getResponse(), url);
    }

    public void redirect(Class<? extends ViewConfig> view, String queryString) throws IOException {
        redirect("${getBaseUrl()}${getViewId(view)}?$queryString");
    }

    public void redirect(Class<? extends ViewConfig> view) throws IOException {
        redirect("${getBaseUrl()}${getViewId(view)}");
    }

    public void notFound() {
        render(Views.Error.Notfound.class);
    }

    public void readOnly() {
        render(Views.Error.Readonly.class);
    }

    public HttpServletRequest getRequest() {
        return (HttpServletRequest) facesContext.getExternalContext().getRequest();
    }

    public PrettyContext getPretty() {
        return PrettyContext.getCurrentInstance();
    }

    public HttpServletResponse getResponse() {
        return (HttpServletResponse) facesContext.getExternalContext().getResponse();
    }

    public String getRequestUrlWithQueryString() {
        return "${getBaseUrl()}${getRequestUrl()}${pretty.requestQueryString.toString()}";
    }

    public String getRequestUrl() {
        return getPretty().getRequestURL().toString();
    }

    public String getBaseUrl() {
        return  getRequest().getContextPath();
    }

    public boolean hasFacesError() {
        for(FacesMessage message : facesContext.getMessageList()) {
            if(message.getSeverity() == FacesMessage.SEVERITY_ERROR) return true;
        }
        return false;
    }
}



