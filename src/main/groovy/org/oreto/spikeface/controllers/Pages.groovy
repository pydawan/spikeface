package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.jsf.api.config.view.View
import org.apache.deltaspike.jsf.api.config.view.View.NavigationMode
import org.apache.deltaspike.jsf.api.config.view.View.ViewParameterMode

interface Pages {
    class Index implements ViewConfig { }

    @View(navigation = NavigationMode.REDIRECT, viewParams = ViewParameterMode.INCLUDE)
    interface RedirectedPages extends ViewConfig { }

    @View(navigation = NavigationMode.FORWARD, viewParams = ViewParameterMode.INCLUDE)
    interface ForwardedPages extends ViewConfig { }

    interface Technology extends ViewConfig
    {
        class List implements ViewConfig { }
        class Create implements ViewConfig { }
        class Show implements RedirectedPages { }
    }

    interface Error extends ViewConfig {
        class PageNotFound implements ViewConfig { }
    }
}