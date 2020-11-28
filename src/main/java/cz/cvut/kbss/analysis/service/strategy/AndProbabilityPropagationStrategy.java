package cz.cvut.kbss.analysis.service.strategy;

import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.service.strategy.probability.ProbabilityPropagationStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class AndProbabilityPropagationStrategy implements ProbabilityPropagationStrategy {

    @Override
    public double propagate(List<Double> probabilities, FaultEvent event) {
        return probabilities.stream().reduce((a, b) -> a * b).orElse(0.0);
    }

}
