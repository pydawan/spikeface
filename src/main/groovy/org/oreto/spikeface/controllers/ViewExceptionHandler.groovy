package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.config.view.metadata.ViewConfigResolver
import org.apache.deltaspike.core.api.exception.control.ExceptionHandler
import org.apache.deltaspike.core.api.exception.control.Handles
import org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent
import org.apache.deltaspike.security.api.authorization.ErrorViewAwareAccessDeniedException
import org.codehaus.groovy.runtime.DateGroovyMethods
import org.omnifaces.util.Messages
import org.oreto.spikeface.utils.Utils

import javax.enterprise.inject.Model
import javax.inject.Inject

@ExceptionHandler
@Model
public class ViewExceptionHandler {
    @Inject private ViewConfigResolver viewConfigResolver

    Throwable exception
    String time
    String type
    String message
    String stackTrace

    public void onAccessDeniedException(@Handles ExceptionEvent<ErrorViewAwareAccessDeniedException> event) {
        exception = event.exception
        String viewId = viewConfigResolver.getViewConfigDescriptor(Views.Login).viewId
        Utils.render(viewId)
        event.handled()
    }

    public void onException(@Handles ExceptionEvent<Exception> event) {
        exception = event.exception
        this.time = DateGroovyMethods.format(new Date(), "MM-dd-yyyy hh:mm:ss a")
        this.type = exception.class.typeName
        this.message = exception.message

        StringWriter sw = new StringWriter()
        PrintWriter pw = new PrintWriter(sw)
        exception.printStackTrace(pw)
        this.stackTrace = Utils.escapeXmlWithBreaks(sw.toString())
        String viewId = viewConfigResolver.getViewConfigDescriptor(Views.Error.Server).viewId

        Messages.addGlobalError(message)
        Utils.render(viewId)
        event.handled()
    }
}

