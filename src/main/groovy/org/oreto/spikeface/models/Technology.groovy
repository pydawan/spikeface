package org.oreto.spikeface.models

import org.apache.deltaspike.data.api.Repository
import org.hibernate.validator.constraints.NotEmpty

import javax.persistence.Column
import javax.persistence.Entity

@Entity
public class Technology extends BaseEntity<Long> implements Named {
    @Column @NotEmpty String name
    @Column @NotEmpty String versionName
}

@Repository
public interface TechnologyRepository extends BaseEntityRepo<Technology, Long> {
    Optional<Technology> findByName(String name)
}
