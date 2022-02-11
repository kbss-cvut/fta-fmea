package cz.cvut.kbss.analysis.resources;

import org.eclipse.rdf4j.common.io.ResourceUtil;

import java.io.IOException;

public class ResourceUtils {
    public static final String REQUIRED_BEHAVIORS = "required-behaviors";
    public static final String BEHAVIOR_PARTS = "behavior-parts";
    public static final String IMPAIRING_BEHAVIORS = "impairing-behaviors";

    public static String loadQuery(String queryName) throws IOException {
        String resourcePath = String.format("/queries/%s.sparql", queryName);
        return ResourceUtil.getString(resourcePath);
    }
}
