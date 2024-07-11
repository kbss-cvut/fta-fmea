package cz.cvut.kbss.analysis.controller.util;

import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.util.Constants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PagingUtils {

    /**
     * Prefix indicating ascending sort order.
     */
    public static final char SORT_ASC = '+';

    /**
     * Prefix indicating descending sort order.
     */
    public static final char SORT_DESC = '-';



    private PagingUtils() {
        throw new AssertionError();
    }

    /**
     * Resolves paging and sorting configuration from the specified request parameters.
     * <p>
     * If no paging and filtering info is specified, an {@link Pageable#unpaged()} object is returned.
     * <p>
     * Note that for sorting, {@literal +} should be used before sorting property name to specify ascending order,
     * {@literal -} for descending order, for example, {@literal -date} indicates sorting by date in descending order.
     *
     * @param params Request parameters
     * @return {@code Pageable} containing values resolved from the params or defaults
     */
    public static Pageable resolvePaging(MultiValueMap<String, String> params) {
        Sort sort;
        if (params.containsKey(Constants.SORT_PARAM)) {
            sort = Sort.by(params.get(Constants.SORT_PARAM).stream().map(sp -> {
                if (sp.charAt(0) == SORT_ASC || sp.charAt(0) == SORT_DESC) {
                    final String property = sp.substring(1);
                    return sp.charAt(0) == SORT_DESC ? Sort.Order.desc(property) : Sort.Order.asc(property);
                }
                return Sort.Order.asc(sp);
            }).collect(Collectors.toList()));
        }else{
            sort = Sort.by(
                    Sort.Order.desc(Constants.SORT_BY_DATE_PARAM),
                    Sort.Order.asc(Constants.SORT_BY_SNS_LABEL_PARAM),
                    Sort.Order.asc(Constants.SORT_BY_LABEL_PARAM)
            );
        }

        if (params.getFirst(Constants.PAGE_PARAM) == null)
            return Pageable.unpaged(sort);

        final int page = Integer.parseInt(params.getFirst(Constants.PAGE_PARAM));
        final int size = Optional.ofNullable(params.getFirst(Constants.PAGE_SIZE_PARAM)).map(Integer::parseInt)
                                 .orElse(Constants.DEFAULT_PAGE_SIZE);

        return PageRequest.of(page, size, sort);
    }

    public static Comparator<FaultTree> comparator(Sort sort){
        Iterator<Sort.Order> orders = sort.iterator();
        Comparator<FaultTree> c = null;
        while(orders.hasNext()){
            Sort.Order order = orders.next();
            if(c == null)
                c = getFunction(order);
            else
                c.thenComparing(getFunction(order));
        }
        return c;
    }

    private static Comparator<FaultTree>  getFunction(Sort.Order order){
        Comparator<FaultTree> comp = switch (order.getProperty()){
            case Constants.SORT_BY_DATE_PARAM ->  Comparator.comparing((FaultTree t) ->
                        Stream.of(t.getModified(), t.getCreated())
                    .filter(d -> d != null).findFirst().orElse(new Date(0)));
            case Constants.SORT_BY_LABEL_PARAM -> Comparator.comparing((FaultTree t) ->
                    Optional.ofNullable(t.getName()).orElse(""));
            case Constants.SORT_BY_SNS_LABEL_PARAM -> Comparator.comparing((FaultTree t) ->
                    Optional.ofNullable(t.getSubsystem()).map(i -> i.getName()).orElse(""));
            default -> null;
        };

        return order.getDirection() == Sort.Direction.DESC
                ?  comp.reversed()
                : comp;
    }

}
