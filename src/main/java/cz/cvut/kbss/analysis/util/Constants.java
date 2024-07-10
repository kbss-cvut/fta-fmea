package cz.cvut.kbss.analysis.util;

public final class Constants {

    private Constants() {
        throw new AssertionError();
    }

    /**
     * Number of history actions fetched from database. Needs to be changes also in front-end.
     */
    public static final int DEFAULT_PAGE_SIZE = 25;

    /**
     * Name of the request parameter specifying page number.
     */
    public static final String PAGE_PARAM = "page";

    /**
     * Name of the request parameter specifying page size.
     */
    public static final String PAGE_SIZE_PARAM = "size";

    /**
     * Name of the request parameter specifying sorting.
     */
    public static final String SORT_PARAM = "sort";

    /**
     * Name of the request parameter specifying ordering by last date.
     */
    public static final String SORT_BY_DATE_PARAM = "date";

    /**
     * Name of the request parameter specifying ordering by label.
     */
    public static final String SORT_BY_LABEL_PARAM = "label";

    /**
     * Name of the request parameter specifying ordering by SNS label.
     */
    public static final String SORT_BY_SNS_LABEL_PARAM = "snsLabel";

    /**
     * Represents the X-Total-Count HTTP header used to convey the total number of items in paged or otherwise
     * restricted response.
     */
    public static final String X_TOTAL_COUNT_HEADER = "X-Total-Count";
}
