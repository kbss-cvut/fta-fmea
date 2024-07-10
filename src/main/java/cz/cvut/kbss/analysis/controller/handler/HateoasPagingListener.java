package cz.cvut.kbss.analysis.controller.handler;

import cz.cvut.kbss.analysis.controller.event.PaginatedResultRetrievedEvent;
import cz.cvut.kbss.analysis.controller.util.HttpPaginationLink;
import cz.cvut.kbss.analysis.util.Constants;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Generates HATEOAS paging headers based on the paginated result retrieved by a REST controller.
 */
@Component
public class HateoasPagingListener implements ApplicationListener<PaginatedResultRetrievedEvent> {

    @Override
    public void onApplicationEvent(PaginatedResultRetrievedEvent event) {
        final Page<?> page = event.getPage();
        final LinkHeader header = new LinkHeader();
        if (!page.isEmpty() || page.getTotalPages() > 0) {
            // Always add first and last links, even when there is just one page. This allows clients to know where the limits
            // are
            header.addLink(generateFirstPageLink(page, event.getUriBuilder()), HttpPaginationLink.FIRST);
            header.addLink(generateLastPageLink(page, event.getUriBuilder()), HttpPaginationLink.LAST);
        }
        if (page.hasNext()) {
            header.addLink(generateNextPageLink(page, event.getUriBuilder()), HttpPaginationLink.NEXT);
        }
        if (page.hasPrevious()) {
            header.addLink(generatePreviousPageLink(page, event.getUriBuilder()), HttpPaginationLink.PREVIOUS);
        }
        if (header.hasLinks()) {
            event.getResponse().addHeader(HttpHeaders.LINK, header.toString());
        }
        event.getResponse().addHeader(Constants.X_TOTAL_COUNT_HEADER, Long.toString(page.getTotalElements()));
    }

    private String generateNextPageLink(Page<?> page, UriComponentsBuilder uriBuilder) {
        return uriBuilder.replaceQueryParam(Constants.PAGE_PARAM, page.getNumber() + 1)
                         .replaceQueryParam(Constants.PAGE_SIZE_PARAM, page.getSize())
                         .build().encode().toUriString();
    }

    private String generatePreviousPageLink(Page<?> page, UriComponentsBuilder uriBuilder) {
        return uriBuilder.replaceQueryParam(Constants.PAGE_PARAM, page.getNumber() - 1)
                         .replaceQueryParam(Constants.PAGE_SIZE_PARAM, page.getSize())
                         .build().encode().toUriString();
    }

    private String generateFirstPageLink(Page<?> page, UriComponentsBuilder uriBuilder) {
        return uriBuilder.replaceQueryParam(Constants.PAGE_PARAM, 0)
                         .replaceQueryParam(Constants.PAGE_SIZE_PARAM, page.getSize())
                         .build().encode().toUriString();
    }

    private String generateLastPageLink(Page<?> page, UriComponentsBuilder uriBuilder) {
        return uriBuilder.replaceQueryParam(Constants.PAGE_PARAM, page.getTotalPages() - 1)
                         .replaceQueryParam(Constants.PAGE_SIZE_PARAM, page.getSize())
                         .build().encode().toUriString();
    }

    private static class LinkHeader {

        private final StringBuilder linkBuilder = new StringBuilder();

        private void addLink(String url, HttpPaginationLink type) {
            if (!linkBuilder.isEmpty()) {
                linkBuilder.append(", ");
            }
            linkBuilder.append('<').append(url).append('>').append("; ").append("rel=\"").append(type.getName())
                       .append('"');
        }

        private boolean hasLinks() {
            return !linkBuilder.isEmpty();
        }

        @Override
        public String toString() {
            return linkBuilder.toString();
        }
    }
}
