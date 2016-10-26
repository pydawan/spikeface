package org.oreto.spikeface.controllers;

import org.apache.deltaspike.core.api.config.view.ViewConfig;
import org.apache.deltaspike.core.api.config.view.metadata.ViewConfigResolver;
import org.apache.deltaspike.core.api.config.view.navigation.NavigationParameterContext;
import org.apache.deltaspike.core.api.config.view.navigation.ViewNavigationHandler;
import org.apache.deltaspike.core.util.StringUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoter;
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoterContext;
import org.apache.deltaspike.security.api.authorization.SecurityViolation;
import org.omnifaces.cdi.Param;
import org.oreto.spikeface.models.BaseEntity;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PermissionManager;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public abstract class ScaffoldingController<T extends BaseEntity, ID extends Serializable> extends LazyDataModel<T>
        implements Scaffolding<T, ID>, AccessDecisionVoter, ApplicationController {

    @Inject ViewNavigationHandler viewNavigationHandler;
    @Inject ViewConfigResolver viewConfigResolver;
    @Inject NavigationParameterContext navigationParameterContext;
    @Inject FacesContext facesContext;
    @Inject Identity identity;
    @Inject IdentityManager identityManager;
    @Inject PermissionManager permissionManager;

    @Inject @Param int page;
    @Inject @Param int size;
    @Inject @Param String sort;
    @Inject @Param String dir;

    @ManagedProperty("#{msg}") ResourceBundle bundle;

     @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        int offset = getFirst() + first;
        int size = pageSize == 0 ? DataPager.defaultSize: pageSize;
        int page = offset / size;
        String sortColumn = StringUtils.isEmpty(sortField) ? getSort() : sortField;
        Sort.Direction sortDir = sortOrder == SortOrder.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC;

        if (first == 0 && getEntities() != null) {
            this.setRowCount((int) getEntities().getTotalElements());
            return getEntities().getContent();
        } else {
            Page<T> result = StringUtils.isNotEmpty(sortColumn) ? getRepository().findAll(new PageRequest(page, size, sortDir, sortColumn)) :
                    getRepository().findAll(new PageRequest(page, size));
            this.setRowCount((int) result.getTotalElements());
            return result.getContent();
        }
    }

    public void get() throws IOException {
        if (isReadOnly() && Objects.equals(getRequestUrl(), getViewId(getSaveView()))) readOnly();
        else if (getId() != null) {
            setEntity(getRepository().findOne(getId()) );
            if (getEntity() == null) notFound();
        } else if (Objects.equals(getRequestUrl(), getViewId(getShowView())) && hasFacesError()) notFound();
    }

    public Class<? extends ViewConfig> edit() {
        navigationParameterContext.addPageParameter(getIdName(), getEntity().getId());
        return getSaveView();
    }

    @Transactional
    public Class<? extends ViewConfig> save() {
        if (isReadOnly()) return Views.Error.Readonly.class;
        else {
            T newEntity = getRepository().save(getEntity());
            setEntity(newEntity);
            navigationParameterContext.addPageParameter(getIdName(), newEntity.getId());
            return getShowView();
        }
    }

    int getFirst() {
        int page = getPage() == 0 ? DataPager.defaultPage : getPage();
        int size = getSize() == 0 ? DataPager.defaultSize : getSize();
        return (page - 1) * size;
    }

    @Transactional
    public Class<? extends ViewConfig> delete() throws IOException {
        if (getEntity().isTransient() || isReadOnly()) notFound();
        else if (isReadOnly()) readOnly();
        else {
            getRepository().delete((ID) getEntity().getId());
            return getListView();
        }
        return null;
    }

    public Class<? extends ViewConfig> cancel() {
        if (getEntity().isTransient()) return getListView();
        else {
            navigationParameterContext.addPageParameter(getIdName(), getId());
            return getShowView();
        }
    }

    public boolean isReadOnly() { return false; }
    public boolean isReadWrite() { return !isReadOnly(); }
    public String getIdName() { return "id"; }

    public Page<T> list() {
        int page = getPage() == 0 ? DataPager.defaultPage: getPage();
        int size = getSize() == 0 ? DataPager.defaultSize: getSize();
        Sort.Direction direction = Objects.equals(DataHeader.ascendingOrder, getDir()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        if (getSort() != null && StringUtils.isNotEmpty(getSort()))
            setEntities(getRepository().findAll(new PageRequest(page - 1, size, direction, getSort())) );
        else
            setEntities(getRepository().findAll(new PageRequest(page - 1, size)) );

        return getEntities();
    }

    public int getCount() {
        return (int) getRepository().count();
    }

    @Override
    public Set<SecurityViolation> checkPermission(AccessDecisionVoterContext accessDecisionVoterContext) {
        List<SecurityViolation> violations = new ArrayList<>();
        if (!hasPermission("manage")) {
            violations.add((SecurityViolation) () ->
                    String.format("you don't have permission for resource %s %s",
                            getEntity().getClass().getSimpleName(), getId()));
        }
        return new HashSet<>(violations);
    }

    public boolean hasPermission(String action) {
        return !(getEntity() != null && getId() != null) || identity.hasPermission(getEntity().getClass(), getId(), action);
    }

    @Override
    public ViewNavigationHandler getViewNavigationHandler() {
        return viewNavigationHandler;
    }

    @Override
    public ViewConfigResolver getViewConfigResolver() {
        return viewConfigResolver;
    }

    @Override
    public NavigationParameterContext getNavigationParameterContext() {
        return navigationParameterContext;
    }

    @Override
    public FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    public Identity getIdentity() {
        return identity;
    }

    @Override
    public IdentityManager getIdentityManager() {
        return identityManager;
    }

    @Override
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getSort() {
        return sort;
    }

    @Override
    public String getDir() {
        return dir;
    }
}
