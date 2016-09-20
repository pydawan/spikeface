package org.oreto.spikeface.models

import javax.persistence.Column

trait Named {

    @Column private String name

    public String getName() {
         name
    }
    public void setName(String name) {
        this.name = name
    }
}
