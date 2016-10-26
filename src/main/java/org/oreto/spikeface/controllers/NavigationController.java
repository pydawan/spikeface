package org.oreto.spikeface.controllers;


import org.apache.deltaspike.core.api.config.view.metadata.ViewConfigResolver;
import org.apache.deltaspike.core.api.config.view.navigation.NavigationParameterContext;
import org.apache.deltaspike.core.api.config.view.navigation.ViewNavigationHandler;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PermissionManager;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

@ApplicationScoped @Named
public class NavigationController implements ApplicationController {

    @Inject ViewNavigationHandler viewNavigationHandler;
    @Inject ViewConfigResolver viewConfigResolver;
    @Inject NavigationParameterContext navigationParameterContext;
    @Inject FacesContext facesContext;
    @Inject Identity identity;
    @Inject IdentityManager identityManager;
    @Inject PermissionManager permissionManager;

    public String getTechListView() {
        return getViewId(Views.Technology.List.class);
    }
    public String getTechSaveView() {
        return getViewId(Views.Technology.Save.class);
    }

    @Override
    public ViewNavigationHandler getViewNavigationHandler() {
        return viewNavigationHandler;
    }

    @Override
    public ViewConfigResolver getViewConfigResolver() {
        return viewConfigResolver;
    }

    @Override
    public NavigationParameterContext getNavigationParameterContext() {
        return navigationParameterContext;
    }

    @Override
    public FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    public Identity getIdentity() {
        return identity;
    }

    @Override
    public IdentityManager getIdentityManager() {
        return identityManager;
    }

    @Override
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }
}
