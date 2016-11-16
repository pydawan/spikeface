package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewRef
import org.apache.deltaspike.core.api.config.view.controller.PreRenderView
import org.omnifaces.cdi.Param
import org.omnifaces.cdi.ViewScoped
import org.oreto.spikeface.controllers.common.SimpleScaffoldingController
import org.oreto.spikeface.controllers.common.Views
import org.oreto.spikeface.models.Attribute
import org.oreto.spikeface.models.AttributeRepository

import javax.inject.Inject
import javax.inject.Named

@Named @ViewScoped @ViewRef(config = Views.Attribute.List)
public class AttributeController extends SimpleScaffoldingController<Attribute, Long> {

    @Inject AttributeRepository repository
    @Inject Attribute entity
    @Inject @Param Long id

    @Inject @Param int page
    @Inject @Param int size
    @Inject @Param String sort
    @Inject @Param String dir

    List<Attribute> entities
    @Override Attribute newEntity() { new Attribute()}

    @PreRenderView protected void preRenderView() { get() }
}
