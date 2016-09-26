package org.oreto.spikeface.services

import org.oreto.spikeface.models.RepoImpl
import org.oreto.spikeface.models.Technology
import org.oreto.spikeface.models.TechnologyRepository

import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
public class TechnologyService extends RepoImpl<Technology> implements Serializable {
    @Inject TechnologyRepository entityRepository
}
