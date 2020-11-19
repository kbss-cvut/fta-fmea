package cz.cvut.kbss.analysis.service.strategy;

import cz.cvut.kbss.analysis.model.util.GateType;
import cz.cvut.kbss.analysis.service.strategy.probability.ProbabilityPropagationStrategy;

import java.util.HashMap;
import java.util.Map;

public class GateStrategyFactory {

    private static final Map<GateType, ProbabilityPropagationStrategy> gatePropagationStrategyMap;

    static {
        ProbabilityPropagationStrategy andStrategy = new AndProbabilityPropagationStrategy();
        ProbabilityPropagationStrategy orStrategy = new OrProbabilityPropagationStrategy();

        gatePropagationStrategyMap = new HashMap<>();
        gatePropagationStrategyMap.put(GateType.AND, andStrategy);
        gatePropagationStrategyMap.put(GateType.OR, orStrategy);
        gatePropagationStrategyMap.put(GateType.XOR, new XorProbabilityPropagationStrategy(andStrategy, orStrategy));
        gatePropagationStrategyMap.put(GateType.INHIBIT, new InhibitProbabilityPropagationStrategy(andStrategy));

        // TODO PRIORITY_AND, INHIBIT
    }

    public static ProbabilityPropagationStrategy get(GateType gateType) {
        return gatePropagationStrategyMap.get(gateType);
    }

}
