package cz.cvut.kbss.analysis.service.strategy.probability;

import cz.cvut.kbss.analysis.exception.CalculationException;
import cz.cvut.kbss.analysis.model.FaultEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public interface ProbabilityPropagationStrategy {


    default void checkArguments(Collection<?> probabilities, FaultEvent event) {
        if(probabilities.stream().filter(p -> p == null).map(p -> 0).findAny().isPresent() )
            throw CalculationException.childProbabilityNotSet(event);
    }

    double propagate(List<Double> probabilities, FaultEvent event) throws CalculationException;
}
