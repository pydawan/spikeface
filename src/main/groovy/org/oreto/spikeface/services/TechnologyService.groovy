package org.oreto.spikeface.services

import org.oreto.spikeface.models.RepoImpl
import org.oreto.spikeface.models.Technology
import org.oreto.spikeface.models.TechnologyRepository

import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped @Transactional
public class TechnologyService extends RepoImpl<Technology> implements Serializable {
    @Inject TechnologyRepository entityRepository

    Optional<Technology> findByName(String name) {
        entityRepository.findByName(name)
    }
}
