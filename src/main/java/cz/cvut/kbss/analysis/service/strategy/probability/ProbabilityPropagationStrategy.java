package cz.cvut.kbss.analysis.service.strategy.probability;

import java.util.List;

public interface ProbabilityPropagationStrategy {

    double propagate(List<Double> probabilities);

}
