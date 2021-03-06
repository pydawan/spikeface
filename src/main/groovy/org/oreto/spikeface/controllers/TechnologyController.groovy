package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.core.api.config.view.ViewRef
import org.apache.deltaspike.core.api.config.view.controller.PreRenderView
import org.apache.deltaspike.core.api.scope.ViewAccessScoped
import org.apache.deltaspike.jpa.api.transaction.Transactional
import org.apache.deltaspike.security.api.authorization.Secured
import org.omnifaces.cdi.Param
import org.oreto.spikeface.controllers.common.ScaffoldingController
import org.oreto.spikeface.controllers.common.Views
import org.oreto.spikeface.models.Technology
import org.oreto.spikeface.models.TechnologyRepository
import org.springframework.data.domain.Page

import javax.inject.Inject
import javax.inject.Named

@Named @ViewAccessScoped @ViewRef(config = [Views.Technology.List, Views.Technology.Show, Views.Technology.Save])
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

    @Override int getPage() { page > 0 ? page : defaultPage }
    @Override int getSize() { size > 0 ? size : defaultSize }

    @Override @Secured(TechnologyController.class) @Transactional
    Class<? extends ViewConfig> save() {
        def view = super.save()
        permissionManager.grantPermission(identity.getAccount(), entity, 'manage')
        view
    }
}
