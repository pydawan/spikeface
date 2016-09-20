package org.oreto.spikeface.models

import javax.persistence.Column
import javax.persistence.Entity

@Entity
public class Technology extends BaseEntity<Long> implements Named {

    @Column String versionName
}
