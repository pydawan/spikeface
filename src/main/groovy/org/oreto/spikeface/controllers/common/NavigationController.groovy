package org.oreto.spikeface.controllers.common


import javax.enterprise.context.ApplicationScoped
import javax.inject.Named

@ApplicationScoped @Named
class NavigationController implements ApplicationController {

    String getTechListView() {
        getViewId(Views.Technology.List)
    }

    String getTechSaveView() {
        getViewId(Views.Technology.Save)
    }

    String getAttributeListView() {
        getViewId(Views.Attribute.List)
    }
}
