package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.core.api.config.view.ViewRef
import org.apache.deltaspike.core.api.config.view.controller.PreRenderView
import org.omnifaces.cdi.Param
import org.omnifaces.cdi.ViewScoped
import org.oreto.spikeface.models.Technology
import org.oreto.spikeface.services.TechnologyService

import javax.inject.Inject
import javax.inject.Named
import javax.mvc.Models
import javax.mvc.annotation.Controller
import javax.ws.rs.GET
import javax.ws.rs.Path

@Named @ViewScoped @ViewRef(config = [Views.Technology.List, Views.Technology.Show, Views.Technology.Save])
@Controller @Path('technology')
public class TechnologyController extends Scaffolding<Technology, Long> {

    @Inject TechnologyService repository
    @Inject Technology entity
    @Inject @Param Long id

    @Inject @Param int page
    @Inject @Param int size
    @Inject @Param String sort
    @Inject @Param String dir

    @Inject Models models

    Iterable<Technology> entities

    Class<? extends ViewConfig> showView = Views.Technology.Show
    Class<? extends ViewConfig> listView = Views.Technology.List
    Class<? extends ViewConfig> saveView = Views.Technology.Save

    @PreRenderView protected void preRenderView() { get() }

    @Override @GET
    public String list() {
        super.list()
    }
}
