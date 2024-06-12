package cz.cvut.kbss.analysis.service.external;

import cz.cvut.kbss.analysis.config.conf.OperationalDataConfig;
import cz.cvut.kbss.analysis.model.opdata.ItemFailureRate;
import cz.cvut.kbss.analysis.model.opdata.OperationalDataFilter;
import cz.cvut.kbss.analysis.service.OperationalDataFilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OperationalDataService {

    private final OperationalDataConfig operationalDataConfig;
    private final OperationalDataFilterService service;
    private final RestTemplate restTemplate;


    public OperationalDataService(OperationalDataConfig operationalDataConfig, OperationalDataFilterService service, @Qualifier("customRestTemplate") RestTemplate restTemplate) {
        this.operationalDataConfig = operationalDataConfig;
        this.service = service;
        this.restTemplate = restTemplate;
    }

    public ItemFailureRate[] fetchFailureRates(OperationalDataFilter filter, Collection<URI> components){
        String apiURI = operationalDataConfig.getOperationalFailureRateService();
        Map<String, Object> uriParams = new HashMap<>();
        uriParams.put("minOperationalTime", filter.getMinOperationalHours());
        return restTemplate.postForObject(apiURI, components, ItemFailureRate[].class, uriParams);
    }
}
