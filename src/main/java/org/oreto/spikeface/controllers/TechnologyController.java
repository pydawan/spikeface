package org.oreto.spikeface.controllers;

import org.oreto.spikeface.models.Technology;
import org.oreto.spikeface.services.TechnologyService;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

@Model
public class TechnologyController {

    @Inject TechnologyService technologyService;

    private Technology technology = new Technology();

    public Technology getTechnology() {
        return technology;
    }
    public void setTechnology(Technology technology) {
        this.technology = technology;
    }

    public String save() {
        technologyService.create(technology);
        return "";
    }
}
