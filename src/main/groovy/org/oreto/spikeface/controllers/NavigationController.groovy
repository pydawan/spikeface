package org.oreto.spikeface.controllers


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
}
