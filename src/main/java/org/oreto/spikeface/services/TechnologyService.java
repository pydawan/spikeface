package org.oreto.spikeface.services;

import com.ocpsoft.pretty.PrettyContext;
import org.apache.deltaspike.core.api.common.DeltaSpike;
import org.hibernate.Hibernate;
import org.omnifaces.config.OmniFaces;
import org.oreto.spikeface.models.Technology;
import org.picketlink.authentication.Authenticator;
import org.primefaces.context.PrimeFacesContext;
import org.springframework.core.SpringVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@javax.ejb.Singleton @Startup
public class TechnologyService implements Serializable {
    @Inject TechnologyData entityRepository;

    @PostConstruct
    public void initDatabase() {
        Map<String, String> technologies = new HashMap<>();
        technologies.put(DeltaSpike.class.getPackage().getName(), DeltaSpike.class.getPackage().getImplementationVersion());
        technologies.put(PrimeFacesContext.class.getPackage().getName(), PrimeFacesContext.class.getPackage().getImplementationVersion());
        technologies.put(FacesContext.class.getPackage().getName(), FacesContext.class.getPackage().getImplementationVersion());
        technologies.put(PrimeFacesContext.class.getPackage().getName(), PrimeFacesContext.class.getPackage().getImplementationVersion());
        technologies.put(PrettyContext.class.getPackage().getName(), PrettyContext.class.getPackage().getImplementationVersion());
        technologies.put(OmniFaces.class.getPackage().getName(), OmniFaces.getVersion());
        technologies.put(Hibernate.class.getPackage().getName(), Hibernate.class.getPackage().getImplementationVersion());
        technologies.put(JpaRepository.class.getPackage().getName(), SpringVersion.getVersion());
        technologies.put(Authenticator.class.getPackage().getName(), "2.7.1.Final");
        technologies.put("java.version", System.getProperty("java.version"));

        for(int i = 0; i < 20; i++) {
            technologies.put(String.valueOf(i), String.valueOf(i));
        }

        for(Map.Entry<String, String> val : technologies.entrySet()) {
            String name = val.getKey();
            Optional<Technology> techOption = entityRepository.findOptionalByName(name);
            if(!techOption.isPresent()) {
                Technology technology = new Technology();
                technology.setName(name);
                technology.setVersionName(val.getValue());
                entityRepository.save(technology);
            }
        }
    }
}
