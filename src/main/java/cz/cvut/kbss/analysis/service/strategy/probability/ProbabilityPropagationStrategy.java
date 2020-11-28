package cz.cvut.kbss.analysis.service.strategy.probability;

import cz.cvut.kbss.analysis.model.FaultEvent;

import java.util.List;

public interface ProbabilityPropagationStrategy {

    double propagate(List<Double> probabilities, FaultEvent event);

}
