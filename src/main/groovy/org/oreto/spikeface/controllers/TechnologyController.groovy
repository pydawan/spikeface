package org.oreto.spikeface.controllers

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

    public String save() {
        technology = technologyService.save(technology)
        navigationParameterContext.addPageParameter('id', technology.id)
        getViewId(Pages.Technology.Show)
    }

    public String delete() {
        show()
        technologyService.delete(technology)
        getViewId(Pages.Technology.List)
    }
}
