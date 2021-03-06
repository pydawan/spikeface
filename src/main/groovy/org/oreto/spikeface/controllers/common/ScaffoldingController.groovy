package org.oreto.spikeface.controllers.common

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.jpa.api.transaction.Transactional
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoter
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoterContext
import org.apache.deltaspike.security.api.authorization.SecurityViolation
import org.oreto.spikeface.models.common.BaseEntity
import org.oreto.spikeface.utils.Utils
import org.primefaces.event.CellEditEvent
import org.primefaces.model.LazyDataModel
import org.primefaces.model.SortOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository

import java.text.MessageFormat

abstract class ScaffoldingController<T extends BaseEntity, ID extends Serializable> extends LazyDataModel<T>
        implements Scaffolding<T, ID>, Pageable, AccessDecisionVoter {

    abstract Class<? extends ViewConfig> getShowView()
    abstract Class<? extends ViewConfig> getListView()
    abstract Class<? extends ViewConfig> getSaveView()

    abstract ID getId()
    abstract void setId(ID id)
    abstract void setEntities(Page<T> entities)
    abstract Page<T> getEntities()

    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        int offset = getFirst() + first
        int size = pageSize ?: Pageable.defaultSize
        int page = offset / size
        def sortColumn = sortField ?: sort
        def sortDir = sortOrder == SortOrder.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC

        if (first == 0 as int && entities) {
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
        int page = page ?: Pageable.defaultPage
        int size = this.size ?: Pageable.defaultSize
        int first = (page - 1) * size
        first
    }

    @Override
    Set<SecurityViolation> checkPermission(AccessDecisionVoterContext accessDecisionVoterContext) {
        List<SecurityViolation> violations = []
        if (!hasPermission('manage')) {
            violations.add(new SecurityViolation() {
                @Override
                String getReason() {
                    MessageFormat.format(bundle.getString('permissionError'), entity.class.simpleName, entity.id)
                }
            })
        }
        violations
    }

    boolean hasPermission(String action) {
        if (entity && id) {
            identity.hasPermission(entity.class, id, action)
        } else {
            true
        }
    }

    public void get() {
        if(requestEqualsView(listView)) {
            entities = list()
        } else if(isReadOnly() && requestEqualsView(saveView)) readOnly()
        else if(id != null)  {
            entity = repository.findOne(id)
            if(!entity) notFound()
        } else if(requestEqualsView(showView) && hasFacesError()) notFound()
    }

    public Page<T> list() {
        int page = page ?: Pageable.defaultPage
        int size = size ?: Pageable.defaultSize
        def direction = DataHeader.ascendingOrder == dir ? Sort.Direction.ASC : Sort.Direction.DESC
        if(sort)
            entities = repository.findAll(new PageRequest(page - 1, size, direction, sort))
        else
            entities = repository.findAll(new PageRequest(page - 1, size))
    }

    public Class<? extends ViewConfig> show(ID id) {
        this.id = id
        navigationParameterContext.addPageParameter(idName, id)
        showView
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
            id = entity.id as ID
            navigationParameterContext.addPageParameter(idName, entity.id)
            showView
        }
    }

    @Transactional
    public Class<? extends ViewConfig> delete() {
        if(id == null || isReadOnly()) notFound()
        else if(isReadOnly()) readOnly()
        else {
            repository.delete(entity.id as ID)

            int newTotal = total - 1
            int lastPage = newTotal / size + (newTotal % size > 0 ? 1 : 0)
            if(page > lastPage) page = lastPage
            addPagerParams()
            listView
        }
    }

    public Class<? extends ViewConfig> cancel() {
        if(id == null) {
            addPagerParams()
            listView
        } else {
            navigationParameterContext.addPageParameter(idName, id)
            showView
        }
    }

    public Class<? extends ViewConfig> back() {
        addPagerParams()
        listView
    }

    @Override public int getTotal() { entities?.totalElements ?: 0 }

    @Override Class<? extends ViewConfig> next() {
        page = page + 1
        addPagerParams()
        listView
    }

    @Override Class<? extends ViewConfig> last() {
        page = totalPages
        addPagerParams()
        listView
    }

    @Override Class<? extends ViewConfig> previous() {
        page = page - 1
        addPagerParams()
        listView
    }

    @Override Class<? extends ViewConfig> first() {
        page = 1
        addPagerParams()
        listView
    }

    @Override Class<? extends ViewConfig> page(int page) {
        this.page = page
        addPagerParams()
        listView
    }

    @Override void sizeChanged() {
        page = 1
    }

    @Override String getDefaultUrl() {
        Utils.newQueryString(facesContext, pageParamName, sizeParamName)
    }

    def sortBy() {
        sort = getParam(DataHeader.sortParamName)
        dir = getParam(DataHeader.dirParamName)
    }

    def void addPagerParams() {
        if(page > 0) navigationParameterContext.addPageParameter(pageParamName, page)
        if(size > 0) navigationParameterContext.addPageParameter(sizeParamName, size)
        if(sort) {
            navigationParameterContext.addPageParameter(sortParamName, sort)
            if(dir) navigationParameterContext.addPageParameter(dirParamName, dir)
        }
    }
}

abstract class SimpleScaffoldingController<T extends BaseEntity, ID extends Serializable>
        implements Scaffolding<T, ID> {

    abstract void setEntities(List<T> entities)
    abstract List<T> getEntities()
    abstract T newEntity()

    @Override
    public void get() {
        entities = list()
    }

    public List<T> list() {
        repository.findAll()
    }

    @Transactional
    public void onCellEdit(CellEditEvent event) {
        repository.save(entities.get(event.rowIndex))
    }

    @Transactional
    public void delete(T entity) {
        repository.delete(entity.id as ID)
    }

    @Transactional
    public void save() {
        repository.save(entity)
        entity = newEntity()
    }

    boolean hasPermission(T entity, String action) {
        if (entity) {
            identity.hasPermission(entity.class, entity.id, action)
        } else {
            true
        }
    }
}

trait Scaffolding<T extends BaseEntity, ID extends Serializable> implements ApplicationController {

    abstract void setEntity(T entity)
    abstract T getEntity()

    abstract JpaRepository<T, ID> getRepository()
    abstract void get()

    boolean isReadOnly() { false }
    boolean isReadWrite() { !isReadOnly() }

    public String getIdName() { 'id' }
    public int getCount() {
        repository.count()
    }
}

trait Pageable {
    static Integer defaultPage = 1
    static Integer defaultSize = 10
    static String pageParamName = 'page'
    static String sizeParamName = 'size'
    static String sortParamName = 'sort'
    static String dirParamName = 'dir'

    abstract int getTotal()
    abstract int getPage()
    abstract void setPage(int page)
    abstract int getSize()
    abstract void setSize(int size)
    abstract String getSort()
    abstract void setSort(String sort)
    abstract String getDir()
    abstract void setDir(String dir)
    abstract Class<? extends ViewConfig> next()
    abstract Class<? extends ViewConfig> last()
    abstract Class<? extends ViewConfig> page(int page)
    abstract Class<? extends ViewConfig> previous()
    abstract Class<? extends ViewConfig> first()
    abstract String getDefaultUrl()
    abstract void sizeChanged()

    List getSizeOptions() {
        ([size] + [5, 10, 20, 50]).unique()
    }

    int getTotalPages(){
        int size = size ?: defaultSize
        total / size + (total % size > 0 ? 1 : 0)
    }

    List getPages() {
        int page = page ?: defaultPage
        int maxPages = 6
        int firstPage
        int lastPage
        if(maxPages < totalPages) {
            int right = page
            int left = page

            while (right - left < maxPages - 1){
                if (left > 1) left--
                if (right - left < maxPages - 1 && right < totalPages) right++
            }
            firstPage = left
            lastPage = right
        } else {
            firstPage = 1
            lastPage = totalPages
        }
        (firstPage..lastPage)
    }
}



