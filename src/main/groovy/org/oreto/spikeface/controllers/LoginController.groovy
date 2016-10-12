package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.core.api.config.view.metadata.ViewConfigResolver
import org.apache.deltaspike.core.api.config.view.navigation.ViewNavigationHandler
import org.apache.deltaspike.security.api.authorization.AbstractAccessDecisionVoter
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoterContext
import org.apache.deltaspike.security.api.authorization.SecurityViolation
import org.picketlink.Identity
import org.picketlink.authentication.event.LoggedInEvent
import org.picketlink.authentication.event.LoginFailedEvent
import org.picketlink.idm.IdentityManager
import org.picketlink.idm.PartitionManager
import org.picketlink.idm.credential.Password
import org.picketlink.idm.model.basic.User

import javax.annotation.PostConstruct
import javax.ejb.Startup
import javax.enterprise.context.SessionScoped
import javax.enterprise.event.Observes
import javax.faces.context.FacesContext
import javax.inject.Inject

@SessionScoped
class LoginController extends AbstractAccessDecisionVoter{

    @Inject private ViewConfigResolver viewConfigResolver
    @Inject Identity identity
    @Inject private ViewNavigationHandler viewNavigationHandler

    private Class<? extends ViewConfig> deniedPage = Views.Index.class

    @Override
    protected void checkPermission(AccessDecisionVoterContext accessDecisionVoterContext, Set<SecurityViolation> violations) {
        if(!identity.loggedIn) {
            violations.add(new SecurityViolation() {
                @Override public String getReason() {
                    'User must be logged in to access this resource'
                }
            })
            deniedPage = viewConfigResolver
                    .getViewConfigDescriptor(FacesContext.getCurrentInstance().getViewRoot().getViewId())
                    .getConfigClass()
        }
    }

    public Class<? extends ViewConfig> getDeniedPage() { deniedPage }

    public void handleLoggedIn(@Observes LoggedInEvent event) {
        this.viewNavigationHandler.navigateTo(getDeniedPage())
    }

    public void handleFailed(@Observes LoginFailedEvent event) {
        this.viewNavigationHandler.navigateTo(Views.Login)
    }
}

/**
 * This startup bean creates the default users, groups and roles when the application is started.
 */
@Singleton
@Startup
public class Initializer
{
    @Inject private PartitionManager partitionManager

    @PostConstruct
    public void create() {
        User ross = new User("ross")
        ross.setEmail("ross@sea.com")
        ross.setFirstName("Ross")
        ross.setLastName("Sea")

        IdentityManager identityManager = this.partitionManager.createIdentityManager()
        identityManager.add(ross)
        identityManager.updateCredential(ross, new Password("test"))
    }
}


