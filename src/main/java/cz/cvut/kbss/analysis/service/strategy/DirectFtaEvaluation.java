package cz.cvut.kbss.analysis.service.strategy;

import cz.cvut.kbss.analysis.exception.CalculationException;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.fta.FtaEventType;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DirectFtaEvaluation {
    public Double evaluate(FaultEvent event){
        if (event.getEventType() == FtaEventType.INTERMEDIATE) {
            List<Double> childProbabilities = event.getChildren().stream()
                    .map(this::evaluate).collect(Collectors.toList());
            propagateProbabilities(event, childProbabilities);
            return event.getProbability();
        }
        return event.getProbability();
    }

    public void propagateProbabilities(FaultEvent event, List<Double> childProbabilities){
        try {
            double eventProbability = GateStrategyFactory.get(event.getGateType()).propagate(childProbabilities, event);
            event.setProbability(eventProbability);
        }catch (CalculationException ex){
            log.info(ex.getMessage());
        }
    }

    public void propagateProbabilities(FaultEvent event){
        List<Double> childProbabilities = event.getChildren().stream().map(FaultEvent::getProbability).toList();
        propagateProbabilities(event, childProbabilities);
    }
}
