package org.oreto.spikeface.services;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.oreto.spikeface.models.Technology;

import java.util.Optional;

@Repository
public interface TechnologyData extends EntityRepository<Technology, Long> {
    Optional<Technology> findOptionalByName(String name);
}
