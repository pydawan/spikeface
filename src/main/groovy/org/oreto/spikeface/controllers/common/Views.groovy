package org.oreto.spikeface.controllers.common

import org.apache.deltaspike.core.api.config.view.DefaultErrorView
import org.apache.deltaspike.core.api.config.view.ViewConfig
import org.apache.deltaspike.jsf.api.config.view.View
import org.apache.deltaspike.jsf.api.config.view.View.NavigationMode
import org.apache.deltaspike.security.api.authorization.Secured
import org.oreto.spikeface.controllers.TechnologyController

interface Views {
    @View(navigation = NavigationMode.REDIRECT, viewParams = View.ViewParameterMode.INCLUDE)
    interface RedirectedPages extends ViewConfig { }

    @View(navigation = NavigationMode.FORWARD, viewParams = View.ViewParameterMode.INCLUDE)
    interface ForwardedPages extends ViewConfig { }

    class Index implements ViewConfig { }
    class Login implements ViewConfig { }

    interface Error extends ViewConfig {
        class Readonly implements ForwardedPages { }
        class Notfound implements ForwardedPages { }
        class Server extends DefaultErrorView { }
    }

    @Secured(value = LoginController.class, errorView = Login)
    interface Technology extends ViewConfig {
        class List implements RedirectedPages { }
        @Secured(TechnologyController.class)
        class Save implements RedirectedPages { }
        class Show implements RedirectedPages { }
    }
}