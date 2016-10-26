package org.oreto.spikeface.controllers;

import org.apache.deltaspike.core.api.config.view.metadata.ViewConfigResolver;
import org.apache.deltaspike.core.api.exception.control.ExceptionHandler;
import org.apache.deltaspike.core.api.exception.control.Handles;
import org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent;
import org.apache.deltaspike.security.api.authorization.AccessDeniedException;
import org.apache.deltaspike.security.api.authorization.ErrorViewAwareAccessDeniedException;
import org.omnifaces.util.Messages;
import org.oreto.spikeface.utils.Utils;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

@ExceptionHandler
@Model
public class ViewExceptionHandler {
    @Inject private ViewConfigResolver viewConfigResolver;

    private Throwable exception;
    private String time;
    private String type;
    private String message;
    private String stackTrace;

    public void onErrorViewAccessDeniedException(@Handles ExceptionEvent<ErrorViewAwareAccessDeniedException> event) {
        String viewId = viewConfigResolver.getViewConfigDescriptor(event.getException().getErrorView()).getViewId();
        try {
            Utils.render(viewId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        event.handled();
    }

    public void onAccessDeniedException(@Handles ExceptionEvent<AccessDeniedException> event) {
        String viewId = viewConfigResolver.getViewConfigDescriptor(Views.Error.Server.class).getViewId();
        try {
            Utils.render(viewId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        event.handled();
    }

    public void onException(@Handles ExceptionEvent<Exception> event) {
        exception = event.getException();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
        this.time = dateFormat.format(new Date());
        this.type = exception.getClass().getTypeName();
        this.message = exception.getMessage();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        this.stackTrace = Utils.escapeXmlWithBreaks(sw.toString());
        String viewId = viewConfigResolver.getViewConfigDescriptor(Views.Error.Server.class).getViewId();

        Messages.addGlobalError(message);
        try {
            Utils.render(viewId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        event.handled();
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}

