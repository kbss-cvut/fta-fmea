package cz.cvut.kbss.analysis.service.external;

import cz.cvut.kbss.analysis.config.conf.OperationalDataConfig;
import cz.cvut.kbss.analysis.exception.ExternalServiceException;
import cz.cvut.kbss.analysis.model.opdata.ItemFailureRate;
import cz.cvut.kbss.analysis.model.opdata.OperationalDataFilter;
import cz.cvut.kbss.analysis.service.OperationalDataFilterService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;

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

    @PostConstruct
    public void checkConnectionOnStartUp(){
        checkConnection();
    }

    protected String getFailureRateApi(String path){
        if(path == null)
            throw new ExternalServiceException("Configuration parameter operationalFailureRateService not set.");
        return path;
    }

    protected String getOperationalFailureRateService(){
        return getFailureRateApi(operationalDataConfig.getOperationalFailureRateService());
    }

    protected String getFhaBasedOperationalFailureRateService(){
        return getFailureRateApi(operationalDataConfig.getFhaBasedoperationalFailureRateService());
    }

    public String checkConnection(){
        String apiURI = null;
        try {
            apiURI = getOperationalFailureRateService();
            restTemplate.headForHeaders(apiURI);
            log.warn("connection to {} available", apiURI);
            return "ok";
        } catch (Exception e){
            log.warn("checkConnection failed - {} ", e.getMessage());
        }
        return apiURI != null ? "bad configuration " : "connection not working";
    }

    public ItemFailureRate[] fetchFailureRates(OperationalDataFilter filter, Collection<URI> components){
        String apiURI = null;
        try {
            apiURI = getOperationalFailureRateService();

            apiURI = UriComponentsBuilder.fromHttpUrl(apiURI)
                    .queryParam(OperationalDataConfig.MIN_OPERATIONAL_TIME_PARAM, filter.getMinOperationalHours())
                    .toUriString();
            return restTemplate.postForObject(apiURI, components, ItemFailureRate[].class);
        } catch (Exception e){
            log.warn("Failed to fetch failure rates from \"{}\" \nerror message: {}", apiURI, e.getMessage());
        }
        return null;
    }

    public ItemFailureRate[] fetchFhaFailureRates(OperationalDataFilter filter, Collection<URI> fhaTypes){
        String apiURI = null;
        try {
            apiURI = getFhaBasedOperationalFailureRateService();

            apiURI = UriComponentsBuilder.fromHttpUrl(apiURI)
                    .queryParam(OperationalDataConfig.MIN_OPERATIONAL_TIME_PARAM, filter.getMinOperationalHours())
                    .queryParam(OperationalDataConfig.FHA_TYPES_PARAM, fhaTypes)
                    .toUriString();

            return restTemplate.getForObject(apiURI, ItemFailureRate[].class);
        } catch (Exception e){
            log.warn("Failed to fetch fha based failure rates from \"{}\" \nerror message: {}", apiURI, e.getMessage());
        }
        return null;
    }
}
