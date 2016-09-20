package org.oreto.spikeface.services

import org.oreto.spikeface.models.Technology
import org.oreto.spikeface.models.TechnologyRepository

import javax.faces.bean.SessionScoped
import javax.inject.Inject

@SessionScoped
public class TechnologyService implements Serializable {
    @Inject TechnologyRepository technologyRepository

    public Technology create(Technology technology) {
        technologyRepository.save(technology)
    }

    public List<Technology> list() {
        technologyRepository.findAll()
    }
}
