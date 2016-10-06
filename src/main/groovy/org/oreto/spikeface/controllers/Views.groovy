package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.DefaultErrorView
import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.jsf.api.config.view.View
import org.apache.deltaspike.jsf.api.config.view.View.NavigationMode
import org.apache.deltaspike.jsf.api.config.view.View.ViewParameterMode

interface Views {
    class Index implements ViewConfig { }

    @View(navigation = NavigationMode.REDIRECT, viewParams = ViewParameterMode.INCLUDE)
    interface RedirectedPages extends ViewConfig { }

    @View(navigation = NavigationMode.FORWARD, viewParams = ViewParameterMode.INCLUDE)
    interface ForwardedPages extends ViewConfig { }

    interface Technology extends ViewConfig
    {
        class List implements RedirectedPages { }
        class Save implements RedirectedPages { }
        class Show implements RedirectedPages { }
    }

    interface Error extends ViewConfig {
        class Readonly implements ForwardedPages { }
        class Notfound implements ForwardedPages { }
        class Server extends DefaultErrorView implements ForwardedPages { }
    }
}