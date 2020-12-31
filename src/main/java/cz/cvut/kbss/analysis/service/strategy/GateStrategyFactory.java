package cz.cvut.kbss.analysis.service.strategy;

import cz.cvut.kbss.analysis.model.util.GateType;
import cz.cvut.kbss.analysis.service.strategy.probability.ProbabilityPropagationStrategy;

import java.util.HashMap;
import java.util.Map;

public class GateStrategyFactory {

    private static final Map<GateType, ProbabilityPropagationStrategy> gatePropagationStrategyMap;

    static {
        ProbabilityPropagationStrategy andStrategy = new AndProbabilityPropagationStrategy();

        gatePropagationStrategyMap = new HashMap<>();
        gatePropagationStrategyMap.put(GateType.AND, andStrategy);
        gatePropagationStrategyMap.put(GateType.OR, new OrProbabilityPropagationStrategy());
        gatePropagationStrategyMap.put(GateType.XOR, new XorProbabilityPropagationStrategy());
        gatePropagationStrategyMap.put(GateType.INHIBIT, new InhibitProbabilityPropagationStrategy(andStrategy));
        gatePropagationStrategyMap.put(GateType.PRIORITY_AND, new PriorityAndProbabilityPropagationStrategy(andStrategy));
    }

    public static ProbabilityPropagationStrategy get(GateType gateType) {
        return gatePropagationStrategyMap.get(gateType);
    }

}
