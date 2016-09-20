package org.oreto.spikeface.services;

import org.oreto.spikeface.models.Technology;
import org.oreto.spikeface.models.TechnologyRepository;

import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

@SessionScoped
public class TechnologyService implements Serializable {
    @Inject TechnologyRepository technologyRepository;

    public Technology create(Technology technology) {
        return technologyRepository.save(technology);
    }

    public List<Technology> list() {
        return technologyRepository.findAll();
    }
}
