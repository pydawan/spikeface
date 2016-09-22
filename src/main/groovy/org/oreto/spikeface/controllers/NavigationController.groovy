package org.oreto.spikeface.controllers

import javax.enterprise.inject.Model

@Model
class NavigationController implements ApplicationController {
    String getTechnologyView() {
        getViewId(Pages.Technology.List)
    }
}
