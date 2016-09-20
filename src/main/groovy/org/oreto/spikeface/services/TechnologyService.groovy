package org.oreto.spikeface.services

import org.oreto.spikeface.models.RepoImpl
import org.oreto.spikeface.models.Technology
import org.oreto.spikeface.models.TechnologyRepository

import javax.faces.bean.SessionScoped
import javax.inject.Inject

@SessionScoped
public class TechnologyService extends RepoImpl<Technology> implements Serializable {
    @Inject TechnologyRepository entityRepository
}
