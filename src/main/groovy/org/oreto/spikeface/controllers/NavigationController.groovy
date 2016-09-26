package org.oreto.spikeface.controllers

import javax.enterprise.context.ApplicationScoped
import javax.inject.Named

@ApplicationScoped @Named
class NavigationController implements ApplicationController {

    String getTechnologyView() {
        getViewId(Pages.Technology.List)
    }
}
