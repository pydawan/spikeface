package org.oreto.spikeface.services

import org.oreto.spikeface.models.Technology
import org.springframework.data.jpa.repository.JpaRepository

public interface TechnologyRepository extends JpaRepository<Technology, Long> {
}
