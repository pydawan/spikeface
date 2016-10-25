package org.oreto.spikeface.models

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.validator.constraints.NotEmpty

import javax.persistence.Cacheable
import javax.persistence.Column
import javax.persistence.Entity

@Entity @Cacheable @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Technology extends BaseEntity<Long> implements Named {
    @Column @NotEmpty String name
    @Column @NotEmpty String versionName
}


