package org.oreto.spikeface.models

import org.apache.deltaspike.data.api.EntityRepository
import org.apache.deltaspike.data.api.Repository
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.jpa.repository.JpaRepository

import javax.persistence.Cacheable
import javax.persistence.Column
import javax.persistence.Entity

@Entity @Cacheable @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Technology extends BaseEntity<Long> implements Named {
    @Column @NotEmpty String name
    @Column @NotEmpty String versionName
}

public interface TechnologyRepository extends JpaRepository<Technology, Long> {
}

@Repository
public interface TechnologyData extends EntityRepository<Technology, Long> {
    Optional<Technology> findOptionalByName(String name)
}
