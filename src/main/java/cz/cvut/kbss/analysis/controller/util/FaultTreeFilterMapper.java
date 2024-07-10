package cz.cvut.kbss.analysis.controller.util;

import cz.cvut.kbss.analysis.dao.util.FaultTreeFilterParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

/**
 * Maps query parameters to {@link FaultTreeFilterParams} instances.
 */
@Slf4j
public class FaultTreeFilterMapper {

    private static final String SNS_LABEL_PARAM = "snsLabel";

    private static final String LABEL_PARAM = "label";


    /**
     * Maps the specified single parameter and value to a new {@link FaultTreeFilterParams} instance.
     *
     * @param param Parameter name
     * @param value Parameter value
     * @return New {@code FaultTreeFilterParams} instance
     */
    public static FaultTreeFilterParams constructFaultTreeFilter(String param, String value) {
        return constructFaultTreeFilter(new LinkedMultiValueMap<>(Map.of(param, List.of(value))));
    }

    public static FaultTreeFilterParams constructFaultTreeFilter(MultiValueMap<String, String> params) {
        final FaultTreeFilterParams result = new FaultTreeFilterParams();
        return constructFaultTreeFilter(result, new LinkedMultiValueMap<>(params));
    }

    /**
     * Maps the specified parameters to a new {@link FaultTreeFilterParams} instance.
     *
     * @param params Request parameters to map
     * @return New {@code FaultTreeFilterParams} instance
     */
    public static FaultTreeFilterParams constructFaultTreeFilter(final FaultTreeFilterParams result, MultiValueMap<String, String> params) {
        Objects.requireNonNull(params);
        getSingleValue(SNS_LABEL_PARAM, params).ifPresent(s -> result.setSnsLabel(s));
        getSingleValue(LABEL_PARAM, params).ifPresent(s -> result.setLabel(s));

        return result;
    }

    private static Optional<String> getSingleValue(String param, MultiValueMap<String, String> source) {
        final List<String> values = source.getOrDefault(param, Collections.emptyList());
        if (values.isEmpty()) {
            return Optional.empty();
        }
        if (values.size() > 1) {
            log.warn("Found multiple values of parameter '{}'. Using the first one - '{}'.", param, values.get(0));
        }
        return Optional.of(values.get(0));
    }
}
