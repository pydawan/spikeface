package org.oreto.spikeface.controllers

import com.ocpsoft.pretty.PrettyContext
import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.core.api.config.view.metadata.ViewConfigResolver
import org.apache.deltaspike.core.api.config.view.navigation.NavigationParameterContext
import org.apache.deltaspike.core.api.config.view.navigation.ViewNavigationHandler
import org.apache.deltaspike.jpa.api.transaction.Transactional
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoter
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoterContext
import org.apache.deltaspike.security.api.authorization.SecurityViolation
import org.omnifaces.util.Servlets
import org.oreto.spikeface.models.BaseEntity
import org.oreto.spikeface.utils.Utils
import org.picketlink.Identity
import org.primefaces.model.LazyDataModel
import org.primefaces.model.SortOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository

import javax.faces.application.FacesMessage
import javax.faces.context.FacesContext
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

trait ApplicationController implements Serializable {
    @Inject ViewNavigationHandler viewNavigationHandler
    @Inject ViewConfigResolver viewConfigResolver
    @Inject NavigationParameterContext navigationParameterContext
    @Inject FacesContext facesContext
    @Inject Identity identity

    public String getViewId(Class<? extends ViewConfig> view) {
        viewConfigResolver.getViewConfigDescriptor(view).viewId
    }

    public void render(Class<? extends ViewConfig> view) {
        Utils.render(getViewId(view))
    }

    public void navigate(Class<? extends ViewConfig> view) {
        viewNavigationHandler.navigateTo(view)
    }

    public void redirect(String url) {
        Servlets.facesRedirect(getRequest(), getResponse(), url)
    }

    public void redirect(Class<? extends ViewConfig> view, String queryString) {
        redirect("${getBaseUrl()}${getViewId(view)}?$queryString")
    }

    public void notFound() {
        render(Views.Error.Notfound)
    }

    public void readOnly() {
        render(Views.Error.Readonly)
    }

    public HttpServletRequest getRequest() {
        facesContext.externalContext.request as HttpServletRequest
    }

    public PrettyContext getPretty() {
        PrettyContext.currentInstance
    }

    public HttpServletResponse getResponse() {
        facesContext.externalContext.response as HttpServletResponse
    }

    public String getRequestUrlWithQueryString() {
        "${getBaseUrl()}${getRequestUrl()}${pretty.requestQueryString.toString()}"
    }

    public String getRequestUrl() {
        pretty.getRequestURL().toString()
    }

    public String getBaseUrl() {
        getRequest().contextPath
    }

    public boolean hasFacesError() {
        for(FacesMessage message : facesContext.messageList) {
            if(message.severity == FacesMessage.SEVERITY_ERROR) return true
        }
        false
    }
}

trait Scaffolding<T extends BaseEntity, ID extends Serializable> extends ApplicationController {

    abstract void setEntity(T entity)
    abstract T getEntity()
    abstract void setEntities(Page<T> entities)
    abstract Page<T> getEntities()
    abstract ID getId()
    abstract JpaRepository<T, ID> getRepository()
    abstract Class<? extends ViewConfig> getShowView()
    abstract Class<? extends ViewConfig> getListView()
    abstract Class<? extends ViewConfig> getSaveView()

    // pagination
    abstract int getPage()
    abstract int getSize()
    abstract String getSort()
    abstract String getDir()

    boolean isReadOnly() { false }
    boolean isReadWrite() { !isReadOnly() }

    public getIdName() { 'id' }

    public void get() {
        if(isReadOnly() && requestUrl == getViewId(saveView)) readOnly()
        else if(id)  {
            entity = repository.findOne(id)
            if(!entity) notFound()
        } else if(requestUrl == getViewId(showView) && hasFacesError()) notFound()
    }

    public Page<T> list() {
        int page = page ?: DataPager.defaultPage
        int size = size ?: DataPager.defaultSize
        def direction = DataHeader.ascendingOrder == dir ? Sort.Direction.ASC : Sort.Direction.DESC
        if(sort)
            entities = repository.findAll(new PageRequest(page - 1, size, direction, sort))
        else
            entities = repository.findAll(new PageRequest(page - 1, size))
    }

    public int getCount() {
        repository.count()
    }

    public Class<? extends ViewConfig> edit() {
        navigationParameterContext.addPageParameter(idName, entity.id)
        saveView
    }

    @Transactional
    public Class<? extends ViewConfig> save() {
        if(isReadOnly()) Views.Error.Readonly
        else {
            entity = repository.save(entity)
            navigationParameterContext.addPageParameter(idName, entity.id)
            showView
        }
    }

    @Transactional
    public Class<? extends ViewConfig> delete() {
        if(entity?.isTransient() || isReadOnly()) notFound()
        else if(isReadOnly()) readOnly()
        else {
            repository.delete(entity.id as ID)
            listView
        }
    }

    public Class<? extends ViewConfig> cancel() {
        if(entity?.isTransient()) listView
        else {
            navigationParameterContext.addPageParameter(idName, id)
            showView
        }
    }
}

abstract class ScaffoldingController<T extends BaseEntity, ID extends Serializable> extends LazyDataModel<T>
        implements Scaffolding<T, ID>, AccessDecisionVoter {
    @Override public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {
        int offset = getFirst() + first
        int size = pageSize ?: DataPager.defaultSize
        int page = offset / size
        def sortColumn = sortField ?: sort
        def sortDir = sortOrder == SortOrder.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC

        if(first == 0 && entities) {
            this.setRowCount(entities.totalElements as int)
            entities.content
        } else {
            def result = sortColumn ? repository.findAll(new PageRequest(page, size, sortDir, sortColumn)) :
                    repository.findAll(new PageRequest(page, size))
            this.setRowCount(result.totalElements as int)
            result.content
        }
    }

    int getFirst() {
        int page = page ?: DataPager.defaultPage
        int size = this.size ?: DataPager.defaultSize
        int first = (page - 1) * size
        first
    }

    @Override
    Set<SecurityViolation> checkPermission(AccessDecisionVoterContext accessDecisionVoterContext) {
        List<SecurityViolation> violations = []
        if(entity && id && !identity.hasPermission(entity.class, id, "manage")) {
            violations.add(new SecurityViolation() {@Override String getReason() { "you don't have permission for resource ${entity.class.simpleName} - $id" }})
        }
        violations
    }
}

