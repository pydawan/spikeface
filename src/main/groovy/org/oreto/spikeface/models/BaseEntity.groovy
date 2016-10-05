package org.oreto.spikeface.models

import javax.persistence.*

@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class BaseEntity<P extends Serializable> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    P id

    @Version @Column
    Integer version

    public boolean isTransient() {
        this.version == null
    }
}
