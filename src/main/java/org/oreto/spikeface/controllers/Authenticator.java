package org.oreto.spikeface.controllers;

import org.oreto.spikeface.services.TechnologyData;
import org.picketlink.annotations.PicketLink;
import org.picketlink.authentication.BaseAuthenticator;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PermissionManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.basic.User;

import javax.inject.Inject;
import java.util.Objects;

@PicketLink
public class Authenticator extends BaseAuthenticator {

    @Inject DefaultLoginCredentials credentials;
    @Inject PermissionManager permissionManager;
    @Inject IdentityManager identityManager;
    @Inject TechnologyData technologyData;

    @Override
    public void authenticate() {
        String test = "test";
        if (Objects.equals(test, credentials.getUserId()) &&
                Objects.equals(test, credentials.getPassword())) {
            setStatus(org.picketlink.authentication.Authenticator.AuthenticationStatus.SUCCESS);
            User user = new User(test);
            setAccount(user);
            identityManager.add(user);
            identityManager.updateCredential(user, new Password(test));
            for(int i = 0; i < 20; i++) {
                permissionManager.grantPermission(user, technologyData.findOptionalByName(Integer.toString(i)).get(), "manage");
            }
        } else {
            setStatus(org.picketlink.authentication.Authenticator.AuthenticationStatus.FAILURE);
        }
    }
}
