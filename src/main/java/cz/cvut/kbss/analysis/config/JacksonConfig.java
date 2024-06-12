package cz.cvut.kbss.analysis.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.jsonld.ConfigParam;
import cz.cvut.kbss.jsonld.jackson.JsonLdModule;
import cz.cvut.kbss.jsonld.jackson.serialization.SerializationConstants;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonLdModule m = new JsonLdModule();
        m.configure(ConfigParam.SCAN_PACKAGE, "cz.cvut.kbss.analysis");
        m.configure(SerializationConstants.FORM, SerializationConstants.FORM_COMPACT_WITH_CONTEXT);
        mapper.registerModule(m);
        return mapper;
    }

    @Bean(name = "customRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
//                .additionalMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper()))
                .build();
    }
}
