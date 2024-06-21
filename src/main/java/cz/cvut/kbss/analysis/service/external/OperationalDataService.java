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

    protected String getApi(String api){
        String serverUri = operationalDataConfig.getOperationalDataServer();
        return api == null || serverUri == null ? null : serverUri + api;
    }


    protected String getCheckApi(){
        return getApi(operationalDataConfig.getOperationaDataServerCheck());
    }

    protected String getFailureRateApi(){
        return getApi(operationalDataConfig.getOperationalFailureRateService());
    }

    public String checkConnection(){
        String apiURI = getCheckApi();
        if(apiURI == null) return "not connected";
        try {
            return restTemplate.getForObject(apiURI, String.class);
        } catch (Exception e){
            log.warn("Failed to fetch failure rates from " + apiURI, e);
        }
        return "not working";
    }

    public ItemFailureRate[] fetchFailureRates(OperationalDataFilter filter, Collection<URI> components){
        String apiURI = getFailureRateApi();
        if(apiURI == null) return null;
        try {
            Map<String, Object> uriParams = new HashMap<>();
            uriParams.put("minOperationalTime", filter.getMinOperationalHours());
            return restTemplate.postForObject(apiURI, components, ItemFailureRate[].class, uriParams);
        } catch (Exception e){
            log.warn("Failed to fetch failure rates from \"{}\" \nerror message: {}", apiURI, e.getMessage());
        }
        return null;
    }
}
