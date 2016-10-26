package org.oreto.spikeface

import org.jboss.arquillian.container.test.api.Deployment
import org.jboss.arquillian.junit.Arquillian
import org.jboss.shrinkwrap.api.Filters
import org.jboss.shrinkwrap.api.GenericArchive
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.importer.ExplodedImporter
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.jboss.shrinkwrap.resolver.api.maven.Maven
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.oreto.spikeface.models.TechnologyRepository

import javax.inject.Inject
import javax.transaction.UserTransaction

@RunWith(Arquillian.class)
class AppTest {

    @Inject TechnologyRepository technologyRepository
    @Inject UserTransaction transaction

    @Before
    public void preTest() throws Exception {
        transaction.begin()
    }

    @Deployment
    public static WebArchive createDeployment() {
        PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile('pom.xml')
        MavenFormatStage mavenFormatStage = pom.importCompileAndRuntimeDependencies()
                .resolve().withTransitivity()
        File[] files = mavenFormatStage.asFile()

        String WEBAPP_SRC = 'src/main/webapp'

        WebArchive war = ShrinkWrap.create(WebArchive.class)
                .addAsLibraries(files)
                .addPackages(true, 'org.oreto.spikeface')
                .addAsResource('META-INF/persistence.xml')
                .addAsResource('META-INF/apache-deltaspike.properties')
                .addAsResource('ehcache.xml')
                .addAsResource('messages.properties')
        war.merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                .importDirectory(WEBAPP_SRC).as(GenericArchive.class),
                '/', Filters.includeAll())
        return war
    }

    @Test
    public void findById() {
        Assert.assertNotNull(technologyRepository.getOne(1.toLong()))
    }

    @Test
    public void findByName() {
        Assert.assertNotNull(technologyRepository.findOneByName('java.version'))
    }
}
