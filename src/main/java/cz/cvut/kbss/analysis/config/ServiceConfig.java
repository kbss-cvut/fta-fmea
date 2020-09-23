package cz.cvut.kbss.analysis.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "cz.cvut.kbss.analysis.service")
public class ServiceConfig {
}