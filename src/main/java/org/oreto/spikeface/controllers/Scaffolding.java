package org.oreto.spikeface.controllers;

import org.apache.deltaspike.core.api.config.view.ViewConfig;
import org.oreto.spikeface.models.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

interface Scaffolding<T extends BaseEntity, ID extends Serializable> {

    void setEntity(T entity);
    T getEntity();
    void setEntities(Page<T> entities);
    Page<T> getEntities();
    ID getId();
    JpaRepository<T, ID> getRepository();
    Class<? extends ViewConfig> getShowView();
    Class<? extends ViewConfig> getListView();
    Class<? extends ViewConfig> getSaveView();

    // pagination
    int getPage();
    int getSize();
    String getSort();
    String getDir();

    boolean isReadOnly();
    boolean isReadWrite();
    String getIdName();
    Page<T> list();
    int getCount();
}
