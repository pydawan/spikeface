package org.oreto.spikeface.models

import org.apache.deltaspike.data.api.EntityRepository
import org.apache.deltaspike.data.api.Repository

import javax.persistence.Column
import javax.persistence.Entity

@Entity
public class Technology extends BaseEntity<Long> implements Named {
    @Column String versionName
}

@Repository
public interface TechnologyRepository extends EntityRepository<Technology, Long> {
    Technology findByName(String name)
}
