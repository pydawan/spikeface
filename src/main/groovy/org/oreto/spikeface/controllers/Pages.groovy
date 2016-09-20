package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.jsf.api.config.view.View
import org.apache.deltaspike.jsf.api.config.view.View.NavigationMode
import org.apache.deltaspike.jsf.api.config.view.View.ViewParameterMode

interface Pages {
    class Index implements ViewConfig { }
    interface Technology extends ViewConfig
    {
        @View(navigation = NavigationMode.REDIRECT, viewParams = ViewParameterMode.INCLUDE)
        interface RedirectedPages extends ViewConfig { }

        class List implements ViewConfig { }
        class Create implements ViewConfig { }
        class Show implements RedirectedPages { }
    }
}