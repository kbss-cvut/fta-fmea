package cz.cvut.kbss.analysis.service.strategy;

import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.service.strategy.probability.ProbabilityPropagationStrategy;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PriorityAndProbabilityPropagationStrategy implements ProbabilityPropagationStrategy {

    private final ProbabilityPropagationStrategy andStrategy;

    @Override
    public double propagate(List<Double> probabilities, FaultEvent event) {
        double sequenceCoefficient = (event.getSequenceProbability() != null) ? event.getSequenceProbability() : 0.0;
        double multipliedProbability = andStrategy.propagate(probabilities, event) * sequenceCoefficient;

        return restrictToBoundaries(multipliedProbability, 0, 1);
    }

    private double restrictToBoundaries(double value, double min, double max) {
        return Math.min(max, Math.max(min, value));
    }

}
