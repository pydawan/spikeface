package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewRef
import org.apache.deltaspike.core.api.config.view.controller.PreRenderView
import org.apache.deltaspike.core.api.scope.WindowScoped
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoter
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoterContext
import org.apache.deltaspike.security.api.authorization.SecurityViolation
import org.omnifaces.util.Messages
import org.oreto.spikeface.models.TechnologyData
import org.picketlink.Identity
import org.picketlink.annotations.PicketLink
import org.picketlink.authentication.Authenticator
import org.picketlink.authentication.BaseAuthenticator
import org.picketlink.authentication.event.LoggedInEvent
import org.picketlink.authentication.event.LoginFailedEvent
import org.picketlink.credential.DefaultLoginCredentials
import org.picketlink.idm.PartitionManager
import org.picketlink.idm.credential.Password
import org.picketlink.idm.model.basic.Realm
import org.picketlink.idm.model.basic.User

import javax.annotation.PostConstruct
import javax.enterprise.event.Observes
import javax.inject.Inject
import javax.inject.Named

@WindowScoped @Named @PicketLink @ViewRef(config = Views.Login)
class LoginController extends BaseAuthenticator implements ApplicationController, AccessDecisionVoter {

    @Inject PartitionManager partitionManager
    @Inject TechnologyData technologyData

    String returnUrl = ''

    @PostConstruct
    public void init(){
        Realm defaultRealm = this.partitionManager.getPartition(Realm.class, "default")
        if (defaultRealm == null){
            System.out.println("Couldn't find default partition, creating default partition")
            defaultRealm = new Realm("default")
            this.partitionManager.add(defaultRealm)
        } else {
            System.out.println("Found default partition")
        }
    }

    public void handleLoggedIn(@Observes LoggedInEvent event) {
        redirect(returnUrl)
    }

    public void handleFailed(@Observes LoginFailedEvent event) {
        navigate(Views.Login)
    }

    public void login() {
        if(!identity.isLoggedIn()) {
            Identity.AuthenticationResult result = identity.login()
            if (Identity.AuthenticationResult.FAILED == result) {
                Messages.addFlashGlobalInfo('Authentication was unsuccessful.  Please check your username and password')
            }
        }
    }

    public void logout() {
        identityManager.remove(identity.getAccount())
        identity.logout()
        redirect(Views.Login)
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
            identityManager.add(user)
            identityManager.updateCredential(user, new Password(test))
            20.times {
                permissionManager.grantPermission(user, technologyData.findOptionalByName(it.toString()).get(), "manage")
            }
        } else {
            setStatus(Authenticator.AuthenticationStatus.FAILURE)
        }
    }

    @PreRenderView protected void preRenderView() {
        if(identity.isLoggedIn()) redirect(Views.Index)
    }
}