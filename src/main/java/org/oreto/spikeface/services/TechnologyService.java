package org.oreto.spikeface.services;

import org.oreto.spikeface.models.Technology;
import org.oreto.spikeface.models.TechnologyRepository;

import javax.faces.bean.SessionScoped;
import javax.inject.Inject;

@SessionScoped
public class TechnologyService {
    @Inject TechnologyRepository technologyRepository;

    public Technology create(Technology technology) {
        return technologyRepository.save(technology);
    }
}
