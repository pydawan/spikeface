package org.oreto.spikeface.models

import org.apache.deltaspike.data.api.EntityRepository
import org.apache.deltaspike.data.api.Repository

@Repository
public interface TechnologyRepository extends EntityRepository<Technology, Long> {
    Technology findByName(String name)
}
