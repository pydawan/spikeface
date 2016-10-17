package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.core.api.scope.WindowScoped
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoter
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoterContext
import org.apache.deltaspike.security.api.authorization.SecurityViolation
import org.omnifaces.util.Messages
import org.oreto.spikeface.models.Technology
import org.picketlink.Identity
import org.picketlink.annotations.PicketLink
import org.picketlink.authentication.Authenticator
import org.picketlink.authentication.BaseAuthenticator
import org.picketlink.authentication.event.LoggedInEvent
import org.picketlink.authentication.event.LoginFailedEvent
import org.picketlink.credential.DefaultLoginCredentials
import org.picketlink.idm.IdentityManager
import org.picketlink.idm.PartitionManager
import org.picketlink.idm.PermissionManager
import org.picketlink.idm.credential.Password
import org.picketlink.idm.model.basic.User

import javax.enterprise.event.Observes
import javax.inject.Inject
import javax.inject.Named

@WindowScoped @Named @PicketLink
class LoginController extends BaseAuthenticator implements ApplicationController, AccessDecisionVoter {

    @Inject PartitionManager partitionManager

    String returnUrl = ''

    public void handleLoggedIn(@Observes LoggedInEvent event) {
        redirect(returnUrl)
    }

    public void handleFailed(@Observes LoginFailedEvent event) {
        navigate(Views.Login)
    }

    public void login() {
        Identity.AuthenticationResult result = identity.login()
        if (Identity.AuthenticationResult.FAILED == result) {
            Messages.addFlashGlobalInfo('Authentication was unsuccessful.  Please check your username and password')
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
            returnUrl = requestUrlWithQueryString
            violations.add(new SecurityViolation() {@Override String getReason() { 'not logged in' }})
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
            User user = new User(test)
            setAccount(user)
            IdentityManager identityManager = partitionManager.createIdentityManager()
            identityManager.add(user)
            identityManager.updateCredential(user, new Password(test))
            PermissionManager permissionManager = partitionManager.createPermissionManager()
            permissionManager.grantPermission(user, new Technology(id: 1), "manage")
        } else {
            setStatus(Authenticator.AuthenticationStatus.FAILURE)
        }
    }
}