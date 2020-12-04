package cz.cvut.kbss.analysis.environment.config;

import cz.cvut.kbss.analysis.environment.Environment;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@TestConfiguration
@ComponentScan(basePackages = "cz.cvut.kbss.analysis.service")
@ContextConfiguration(classes = {UserDetailsService.class})
public class TestServiceConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Rest template.
     */
    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate client = new RestTemplate();
        final MappingJackson2HttpMessageConverter jacksonConverter =
                new MappingJackson2HttpMessageConverter();
        jacksonConverter.setObjectMapper(Environment.getObjectMapper());
        final StringHttpMessageConverter stringConverter =
                new StringHttpMessageConverter(StandardCharsets.UTF_8);
        client.setMessageConverters(
                Arrays.asList(jacksonConverter, stringConverter, new ResourceHttpMessageConverter()));
        return client;
    }

//    @Bean
//    public LocalValidatorFactoryBean validatorFactoryBean() {
//        return new LocalValidatorFactoryBean();
//    }

//    @Bean
//    public ClassPathResource languageSpecification() {
//        return new ClassPathResource("languages/language.ttl");
//    }

    @Bean
    public SecurityUtils securityUtils() {
        return new SecurityUtils();
    }
}
