package org.oreto.spikeface.services

import com.ocpsoft.pretty.PrettyContext
import org.apache.deltaspike.core.api.common.DeltaSpike
import org.hibernate.Hibernate
import org.omnifaces.config.OmniFaces
import org.oreto.spikeface.models.Technology
import org.oreto.spikeface.models.TechnologyData
import org.picketlink.authentication.Authenticator
import org.primefaces.context.PrimeFacesContext
import org.springframework.core.SpringVersion
import org.springframework.data.jpa.repository.JpaRepository

import javax.annotation.PostConstruct
import javax.ejb.Startup
import javax.faces.context.FacesContext
import javax.inject.Inject

@javax.ejb.Singleton @Startup
public class TechnologyService implements Serializable {
    @Inject TechnologyData entityRepository

    @PostConstruct
    public void initDatabase() {
        def technologies = [:]
        technologies[GroovySystem.name] = GroovySystem.version
        technologies[DeltaSpike.package.name] = DeltaSpike.package.implementationVersion
        technologies[PrimeFacesContext.package.name] = PrimeFacesContext.package.implementationVersion
        technologies[FacesContext.class.package.name] = FacesContext.class.package.implementationVersion
        technologies[PrimeFacesContext.package.name] = PrimeFacesContext.package.implementationVersion
        technologies[PrettyContext.package.name] = PrettyContext.package.implementationVersion
        technologies[OmniFaces.package.name] = OmniFaces.version
        technologies[Hibernate.package.name] = Hibernate.package.implementationVersion
        technologies[JpaRepository.package.name] = SpringVersion.version
        technologies[Authenticator.package.name] = Authenticator.package.implementationVersion ?: '2.7.1.Final'
        technologies['java.version'] = System.getProperty("java.version")

        20.times {
            technologies[it] = it
        }

        for(Map.Entry<Object, Object> val : technologies) {
            String name = val.key
            def techOption = entityRepository.findOptionalByName(name)
            if(!techOption.isPresent()) {
                Technology technology = new Technology(name: name, versionName: val.value)
                entityRepository.save(technology)
            }
        }
    }
}
