package cz.cvut.kbss.analysis.config.conf;

import cz.cvut.kbss.analysis.util.ConfigParam;
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

}
