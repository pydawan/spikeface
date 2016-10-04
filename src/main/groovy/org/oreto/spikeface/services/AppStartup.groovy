package org.oreto.spikeface.services

import com.ocpsoft.pretty.PrettyContext
import org.apache.deltaspike.core.api.common.DeltaSpike
import org.hibernate.Hibernate
import org.omnifaces.cdi.Eager
import org.omnifaces.config.OmniFaces
import org.oreto.spikeface.models.Technology
import org.primefaces.context.PrimeFacesContext

import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.faces.context.FacesContext
import javax.inject.Inject

@ApplicationScoped @Eager
public class AppStartup implements Serializable{

    @Inject TechnologyService repository

    @PostConstruct
    public void initDatabase() {
        def technologies = [:]
        technologies[DeltaSpike.package.name] = DeltaSpike.package.implementationVersion
        technologies[PrimeFacesContext.package.name] = PrimeFacesContext.package.implementationVersion
        technologies[FacesContext.class.package.name] = FacesContext.class.package.implementationVersion
        technologies[PrimeFacesContext.package.name] = PrimeFacesContext.package.implementationVersion
        technologies[PrettyContext.package.name] = PrettyContext.package.implementationVersion
        technologies[OmniFaces.package.name] = OmniFaces.version
        technologies[Hibernate.package.name] = Hibernate.package.implementationVersion
        technologies['java.version'] = System.getProperty("java.version")

        technologies.eachWithIndex { val, i ->
            String name = val.key
            def techOption = repository.findByName(name)
            if(!techOption.isPresent()) {
                Technology technology = new Technology(name: name, versionName: val.value)
                repository.save(technology)
            }
        }
    }
}
