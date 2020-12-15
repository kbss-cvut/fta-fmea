package cz.cvut.kbss.analysis.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.jsonld.ConfigParam;
import cz.cvut.kbss.jsonld.jackson.JsonLdModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonLdModule m = new JsonLdModule();
        m.configure(ConfigParam.SCAN_PACKAGE, "cz.cvut.kbss.analysis");
        mapper.registerModule(m);
        return mapper;
    }

}
