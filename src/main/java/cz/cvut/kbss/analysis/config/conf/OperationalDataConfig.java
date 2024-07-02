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

    public static final String MIN_OPERATIONAL_TIME_PARAM = "minOperationalTime";
    public static final String FHA_TYPES_PARAM = "fha";

    protected Double minOperationalHours;
    protected String operationalFailureRateService;
    protected String fhaBasedoperationalFailureRateService;

    @Autowired
    public OperationalDataConfig(Environment env) {
        operationalFailureRateService = env.getProperty("operationalFailureRateService");
        fhaBasedoperationalFailureRateService = env.getProperty("fhaBasedoperationalFailureRateService");
    }
}
