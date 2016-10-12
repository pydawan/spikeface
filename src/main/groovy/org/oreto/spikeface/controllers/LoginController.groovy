package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoter
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoterContext
import org.apache.deltaspike.security.api.authorization.Secures
import org.apache.deltaspike.security.api.authorization.SecurityViolation
import org.omnifaces.util.Messages
import org.oreto.spikeface.UserLoggedIn
import org.picketlink.Identity
import org.picketlink.annotations.PicketLink
import org.picketlink.authentication.Authenticator
import org.picketlink.authentication.BaseAuthenticator
import org.picketlink.authentication.event.LoggedInEvent
import org.picketlink.authentication.event.LoginFailedEvent
import org.picketlink.credential.DefaultLoginCredentials
import org.picketlink.idm.model.basic.User

import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.enterprise.inject.spi.BeanManager
import javax.inject.Inject
import javax.inject.Named
import javax.interceptor.InvocationContext

@ApplicationScoped @Named @PicketLink
class LoginController extends BaseAuthenticator implements ApplicationController, AccessDecisionVoter {

    @Inject Identity identity

    private Class<? extends ViewConfig> deniedPage = Views.Index.class

    public Class<? extends ViewConfig> getDeniedPage() { deniedPage }

    public void handleLoggedIn(@Observes LoggedInEvent event) {
        navigate(getDeniedPage())
    }

    public void handleFailed(@Observes LoginFailedEvent event) {
        navigate(Views.Login)
    }

    public void login() {
        Identity.AuthenticationResult result = identity.login()
        if (Identity.AuthenticationResult.FAILED == result) {
            Messages.addGlobalInfo('Authentication was unsuccessful.  Please check your username and password')
        }
    }

    public Class<? extends ViewConfig> logout() {
        identity.logout()
        Views.Login
    }

    @Override
    Set<SecurityViolation> checkPermission(AccessDecisionVoterContext accessDecisionVoterContext) {
        List<SecurityViolation> violations = []
        if(!identity.loggedIn ) {
            deniedPage = viewConfigResolver
                    .getViewConfigDescriptor(facesContext.getViewRoot().getViewId())
                    .getConfigClass()
            violations.add(new SecurityViolation() {@Override String getReason() { 'not logged in' }})
        } else {
            violations.clear()
        }
        violations
    }

    @Inject DefaultLoginCredentials credentials

    @Override
    public void authenticate() {
        def test = 'test'
        if (test == credentials.getUserId() &&
                test == credentials.getPassword()) {
            setStatus(Authenticator.AuthenticationStatus.SUCCESS)
            setAccount(new User(test))
        } else {
            setStatus(Authenticator.AuthenticationStatus.FAILURE)
        }
    }

    @Secures @UserLoggedIn
    public boolean doSecuredCheck(InvocationContext invocationContext, BeanManager manager, Identity identity) throws Exception {
        identity.isLoggedIn()
    }
}