package org.oreto.spikeface.controllers

import javax.enterprise.context.ApplicationScoped
import javax.inject.Named

@ApplicationScoped @Named
class NavigationController extends ApplicationController {

    String getTechnologyView() {
        getViewId(Views.Technology.List)
    }
}
