package org.oreto.spikeface.models

import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.jpa.repository.JpaRepository

import javax.enterprise.context.Dependent
import javax.persistence.Column
import javax.persistence.Entity

@Entity
public class Technology extends BaseEntity<Long> implements Named {
    @Column @NotEmpty String name
    @Column @NotEmpty String versionName
}

@Dependent
public interface TechnologyRepository extends JpaRepository<Technology, Long> {
    Optional<Technology> findByName(String name)
}
