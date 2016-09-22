package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.oreto.spikeface.models.Technology
import org.oreto.spikeface.services.TechnologyService

import javax.enterprise.inject.Model
import javax.inject.Inject

@Model
public class TechnologyController implements ApplicationController {

    @Inject TechnologyService technologyService
    @Inject Technology technology

    public List<Technology> list() {
        technologyService.list()
    }

    public void show() {
        technology = technologyService.get(id as Long)
        if(!technology) notFound()
    }

    public Class<? extends ViewConfig> save() {
        technology = technologyService.save(technology)
        navigationParameterContext.addPageParameter('id', technology.id)
        Pages.Technology.Show
    }

    public Class<? extends ViewConfig> delete() {
        show()
        technologyService.delete(technology)
        Pages.Technology.List
    }
}
