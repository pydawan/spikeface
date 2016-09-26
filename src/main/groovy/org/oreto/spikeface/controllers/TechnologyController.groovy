package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.core.api.config.view.ViewRef
import org.apache.deltaspike.core.api.config.view.controller.PreRenderView
import org.omnifaces.cdi.Param
import org.omnifaces.cdi.ViewScoped
import org.oreto.spikeface.models.Technology
import org.oreto.spikeface.services.TechnologyService
import org.primefaces.model.LazyDataModel

import javax.inject.Inject
import javax.inject.Named

@Named @ViewScoped @ViewRef(config = [Pages.Technology.Show, Pages.Technology.Save])
public class TechnologyController implements Scaffolding<Technology, Long> {

    @Inject TechnologyService repository
    @Inject Technology entity
    @Inject @Param Long id

    Class<? extends ViewConfig> showView = Pages.Technology.Show
    Class<? extends ViewConfig> listView = Pages.Technology.List
    Class<? extends ViewConfig> saveView = Pages.Technology.Save

    @PreRenderView protected void preRenderView() { get() }

    LazyDataModel<Technology> getLazyData() {
        repository.entityRepository as LazyDataModel<Technology>
    }
}
