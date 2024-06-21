package cz.cvut.kbss.analysis.config.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Setter
@Getter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("operational.data.filter")
public class OperationalDataConfig {

    protected Double minOperationalHours;
     protected String operationalDataServer;
    protected String operationaDataServerCheck;
    protected String operationalFailureRateService;

    @Autowired
    public OperationalDataConfig(Environment env) {
        operationalDataServer = env.getProperty("operationalDataServer");
        operationaDataServerCheck = env.getProperty("operationaDataServerCheck", "check");
        operationalFailureRateService = env.getProperty("operationalFailureRateService", "failure-rate");
    }
}
