package org.oreto.spikeface

import org.jboss.arquillian.container.test.api.Deployment
import org.jboss.arquillian.junit.Arquillian
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.asset.EmptyAsset
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.oreto.spikeface.controllers.NavigationController

import javax.inject.Inject

@RunWith(Arquillian.class)
class AppTest {

    @Inject NavigationController navigationController

    @Deployment
    public static WebArchive createDeployment() {
         ShrinkWrap.create(WebArchive.class)
                .addClass(NavigationController.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, 'beans.xml')
    }

    @Test
    public void getTechListView() {
        Assert.assertEquals('', navigationController.getTechListView())
    }
}
