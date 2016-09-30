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
    static String pageParamName = 'page'
    static String sizeParamName = 'size'
    static String sortParamName = 'sort'
    static String dirParamName = 'dir'

    int maxPages = 6

    int page
    int size
    int total
    int totalPages
    int startPage
    int endPage
    String sort
    String dir
    def sizeOptions = []
    UrlEncodedQueryString queryString
    String pageLessUrl
    String location

    Integer next
    Integer prev

    public boolean init(FacesContext context) {
        def request = context.getExternalContext().request as HttpServletRequest
        Map requestMap = context.getExternalContext().getRequestParameterMap()

        def temp = this.getAttributes().get('value') as String
        total = temp?.isInteger() ? temp.toInteger() : 0
        if(total) {
            temp = requestMap.getOrDefault(sizeParamName, defaultSize.toString())
            size = temp.isInteger() ? temp.toInteger() : defaultSize
            totalPages = total / size + (total % size > 0 ? 1 : 0)

            def pretty = PrettyContext.currentInstance
            def baseUrl = "${request.getContextPath()}$pretty.requestURL"
            def url = "$baseUrl${pretty.requestQueryString}"
            queryString = UrlEncodedQueryString.parse(url)
            queryString.remove(pageParamName).remove(sizeParamName)
            pageLessUrl = queryString.toString()
            queryString = UrlEncodedQueryString.parse(url)

            temp = requestMap.getOrDefault(pageParamName, defaultPage.toString())
            page = temp.isInteger() ? temp.toInteger() : defaultPage

            next = page == totalPages ? null : page + 1
            prev = page == 1 ? null : page - 1

            def rowsPerPageTemplate = this.getAttributes().get('rowsPerPageTemplate') as String

            sizeOptions.add(size)
            (rowsPerPageTemplate ?: defaultSizeOptions).split(',').each {
                def number = it.trim()
                if (number.isInteger()) sizeOptions.add(number.toInteger())
            }
            computeStartEndPages()

            sort = requestMap.get(sortParamName)
            dir = requestMap.getOrDefault(dirParamName, defaultDirection)

            location = this.getAttributes().get('location') as String == 'bottom' ? 'bottom' : 'top'
        }
        total as boolean
    }

    public computeStartEndPages() {
        if(maxPages < totalPages) {
            int right = page
            int left = page

            while (right - left < maxPages - 1){
                if (left > 1) left--
                if (right - left < maxPages - 1 && right < totalPages) right++
            }
            startPage = left
            endPage = right
        } else {
            startPage = 1
            endPage = totalPages
        }
    }

    @Override String getFamily() { "data.pager" }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        if(init(context)) {
            ResponseWriter writer = context.getResponseWriter()
            writer.write(resolvePagerHtml())
        }
    }

    protected String createPageLinks() {
        StringBuffer sb = new StringBuffer()
        (startPage..endPage).each {
            if(page == it) sb.append(createPageLink(it as int, true))
            else sb.append(createPageLink(it as int))
        }
        sb.toString()
    }

    protected String createPageLink(int page, boolean active = false) {
        def tag = active ? 'div' : 'a'
        def activeState = active ? ' ui-state-active' : ''
        queryString.set(pageParamName, page)
        "<$tag class=\"ui-paginator-page ui-state-default ui-corner-all$activeState\" aria-label=\"Page $page\" tabindex=\"0\" href=\"${queryString}\">$page</$tag>"
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
        String tag = next ? 'a' : 'div'
        String disabled = next ? '' : ' ui-state-disabled'
        if(!disabled) queryString.set(pageParamName, next)
        """\t<$tag href="$queryString" class="ui-paginator-next ui-state-default ui-corner-all$disabled" aria-label="Next Page" tabindex="0">
\t\t<span class="ui-icon ui-icon-seek-next">N</span>
\t</$tag>"""
    }

    protected String createPrevLink() {
        String tag = prev ? 'a' : 'div'
        String disabled = prev ? '' : ' ui-state-disabled'
        if(!disabled) queryString.set(pageParamName, prev)
        """\t<$tag href="$queryString" class="ui-paginator-prev ui-state-default ui-corner-all$disabled" aria-label="Previous Page" tabindex="-1">
\t\t<span class="ui-icon ui-icon-seek-prev">P</span>
\t</$tag>"""
    }

    protected String createFirstLink() {
        String tag = page == 1 ? 'div' : 'a'
        String disabled = page == 1 ? ' ui-state-disabled' : ''
        queryString.set(pageParamName, 1)
        """\t<$tag href="$queryString" class="ui-paginator-first ui-state-default ui-corner-all$disabled" aria-label="First Page" tabindex="-1">
\t\t<span class="ui-icon ui-icon-seek-first">F</span>
\t</$tag>"""
    }

    protected String createLastLink() {
        String tag = page == totalPages ? 'div' : 'a'
        String disabled = page == totalPages ? ' ui-state-disabled' : ''
        queryString.set(pageParamName, totalPages)
        """\t<$tag href="$queryString" class="ui-paginator-last ui-state-default ui-corner-all$disabled" aria-label="Last Page" tabindex="0">
\t\t<span class="ui-icon ui-icon-seek-end">E</span>
\t</$tag>"""
    }

    protected String resolvePagerHtml() {
        """<div class="ui-paginator ui-paginator-$location ui-widget-header ui-corner-$location" role="navigation" aria-label="Pagination">
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
