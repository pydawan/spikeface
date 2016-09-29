package org.oreto.spikeface.controllers

import com.ocpsoft.pretty.PrettyContext
import org.oreto.spikeface.utils.UrlEncodedQueryString

import javax.faces.component.FacesComponent
import javax.faces.component.UIComponentBase
import javax.faces.context.FacesContext
import javax.faces.context.ResponseWriter
import javax.servlet.http.HttpServletRequest

@FacesComponent(value = "components.DataPagerComponent", createTag = true, namespace = "http://org.oreto/oui", tagName = "data-pager")
class DataPager extends UIComponentBase {

    static Integer defaultPage = 1
    static Integer defaultSize = 10
    static String defaultSizeOptions = "5,10,20,50"
    static String defaultDirection = 'desc'
    static String ascendingOrder = 'asc'
    static int maxPages = 3

    int page
    int size
    int total
    int totalPages
    String sort
    String dir
    def sizeOptions = []
    UrlEncodedQueryString queryString
    String pageLessUrl

    Integer next
    Integer prev

    public void init(FacesContext context) {
        super.decode(context)
        def request = context.getExternalContext().request as HttpServletRequest
        def pretty = PrettyContext.currentInstance
        def baseUrl = "${request.getContextPath()}$pretty.requestURL"
        def url = "$baseUrl${pretty.requestQueryString}"
        queryString = UrlEncodedQueryString.parse(url)
        queryString.remove('page').remove('size')
        pageLessUrl = queryString.toString()
        queryString = UrlEncodedQueryString.parse(url)

        Map requestMap = context.getExternalContext().getRequestParameterMap()
        def temp = requestMap.getOrDefault('page', defaultPage.toString())
        page = temp.isInteger() ? temp.toInteger() : defaultPage

        temp = requestMap.getOrDefault('size', defaultSize.toString())
        size = temp.isInteger() ? temp.toInteger() : defaultSize

        temp = this.getAttributes().get('value') as String
        total = temp?.isInteger() ? temp.toInteger() : 0
        totalPages = total / size + (total % size > 0 ? 1 : 0)

        next = page == totalPages ? null : page + 1
        prev = page == 1 ? null : page - 1

        def rowsPerPageTemplate = this.getAttributes().get('rowsPerPageTemplate') as String

        sizeOptions.add(size)
        (rowsPerPageTemplate ?: defaultSizeOptions).split(',').each {
            def number = it.trim()
            if(number.isInteger()) sizeOptions.add(number.toInteger())
        }

        sort = requestMap.get('sort')
        dir = requestMap.getOrDefault('dir', defaultDirection)
    }

    @Override String getFamily() { "data.pager" }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        init(context)
        ResponseWriter writer = context.getResponseWriter()
        writer.write(resolvePagerHtml())
    }

    protected String createPageLinks() {
        StringBuffer sb = new StringBuffer()
        (1..totalPages).eachWithIndex { val, index ->
            if(index == page - 1) sb.append(createPageLink(val as int, true))
            else sb.append(createPageLink(val as int))
        }
        sb.toString()
    }

    protected String createPageLink(int page, boolean active = false) {
        def activeState = active ? ' ui-state-active' : ''
        queryString.set('page', page)
        "<a class=\"ui-paginator-page ui-state-default ui-corner-all$activeState\" aria-label=\"Page $page\" tabindex=\"0\" href=\"${queryString}\">$page</a>"
    }

    protected String createSizeOptions() {
        StringBuffer sb = new StringBuffer()
        sizeOptions.unique().eachWithIndex { int entry, int i ->
            if(i == 0) sb.append(createSizeOption(entry, true))
            else sb.append(createSizeOption(entry))
        }
        sb.toString()
    }

    protected String createSizeOption(int size, boolean selected = false) {
        def select = selected ? ' selected' : ''
        "<option value=\"$size\"$select>$size</option>"
    }

    protected String createNextLink() {
        String disabled = next ? '' : ' ui-state-disabled'
        if(!disabled) queryString.set('page', next)
        """\t<a href="$queryString" class="ui-paginator-next ui-state-default ui-corner-all$disabled" aria-label="Next Page" tabindex="0">
\t\t<span class="ui-icon ui-icon-seek-next">N</span>
\t</a>"""
    }

    protected String createPrevLink() {
        String disabled = prev ? '' : ' ui-state-disabled'
        if(!disabled) queryString.set('page', prev)
        """\t<a href="$queryString" class="ui-paginator-prev ui-state-default ui-corner-all$disabled" aria-label="Previous Page" tabindex="-1">
\t\t<span class="ui-icon ui-icon-seek-prev">P</span>
\t</a>"""
    }

    protected String createFirstLink() {
        String disabled = page == 1 ? ' ui-state-disabled' : ''
        queryString.set('page', 1)
        """\t<a href="$queryString" class="ui-paginator-first ui-state-default ui-corner-all$disabled" aria-label="First Page" tabindex="-1">
\t\t<span class="ui-icon ui-icon-seek-first">F</span>
\t</a>"""
    }

    protected String createLastLink() {
        String disabled = page == totalPages ? ' ui-state-disabled' : ''
        queryString.set('page', totalPages)
        """\t<a href="$queryString" class="ui-paginator-last ui-state-default ui-corner-all$disabled" aria-label="Last Page" tabindex="0">
\t\t<span class="ui-icon ui-icon-seek-end">E</span>
\t</a>"""
    }

    protected String resolvePagerHtml() {
        """<div class="ui-paginator ui-paginator-top ui-widget-header ui-corner-top" role="navigation" aria-label="Pagination">
\t<span class="ui-paginator-current">($page of $totalPages)</span>
${createFirstLink()}
${createPrevLink()}
\t<span class="ui-paginator-pages">
\t${createPageLinks()}
\t</span>
${createNextLink()}
${createLastLink()}
\t<label class="ui-paginator-rpp-label ui-helper-hidden">Rows Per Page</label>
\t<select class="ui-paginator-rpp-options ui-widget ui-state-default ui-corner-left" value="$size" autocomplete="off" onchange="window.location.href='$pageLessUrl&size='+this.value">
\t\t${createSizeOptions()}
\t</select>
</div>
"""
    }
}
