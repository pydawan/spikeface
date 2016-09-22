package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.omnifaces.cdi.Param
import org.oreto.spikeface.models.Technology
import org.oreto.spikeface.services.TechnologyService

import javax.enterprise.inject.Model
import javax.inject.Inject

@Model
public class TechnologyController implements Scaffolding<Technology, Long> {

    @Inject @Param Long id

    @Inject TechnologyService repository
    @Inject Technology entity
    Class<? extends ViewConfig> showView = Pages.Technology.Show
    Class<? extends ViewConfig> listView = Pages.Technology.List
    Class<? extends ViewConfig> editView = Pages.Technology.Edit
    Class<? extends ViewConfig> createView = Pages.Technology.Create

    public Class<? extends ViewConfig> cancel() {
        listView
    }
}
