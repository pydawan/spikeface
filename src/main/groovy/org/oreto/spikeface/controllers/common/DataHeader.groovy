package org.oreto.spikeface.controllers.common

import org.oreto.spikeface.utils.UrlEncodedQueryString
import org.oreto.spikeface.utils.Utils
import org.primefaces.component.api.UIColumn
import org.primefaces.component.datatable.DataTable
import org.primefaces.component.datatable.DataTableRenderer

import javax.faces.context.FacesContext
import javax.faces.context.ResponseWriter

class DataHeader extends DataTableRenderer {

    static String defaultDirection = 'desc'
    static String ascendingOrder = 'asc'
    static String sortParamName = 'sort'
    static String dirParamName = 'dir'

    @Override
    public void encodeColumnHeader(FacesContext context, DataTable table, UIColumn column) throws IOException {

        Map requestMap = context.getExternalContext().getRequestParameterMap()
        UrlEncodedQueryString queryString = Utils.newQueryString(context)
        queryString.remove(sortParamName).remove(dirParamName)

        String sort = requestMap.get(sortParamName)
        String dir = requestMap.getOrDefault(dirParamName, defaultDirection)

        String sortIconClass = 'ui-icon-carat-2-n-s'
        String text = column.headerText
        String field = column.field
        queryString.set(sortParamName, field)
        if (sort == field) {
            sortIconClass = dir == ascendingOrder ? 'ui-icon-triangle-1-n' : 'ui-icon-triangle-1-s'
            queryString.set(dirParamName, dir == ascendingOrder ? defaultDirection : ascendingOrder)
        } else {
            queryString.set(dirParamName, defaultDirection)
        }
        String sortDirection = dir == ascendingOrder ? 'ascending' : 'descending'

        ResponseWriter writer = context.getResponseWriter()
        def header = """<th class="ui-state-default ui-state-hover" role="columnheader"
aria-label="$text: activate to sort column $sortDirection" onclick="sort([{name:'name',value:'$field'}]); window.location.href='$queryString';"
 scope="col" tabindex="0" aria-sort="other"><span class="ui-column-title">$text</span><span class="ui-sortable-column-icon ui-icon $sortIconClass"></span></th>
"""
        writer.write(header)
    }
}
