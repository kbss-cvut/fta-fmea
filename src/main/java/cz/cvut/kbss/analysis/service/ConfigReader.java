package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.util.ConfigParam;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConfigReader {

    private final Environment environment;

    public ConfigReader(Environment environment) {
        this.environment = environment;
    }

    /**
     * Gets value of the specified configuration parameter.
     *
     * @param param Configuration parameter
     * @return Configuration parameter value, empty string if the parameter is not set
     */
    public String getConfig(ConfigParam param) {
        return getConfig(param, "");
    }

    public String getConfig(ConfigParam param, String defaultValue) {
        if (environment.containsProperty(param.toString())) {
            return environment.getProperty(param.toString());
        }
        return defaultValue;
    }

    public String getConfigWithParams(ConfigParam param, Map<String, String> params) {
        String str = environment.getProperty(param.toString());
        for ( String key : params.keySet() ) {
            str = str.replace("{{" + key + "}}", params.get(key));
        }
        return str;
    }
}