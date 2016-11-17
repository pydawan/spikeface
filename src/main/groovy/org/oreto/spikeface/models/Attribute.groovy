package org.oreto.spikeface.models

import org.apache.deltaspike.data.api.EntityRepository
import org.apache.deltaspike.data.api.Repository
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.envers.Audited
import org.hibernate.validator.constraints.NotEmpty
import org.oreto.spikeface.models.common.BaseEntity
import org.oreto.spikeface.models.common.Named
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.history.RevisionRepository

import javax.persistence.Cacheable
import javax.persistence.Column
import javax.persistence.Entity

@Entity @Cacheable @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Audited
class Attribute extends BaseEntity<Long> implements Named {
    @Column @NotEmpty String name
    @Column String value
}

public interface AttributeRepository extends RevisionRepository<Attribute, Long, Integer>, JpaRepository<Attribute, Long> {
    Attribute findOneByName(String name)
}

@Repository
public interface AttributeData extends EntityRepository<Attribute, Long> {
    Optional<Attribute> findOptionalByName(String name)
}
