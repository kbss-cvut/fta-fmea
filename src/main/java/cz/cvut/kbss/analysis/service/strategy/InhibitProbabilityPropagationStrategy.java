package cz.cvut.kbss.analysis.service.strategy;

import cz.cvut.kbss.analysis.service.strategy.probability.ProbabilityPropagationStrategy;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class InhibitProbabilityPropagationStrategy implements ProbabilityPropagationStrategy {

    private final ProbabilityPropagationStrategy andStrategy;

    @Override
    public double propagate(List<Double> probabilities) {
        return andStrategy.propagate(probabilities);
    }
}
