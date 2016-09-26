package org.oreto.spikeface.services

import org.apache.deltaspike.core.api.common.DeltaSpike
import org.oreto.spikeface.models.Technology
import org.primefaces.context.PrimeFacesContext

import javax.annotation.PostConstruct
import javax.ejb.Startup
import javax.faces.context.FacesContext
import javax.inject.Inject

@Singleton
@Startup
public class AppStartup{

    @Inject TechnologyService repository

    @PostConstruct
    public void init() {
        def technologies = [:]
        technologies[DeltaSpike.package.name] = DeltaSpike.package.implementationVersion
        technologies[PrimeFacesContext.package.name] = PrimeFacesContext.package.implementationVersion
        technologies[FacesContext.class.getPackage().name] = FacesContext.class.getPackage().getImplementationVersion()
        technologies.each {
            Technology technology = new Technology(name: it.key, versionName: it.value)
            repository.save(technology)
        }
    }
}
