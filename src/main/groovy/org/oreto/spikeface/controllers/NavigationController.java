package org.oreto.spikeface.controllers;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@ApplicationScoped @Named
class NavigationController extends ApplicationController {

    String getTechListView() {
        return getViewId(Views.Technology.List.class);
    }

    String getTechSaveView() {
        return getViewId(Views.Technology.Save.class);
    }
}
