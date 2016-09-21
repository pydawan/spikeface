package org.oreto.spikeface.controllers

import org.apache.deltaspike.core.api.exception.control.ExceptionHandler
import org.apache.deltaspike.core.api.exception.control.Handles
import org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent

@ExceptionHandler
public class ApplicationExceptionHandler
{
    public void handleNotFoundException(@Handles ExceptionEvent<Exception> event) {
        //FacesContext facesContext = FacesContext.currentInstance
        //        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                "Page not found",
//                event.getException().getMessage()))
//        event.handledAndContinue()
    }
}
