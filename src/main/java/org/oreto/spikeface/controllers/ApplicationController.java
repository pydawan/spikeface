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
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public interface ApplicationController {
    ViewNavigationHandler getViewNavigationHandler();
    ViewConfigResolver getViewConfigResolver();
    NavigationParameterContext getNavigationParameterContext();
    FacesContext getFacesContext();
    Identity getIdentity();
    IdentityManager getIdentityManager();
    PermissionManager getPermissionManager();

    default String getLanguage() {
        return getFacesContext().getViewRoot().getLocale().getLanguage();
    }
    default void changeLanguage(String language) {
        getFacesContext().getViewRoot().setLocale(new Locale(language));
    }
    default ResourceBundle getBundle() {
        return getFacesContext().getApplication().getResourceBundle(getFacesContext(), "msg");
    }

    default String getViewId(Class<? extends ViewConfig> view) {
        return  getViewConfigResolver().getViewConfigDescriptor(view).getViewId();
    }
    default void render(Class<? extends ViewConfig> view) throws IOException {
        Utils.render(getViewId(view));
    }
    default void navigate(Class<? extends ViewConfig> view) {
        getViewNavigationHandler().navigateTo(view);
    }
    default void redirect(String url) throws IOException {
        Servlets.facesRedirect(getRequest(), getResponse(), url);
    }
    default void redirect(Class<? extends ViewConfig> view, String queryString) throws IOException {
        redirect(String.format("%s%s?%s",
                getBaseUrl(), getViewId(view), queryString));
    }
    default void redirect(Class<? extends ViewConfig> view) throws IOException {
        redirect(String.format("%s%s",
                getBaseUrl(), getViewId(view)));
    }

    default void notFound() throws IOException {
        render(Views.Error.Notfound.class);
    }
    default void readOnly() throws IOException {
        render(Views.Error.Readonly.class);
    }

    default HttpServletRequest getRequest() {
        return (HttpServletRequest) getFacesContext().getExternalContext().getRequest();
    }
    default PrettyContext getPretty() {
        return PrettyContext.getCurrentInstance();
    }
    default HttpServletResponse getResponse() {
        return (HttpServletResponse) getFacesContext().getExternalContext().getResponse();
    }
    default String getRequestUrlWithQueryString() {
        return String.format("%s%s%s",
                getBaseUrl(), getRequestUrl(), getPretty().getRequestQueryString().toString());
    }

    default String getRequestUrl() {
        return getPretty().getRequestURL().toString();
    }
    default String getBaseUrl() {
        return  getRequest().getContextPath();
    }

    default boolean hasFacesError() {
        for(FacesMessage message : getFacesContext().getMessageList()) {
            if(message.getSeverity() == FacesMessage.SEVERITY_ERROR) return true;
        }
        return false;
    }
}



