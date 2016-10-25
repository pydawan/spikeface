package org.oreto.spikeface.controllers;

import org.oreto.spikeface.utils.UrlEncodedQueryString;
import org.oreto.spikeface.utils.Utils;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

class DataHeader extends DataTableRenderer {

    static String defaultDirection = "desc";
    static String ascendingOrder = "asc";
    static String sortParamName = "sort";
    static String dirParamName = "dir";

    @Override
    public void encodeColumnHeader(FacesContext context, DataTable table, UIColumn column) throws IOException {

        Map<String, String> requestMap = context.getExternalContext().getRequestParameterMap();
        UrlEncodedQueryString queryString = Utils.newQueryString(context);
        queryString.remove(sortParamName).remove(dirParamName);

        String sort = requestMap.get(sortParamName);
        String dir = requestMap.getOrDefault(dirParamName, defaultDirection);

        String sortIconClass = "ui-icon-carat-2-n-s";
        String text = column.getHeaderText();
        String field = column.getField();
        queryString.set(sortParamName, field);
        if (Objects.equals(sort, field)) {
            sortIconClass = Objects.equals(dir, ascendingOrder) ? "ui-icon-triangle-1-n" : "ui-icon-triangle-1-s";
            queryString.set(dirParamName, Objects.equals(dir, ascendingOrder) ? defaultDirection : ascendingOrder);
        } else {
            queryString.set(dirParamName, defaultDirection);
        }
        String sortDirection = Objects.equals(dir, ascendingOrder) ? "ascending" : "descending";

        ResponseWriter writer = context.getResponseWriter();
        String header = String.format("<th class='ui-state-default ui-state-hover' role='columnheader'" +
"aria-label='%s: activate to sort column %s' onclick='window.location.href='%s';'" +
 "scope='col' tabindex='0' aria-sort='other'><span class='ui-column-title'>%s</span>" +
                "<span class='ui-sortable-column-icon ui-icon %s'></span></th>",
                text, sortDirection, queryString, text, sortIconClass);
        writer.write(header);
    }
}
