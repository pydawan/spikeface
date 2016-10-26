package org.oreto.spikeface.controllers;

import org.apache.deltaspike.core.api.config.view.ViewConfig;
import org.apache.deltaspike.core.api.config.view.ViewRef;
import org.apache.deltaspike.core.api.config.view.controller.PreRenderView;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.apache.deltaspike.security.api.authorization.Secured;
import org.omnifaces.cdi.Param;
import org.omnifaces.cdi.ViewScoped;
import org.oreto.spikeface.models.Technology;
import org.oreto.spikeface.services.TechnologyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;


@Named @ViewScoped @ViewRef(config = {Views.Technology.List.class, Views.Technology.Show.class, Views.Technology.Save.class})
public class TechnologyController extends ScaffoldingController<Technology, Long> {

    @Inject TechnologyRepository repository;
    @Inject Technology entity;
    @Inject @Param Long id;

    private Page<Technology> entities;

    private Class<? extends ViewConfig> showView = Views.Technology.Show.class;
    private Class<? extends ViewConfig> listView = Views.Technology.List.class;
    private Class<? extends ViewConfig> saveView = Views.Technology.Save.class;

    @PreRenderView protected void preRenderView() throws IOException { get(); }

    @Override @Secured(TechnologyController.class) @Transactional
    public Class<? extends ViewConfig> save() { return super.save(); }

    @Override public void setEntity(Technology entity) { this.entity = entity; }
    @Override public Technology getEntity() { return entity; }
    @Override public void setEntities(Page<Technology> entities) {this.entities = entities;}
    @Override public Page<Technology> getEntities() {return entities;}
    @Override public Long getId() {
        return id;
    }
    @Override public JpaRepository<Technology, Long> getRepository() {
        return repository;
    }
    @Override public Class<? extends ViewConfig> getShowView() {
        return showView;
    }
    @Override public Class<? extends ViewConfig> getListView() {
        return listView;
    }
    @Override public Class<? extends ViewConfig> getSaveView() {
        return saveView;
    }
}
