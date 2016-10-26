package org.oreto.spikeface.controllers;

import org.apache.deltaspike.core.api.config.view.ViewRef;
import org.apache.deltaspike.core.api.config.view.controller.PreRenderView;
import org.apache.deltaspike.core.api.config.view.metadata.ViewConfigResolver;
import org.apache.deltaspike.core.api.config.view.navigation.NavigationParameterContext;
import org.apache.deltaspike.core.api.config.view.navigation.ViewNavigationHandler;
import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoter;
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoterContext;
import org.apache.deltaspike.security.api.authorization.SecurityViolation;
import org.omnifaces.util.Messages;
import org.picketlink.Identity;
import org.picketlink.authentication.event.LoggedInEvent;
import org.picketlink.authentication.event.LoginFailedEvent;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PermissionManager;

import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@WindowScoped @Named @ViewRef(config = Views.Login.class)
public class LoginController implements ApplicationController, AccessDecisionVoter {

    @Inject ViewNavigationHandler viewNavigationHandler;
    @Inject ViewConfigResolver viewConfigResolver;
    @Inject NavigationParameterContext navigationParameterContext;
    @Inject FacesContext facesContext;
    @Inject Identity identity;
    @Inject IdentityManager identityManager;
    @Inject PermissionManager permissionManager;

    private String returnUrl = "";

    public void handleLoggedIn(@Observes LoggedInEvent event) throws IOException {
        redirect(returnUrl);
    }

    public void handleFailed(@Observes LoginFailedEvent event) {
        navigate(Views.Login.class);
    }

    public void login() {
        if(!identity.isLoggedIn()) {
            Identity.AuthenticationResult result = identity.login();
            if (Identity.AuthenticationResult.FAILED == result) {
                Messages.addFlashGlobalInfo(getBundle().getString("authenticationFailedMessage"));
            }
        }
    }

    public void logout() throws IOException {
        identityManager.remove(identity.getAccount());
        identity.logout();
        redirect(Views.Login.class);
    }

    @Override
    public Set<SecurityViolation> checkPermission(AccessDecisionVoterContext accessDecisionVoterContext) {
        List<SecurityViolation> violations = new ArrayList<>();
        if(!identity.isLoggedIn()) {
            returnUrl = getRequestUrlWithQueryString();
            violations.add((SecurityViolation) () -> getBundle().getString("notLoggedInMsg"));
        }
        return new LinkedHashSet<>(violations);
    }

    @PreRenderView protected void preRenderView() throws IOException {
        if(identity.isLoggedIn()) redirect(Views.Index.class);
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