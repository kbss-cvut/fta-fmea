package cz.cvut.kbss.analysis.service.strategy;

import cz.cvut.kbss.analysis.service.strategy.probability.ProbabilityPropagationStrategy;

import java.util.List;

public class OrProbabilityPropagationStrategy implements ProbabilityPropagationStrategy {

    @Override
    public double propagate(List<Double> probabilities) {
        return 1 - probabilities.stream()
                .map(p -> 1 - p)
                .reduce((a, b) -> a * b).orElse(0.0);
    }
}
