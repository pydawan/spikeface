package org.oreto.spikeface.controllers

import javax.faces.component.FacesComponent
import javax.faces.component.UIComponentBase
import javax.faces.context.FacesContext
import javax.faces.context.ResponseWriter

@FacesComponent(value = "components.DataPagerComponent", createTag = true, namespace = "http://org.oreto/oui", tagName = "data-pager")
class DataPager extends UIComponentBase {

    static Integer defaultPage = 1
    static Integer defaultSize = 10
    static String defaultDirection = 'desc'
    static String ascendingOrder = 'asc'

    int page
    int size
    int total
    String sort
    String dir

    public void init(FacesContext context) {
        super.decode(context)
        Map requestMap = context.getExternalContext().getRequestParameterMap()
        def temp = requestMap.getOrDefault('page', defaultPage.toString())
        page = temp.isInteger() ? temp.toInteger() : defaultPage
        temp = requestMap.getOrDefault('size', defaultPage.toString())
        size = temp.isInteger() ? temp.toInteger() : defaultSize
        sort = requestMap.get('sort')
        dir = requestMap.getOrDefault('dir', defaultDirection)
    }

    @Override String getFamily() { "data.pager" }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        init(context)
        ResponseWriter writer = context.getResponseWriter()
        writer.write(resolvePagerHtml())
    }

    public String resolvePagerHtml() {
        """<div class="ui-paginator ui-paginator-top ui-widget-header ui-corner-top" role="navigation" aria-label="Pagination">
\t<span class="ui-paginator-current">($page of 5)</span>
\t<a href="#" class="ui-paginator-first ui-state-default ui-corner-all ui-state-disabled" aria-label="First Page" tabindex="-1">
\t\t<span class="ui-icon ui-icon-seek-first">F</span>
\t</a>
\t<a href="#" class="ui-paginator-prev ui-state-default ui-corner-all ui-state-disabled" aria-label="Previous Page" tabindex="-1">
\t\t<span class="ui-icon ui-icon-seek-prev">P</span>
\t</a>
\t<span class="ui-paginator-pages">
\t\t<a class="ui-paginator-page ui-state-default ui-corner-all ui-state-active" aria-label="Page 1" tabindex="0" href="#">1</a>
\t\t<a class="ui-paginator-page ui-state-default ui-corner-all" aria-label="Page 2" tabindex="0" href="#">2</a>
\t\t<a class="ui-paginator-page ui-state-default ui-corner-all" aria-label="Page 3" tabindex="0" href="#">3</a>
\t\t<a class="ui-paginator-page ui-state-default ui-corner-all" aria-label="Page 4" tabindex="0" href="#">4</a>
\t\t<a class="ui-paginator-page ui-state-default ui-corner-all" aria-label="Page 5" tabindex="0" href="#">5</a>
\t</span>
\t<a href="#" class="ui-paginator-next ui-state-default ui-corner-all" aria-label="Next Page" tabindex="0">
\t\t<span class="ui-icon ui-icon-seek-next">N</span>
\t</a>
\t<a href="#" class="ui-paginator-last ui-state-default ui-corner-all" aria-label="Last Page" tabindex="0">
\t\t<span class="ui-icon ui-icon-seek-end">E</span>
\t</a>
\t<label class="ui-paginator-rpp-label ui-helper-hidden">Rows Per Page</label>
\t<select class="ui-paginator-rpp-options ui-widget ui-state-default ui-corner-left" value="10" autocomplete="off">
\t\t<option value="5">5</option>
\t\t<option value="10" selected="selected">10</option>
\t\t<option value="15">15</option>
\t</select>
</div>
"""
    }
}
