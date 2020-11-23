package cz.cvut.kbss.analysis.service.strategy;

import cz.cvut.kbss.analysis.service.strategy.probability.ProbabilityPropagationStrategy;

import java.util.List;

public class PriorityAndProbabilityPropagationStrategy implements ProbabilityPropagationStrategy {

    @Override
    public double propagate(List<Double> probabilities) {
        if(probabilities.isEmpty()) {
            return 0.0;
        }

        // TODO events are independent, thus P(A) * P(B) / P(B) = P(A)
        // conditional probability P(A|B) = P(A and B) / P(B)
        double resultProbability = probabilities.get(0);
        for (int i = 1; i < probabilities.size(); i++) {
            resultProbability = (probabilities.get(i) * resultProbability) / resultProbability;
        }

        return resultProbability;
    }

}
