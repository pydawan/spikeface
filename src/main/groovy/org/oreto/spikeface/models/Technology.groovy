package org.oreto.spikeface.models

import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.repository.PagingAndSortingRepository

import javax.persistence.Column
import javax.persistence.Entity

@Entity
public class Technology extends BaseEntity<Long> implements Named {
    @Column @NotEmpty String name
    @Column @NotEmpty String versionName
}

public interface TechnologyRepository extends PagingAndSortingRepository<Technology, Long> {
    Optional<Technology> findByName(String name)
}
