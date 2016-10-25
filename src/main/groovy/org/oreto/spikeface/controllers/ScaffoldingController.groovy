package org.oreto.spikeface.controllers

import org.apache.deltaspike.security.api.authorization.AccessDecisionVoter
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoterContext
import org.apache.deltaspike.security.api.authorization.SecurityViolation
import org.oreto.spikeface.models.BaseEntity
import org.primefaces.model.LazyDataModel
import org.primefaces.model.SortOrder
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

abstract class ScaffoldingController<T extends BaseEntity, ID extends Serializable> extends LazyDataModel<T>
        implements Scaffolding<T, ID>, AccessDecisionVoter {
    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        int offset = getFirst() + first
        int size = pageSize ?: DataPager.defaultSize
        int page = offset / size
        def sortColumn = sortField ?: sort
        def sortDir = sortOrder == SortOrder.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC

        if (first == 0 && entities) {
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
        if (!hasPermission('manage')) {
            violations.add(new SecurityViolation() {
                @Override
                String getReason() { "you don't have permission for resource ${entity.class.simpleName} ($id)" }
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
}
