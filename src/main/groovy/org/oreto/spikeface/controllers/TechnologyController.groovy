package org.oreto.spikeface.controllers

import org.oreto.spikeface.models.Technology
import org.oreto.spikeface.services.TechnologyService

import javax.enterprise.inject.Model
import javax.inject.Inject

@Model
public class TechnologyController {

    @Inject TechnologyService technologyService

    Technology technology = new Technology()
    Long id

    public Technology create() {
        technologyService.save(technology)
    }

    public List<Technology> list() {
        technologyService.list()
    }

    public Technology show() {
        technology = technologyService.get(id)
    }
}
