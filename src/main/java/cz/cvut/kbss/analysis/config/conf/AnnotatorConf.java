package cz.cvut.kbss.analysis.config.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("annotator")
public class AnnotatorConf {
    private String listDocumentsAPI;
    private String convertDocumentAPI;
    private String processAnnotationAPI;
}
