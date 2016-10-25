package org.oreto.spikeface.controllers;

import org.h2.util.StringUtils;
import org.oreto.spikeface.utils.UrlEncodedQueryString;
import org.oreto.spikeface.utils.Utils;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.*;

@FacesComponent(value = "components.DataPagerComponent", createTag = true, namespace = "http://org.oreto/oui", tagName = "data-pager")
class DataPager extends UIComponentBase {

    static Integer defaultPage = 1;
    static Integer defaultSize = 10;
    static String defaultSizeOptions = "5,10,20,50";
    static String pageParamName = "page";
    static String sizeParamName = "size";

    int maxPages = 6;

    int page;
    int size;
    int total;
    int totalPages;
    int startPage;
    int endPage;
    List<Integer> sizeOptions = new ArrayList<>();
    UrlEncodedQueryString queryString;
    String pageLessUrl;
    String location;

    Integer next;
    Integer prev;

    public boolean init(FacesContext context) {
        Map<String, String> requestMap = context.getExternalContext().getRequestParameterMap();
        String temp = (String) this.getAttributes().get("value");
        total = StringUtils.isNumber(temp) ? Integer.parseInt(temp) : 0;

        if(total > 0) {
            temp = requestMap.getOrDefault(sizeParamName, defaultSize.toString());
            size = StringUtils.isNumber(temp) ? Integer.parseInt(temp) : defaultSize;
            totalPages = total / size + (total % size > 0 ? 1 : 0);

            String url = Utils.getPrettyUrl(context);
            queryString = UrlEncodedQueryString.parse(url);
            queryString.remove(pageParamName).remove(sizeParamName);
            pageLessUrl = queryString.toString();
            queryString = UrlEncodedQueryString.parse(url);

            temp = requestMap.getOrDefault(pageParamName, defaultPage.toString());
            page = StringUtils.isNumber(temp) ? Integer.parseInt(temp) : defaultPage;

            next = page == totalPages ? null : page + 1;
            prev = page == 1 ? null : page - 1;

            String rowsPerPageTemplate = (String) this.getAttributes().get("rowsPerPageTemplate");

            sizeOptions.add(size);
            for(String it : (rowsPerPageTemplate != null ? rowsPerPageTemplate : defaultSizeOptions).split(",")) {
                String number = it.trim();
                if (StringUtils.isNumber(number)) sizeOptions.add(Integer.parseInt(number));
            }
            computeStartEndPages();

            location = Objects.equals(this.getAttributes().get("location"), "bottom") ? "bottom" : "top";
        }
        return total > 0;
    }

    void computeStartEndPages() {
        if(maxPages < totalPages) {
            int right = page;
            int left = page;

            while (right - left < maxPages - 1){
                if (left > 1) left--;
                if (right - left < maxPages - 1 && right < totalPages) right++;
            }
            startPage = left;
            endPage = right;
        } else {
            startPage = 1;
            endPage = totalPages;
        }
    }

    @Override
    public String getFamily() { return "data.pager"; }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        if(init(context)) {
            ResponseWriter writer = context.getResponseWriter();
            writer.write(resolvePagerHtml());
        }
    }

    protected String createPageLinks() {
        StringBuilder sb = new StringBuilder();
        for(int i = startPage; i <= endPage; i++) {
            if(page == i) sb.append(createPageLink(i, true));
            else sb.append(createPageLink(i, false));
        }
        return sb.toString();
    }

    protected String createPageLink(int page, boolean active) {
        String tag = active ? "div" : "a";
        String activeState = active ? " ui-state-active" : "";
        queryString.set(pageParamName, page);
        return String.format("<%s class=\"ui-paginator-page ui-state-default ui-corner-all%s\"" +
                " aria-label=\"Page %d\" tabindex=\"0\" href=\"%s\">$page</$tag>", tag, activeState, page, queryString);
    }

    protected String createSizeOptions() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for(int size : new LinkedHashSet<>(sizeOptions)) {
            if(i == 0) sb.append(createSizeOption(size, true));
            else sb.append(createSizeOption(size, false));
            i++;
        }
        return sb.toString();
    }

    protected String createSizeOption(int size, boolean selected) {
        String select = selected ? " selected" : "";
        return String.format("<option value=\"%d\"%s>%d</option>", size, select, size);
    }

    protected String createNextLink() {
        String tag = next != null ? "a" : "div";
        String disabled = next != null ? "" : " ui-state-disabled";
        if(!Objects.equals(disabled, "")) queryString.set(pageParamName, next);
        return String.format("\t<%s href='%s' class='ui-paginator-next ui-state-default ui-corner-all%s'" +
                " aria-label='Next Page' tabindex='0'>\t\t<span class='ui-icon ui-icon-seek-next'>N</span>\t</%s>",
                tag, queryString, disabled, tag);
    }

    protected String createPrevLink() {
        String tag = prev != null ? "a" : "div";
        String disabled = prev != null ? "" : " ui-state-disabled";
        if(!Objects.equals(disabled, "")) queryString.set(pageParamName, prev);
        return String.format("\t<%s href='%s' class='ui-paginator-prev ui-state-default ui-corner-all%s'" +
                " aria-label='Previous Page' tabindex='-1'>\t\t<span class='ui-icon ui-icon-seek-prev'>P</span>\t</%s>",
                tag, queryString, disabled, tag);
    }

    protected String createFirstLink() {
        String tag = page == 1 ? "div" : "a";
        String disabled = page == 1 ? " ui-state-disabled" : "";
        queryString.set(pageParamName, 1);
        return String.format("\t<%s href='%s' class='ui-paginator-first ui-state-default ui-corner-all%s'" +
                " aria-label='First Page' tabindex='-1'>\t\t<span class='ui-icon ui-icon-seek-first'>F</span>\t</%s>",
                tag, queryString, disabled, tag);
    }

    protected String createLastLink() {
        String tag = page == totalPages ? "div" : "a";
        String disabled = page == totalPages ? " ui-state-disabled" : "";
        queryString.set(pageParamName, totalPages);
        return String.format("\t<%s href='%s' class='ui-paginator-last ui-state-default ui-corner-all%s'" +
                " aria-label='Last Page' tabindex='0'>\t\t<span class='ui-icon ui-icon-seek-end'>E</span>\t</%s>",
                tag, queryString, disabled, tag);
    }

    protected String resolvePagerHtml() {
        return "<div class='ui-paginator ui-paginator-$location ui-widget-header ui-corner-$location' role='navigation'" +
                " aria-label='Pagination'>\t<span class='ui-paginator-current'>($page of $totalPages)</span>" +
                createFirstLink() + createPrevLink() + "\t<span class='ui-paginator-pages'>" + "\t" + createPageLinks() + "\t</span>" + createNextLink() +
                createLastLink() +
                "\t<label class='ui-paginator-rpp-label ui-helper-hidden'>Rows Per Page</label>" +
                "\t<select class='ui-paginator-rpp-options ui-widget ui-state-default ui-corner-left' value='$size'" +
                " autocomplete='off' onchange='window.location.href='$pageLessUrl&size='+this.value'>" +
                "\t\t" + createSizeOptions() + "\t</select>" + "</div>";
    }
}

