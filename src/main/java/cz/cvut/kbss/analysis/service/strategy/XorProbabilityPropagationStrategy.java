package cz.cvut.kbss.analysis.service.strategy;

import cz.cvut.kbss.analysis.service.strategy.probability.ProbabilityPropagationStrategy;
import lombok.AllArgsConstructor;

import java.util.List;


@AllArgsConstructor
public class XorProbabilityPropagationStrategy implements ProbabilityPropagationStrategy {

    private final ProbabilityPropagationStrategy andStrategy;
    private final ProbabilityPropagationStrategy orStrategy;

    @Override
    public double propagate(List<Double> probabilities) {
        return orStrategy.propagate(probabilities) - andStrategy.propagate(probabilities);
    }
}
