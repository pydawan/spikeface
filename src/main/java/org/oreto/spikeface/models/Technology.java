package org.oreto.spikeface.models;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Technology extends BaseEntity<Long> {

    @Column private String name;
    @Column private String versionName;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getVersionName() {
        return versionName;
    }
    public void setVersionName(String version) {
        this.versionName = version;
    }
}
