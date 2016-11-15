package org.oreto.spikeface.controllers.common

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

        String sort = requestMap.get(sortParamName)
        String dir = requestMap.getOrDefault(dirParamName, defaultDirection)

        String sortIconClass = 'ui-icon-carat-2-n-s'
        String text = column.headerText
        String field = column.field

        String direction
        if (sort == field) {
            sortIconClass = dir == ascendingOrder ? 'ui-icon-triangle-1-n' : 'ui-icon-triangle-1-s'
            direction = dir == ascendingOrder ? defaultDirection : ascendingOrder
        } else {
            direction = defaultDirection
        }
        String sortDirection = dir == ascendingOrder ? 'ascending' : 'descending'

        def queryString = Utils.newQueryString(context, ["${sortParamName}" : field, "${dirParamName}" : direction])
        ResponseWriter writer = context.getResponseWriter()
        def header = column.headerText ? """<th class="ui-state-default ui-state-hover" role="columnheader" onmouseup="window.location.href='$queryString';"
aria-label="$text: activate to sort column $sortDirection" onclick="sort([{name:'$sortParamName',value:'$field'},{name:'$dirParamName',value:'$direction'}]);"
 scope="col" tabindex="0" aria-sort="other"><span class="ui-column-title">$text</span><span class="ui-sortable-column-icon ui-icon $sortIconClass"></span></th>
""" : """<th class="ui-state-default" role="columnheader" width="${column.width}" />"""
        writer.write(header)
    }
}
