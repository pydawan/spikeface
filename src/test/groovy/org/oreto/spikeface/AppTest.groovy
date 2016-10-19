package org.oreto.spikeface

import org.jboss.arquillian.container.test.api.Deployment
import org.jboss.arquillian.junit.Arquillian
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.asset.EmptyAsset
import org.jboss.shrinkwrap.api.spec.JavaArchive
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.oreto.spikeface.controllers.NavigationController

import javax.inject.Inject

@RunWith(Arquillian.class)
class AppTest {

    @Deployment
    public static JavaArchive createDeployment() {
         ShrinkWrap.create(JavaArchive.class)
                .addClass(NavigationController.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, 'beans.xml');
    }

    @Inject NavigationController navigationController

    @Test
    public void should_create_greeting() {
        Assert.assertEquals('', navigationController.getTechListView())
    }
}
