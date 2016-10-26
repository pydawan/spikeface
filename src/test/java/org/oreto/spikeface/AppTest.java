package org.oreto.spikeface;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oreto.spikeface.controllers.NavigationController;

import javax.inject.Inject;
import java.io.File;

@RunWith(Arquillian.class)
public class AppTest {

    public @Inject NavigationController navigationController;

    @Deployment
    public static WebArchive createDeployment() {
        PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile("pom.xml");
        MavenFormatStage mavenFormatStage = pom.importCompileAndRuntimeDependencies()
                .resolve().withTransitivity();
        File[] files = mavenFormatStage.asFile();

        String WEBAPP_SRC = "src/main/webapp";

        WebArchive war = ShrinkWrap.create(WebArchive.class)
                .addAsLibraries(files)
                .addPackages(true, "org.oreto.spikeface")
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("META-INF/apache-deltaspike.properties")
                .addAsResource("ehcache.xml")
                .addAsResource("messages.properties");
        war.merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                        .importDirectory(WEBAPP_SRC).as(GenericArchive.class),
                "/", Filters.includeAll());
        return war;
    }

    @Test
    public void getTechListView() {
        Assert.assertEquals("/views/technology/list.xhtml", navigationController.getTechListView());
    }
}
