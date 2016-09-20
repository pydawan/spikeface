package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.navigation.NavigationParameterContext
import org.apache.deltaspike.core.api.config.view.navigation.ViewNavigationHandler
import org.oreto.spikeface.models.Technology
import org.oreto.spikeface.services.TechnologyService

import javax.enterprise.inject.Model
import javax.inject.Inject

@Model
public class TechnologyController {

    @Inject TechnologyService technologyService
    @Inject Technology technology

    @Inject private ViewNavigationHandler viewNavigationHandler;
    @Inject private NavigationParameterContext navigationParameterContext;

    Long id

    public List<Technology> list() {
        technologyService.list()
    }

    public Technology show() {
        technology = technologyService.get(id)
    }

    public void save() {
        technology = technologyService.save(technology)
        navigationParameterContext.addPageParameter("id", technology.id)
        this.viewNavigationHandler.navigateTo(Pages.Technology.Show)
    }

    public void delete() {
        show()
        technologyService.delete(technology)
        this.viewNavigationHandler.navigateTo(Pages.Technology.List)
    }
}
