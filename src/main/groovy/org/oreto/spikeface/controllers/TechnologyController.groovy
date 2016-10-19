package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.core.api.config.view.ViewRef
import org.apache.deltaspike.core.api.config.view.controller.PreRenderView
import org.apache.deltaspike.security.api.authorization.Secured
import org.omnifaces.cdi.Param
import org.omnifaces.cdi.ViewScoped
import org.oreto.spikeface.models.Technology
import org.oreto.spikeface.models.TechnologyRepository
import org.springframework.data.domain.Page

import javax.inject.Inject
import javax.inject.Named


@Named @ViewScoped @ViewRef(config = [Views.Technology.List, Views.Technology.Show, Views.Technology.Save])
public class TechnologyController extends ScaffoldingController<Technology, Long> {

    @Inject TechnologyRepository repository
    @Inject Technology entity
    @Inject @Param Long id

    @Inject @Param int page
    @Inject @Param int size
    @Inject @Param String sort
    @Inject @Param String dir

    Page<Technology> entities

    Class<? extends ViewConfig> showView = Views.Technology.Show
    Class<? extends ViewConfig> listView = Views.Technology.List
    Class<? extends ViewConfig> saveView = Views.Technology.Save

    @PreRenderView protected void preRenderView() { get() }

    @Override @Secured(TechnologyController.class)
    Class<? extends ViewConfig> save() { super.save() }
}
