package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.navigation.NavigationParameterContext
import org.apache.deltaspike.core.api.config.view.navigation.ViewNavigationHandler

import javax.inject.Inject


trait ApplicationController {
    @Inject ViewNavigationHandler viewNavigationHandler
    @Inject NavigationParameterContext navigationParameterContext

    Long id

    public void notFound() {
        this.viewNavigationHandler.navigateTo(Pages.Error.PageNotFound)
    }
}
