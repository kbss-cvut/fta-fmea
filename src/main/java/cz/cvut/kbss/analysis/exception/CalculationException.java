package cz.cvut.kbss.analysis.exception;

import cz.cvut.kbss.analysis.model.FaultEvent;

public class CalculationException extends RuntimeException{
    public CalculationException() {
    }

    public CalculationException(String message) {
        super(message);
    }

    public CalculationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static CalculationException childProbabilityNotSet(FaultEvent event){
        return new CalculationException(childProbabilityNotSetMessage(event));
    }

    public static CalculationException probabilityNotSet(FaultEvent event){
        return new CalculationException(probabilityNotSetMessage(event));
    }

    public static String probabilityNotSetMessage(FaultEvent event){
        return String.format("FTA probability calculation - Event, \"%s\"<%s>, has no specified probability and is used to calculate probability of parent event.",
                event.getName(),
                event.getUri());
    }

    public static String childProbabilityNotSetMessage(FaultEvent event){
        return String.format("FTA probability calculation - Cannot calculate probability of \"%s\"<%s>, some child events have no specified probability.",
                event.getName(),
                event.getUri());
    }
}
