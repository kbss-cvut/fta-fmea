package cz.cvut.kbss.analysis.service.strategy;

import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.service.strategy.probability.ProbabilityPropagationStrategy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class XorProbabilityPropagationStrategy implements ProbabilityPropagationStrategy {

    @Override
    public double propagate(List<Double> probabilities, FaultEvent event) {
        checkArguments(probabilities, event);
        if (probabilities.size() == 1) {
            return probabilities.get(0);
        }

        boolean positiveFound = false;
        double value = 0;
        for (double p : probabilities) {
            if(p > 0) {
                if(positiveFound) {
                    log.debug("Another positive value found in XOR, return zero");
                    return 0;
                }

                positiveFound = true;
                value = p;
            }
        }

        return value;
    }
}
