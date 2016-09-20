package org.oreto.spikeface.models;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class BaseEntity<P extends Serializable> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private P id;

    @Version @Column
    private Integer version;

    public P getId() {
        return id;
    }
    public void setId(P id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }
    public void setVersion(final Integer version) {
        this.version = version;
    }

    public boolean isTransient() {
        return this.version == null;
    }
}
