package cz.cvut.kbss.analysis.exception;

import cz.cvut.kbss.analysis.model.FaultEvent;
import lombok.Getter;

import java.util.Map;

@Getter
public class CalculationException extends RuntimeException{

    private final String messageId;
    private Map<String, String> messageArguments;


    public CalculationException(String message) {
        super(message);
        messageId = null;
    }

    public CalculationException(String messageId, String message, Map<String, String> args){
        super(message);
        this.messageId = messageId;
        this.messageArguments = args;
    }

    public CalculationException(String message, Throwable cause) {
        super(message, cause);
        this.messageId = null;
    }

    public static CalculationException childProbabilityNotSet(FaultEvent event){
        return new CalculationException("error.faultEvent.childProbabilityNotSet",childProbabilityNotSetMessage(event), Map.of("event", event.getName(), "uri", event.getUri().toString()));
    }

    public static CalculationException probabilityNotSet(FaultEvent event){
        return new CalculationException("error.faultEvent.probabilityNotSet",probabilityNotSetMessage(event), Map.of("event", event.getName(), "uri", event.getUri().toString()));
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
