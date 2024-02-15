package cz.cvut.kbss.analysis.model.fta;

import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultEventScenario;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CutSetExtractorTest {

    @Test
    void testExtractMinimalScenarios_ScenarioList() {
        List<FaultEventScenario> faultEventScenarios = Arrays.asList(
                create(1),
                create(2,4),
                create(3,4,5),
                create(2,4,5)
        );

        testExtractionOfMinimalFaultEvent(faultEventScenarios, set(0,1, 2));

        faultEventScenarios = Arrays.asList(
                create(1),
                create(2,4,5),
                create(2,4),
                create(3,4,5),
                create(2,4,5),
                create(3,4,5,6)
        );

        testExtractionOfMinimalFaultEvent(faultEventScenarios, set(0,2,3));
    }

    void testExtractionOfMinimalFaultEvent(List<FaultEventScenario> faultEventScenarios, Set<Integer> extractedScenarios){
        List<FaultEventScenario> minimalFaultEventScenarios = new CutSetExtractor().extractMinimalScenarios(faultEventScenarios);
        assertEquals(extractedScenarios.size(), minimalFaultEventScenarios.size());

        for(int i = 0; i < faultEventScenarios.size(); i ++){
            FaultEventScenario scenario = faultEventScenarios.get(i);
            if(extractedScenarios.contains(i))
                assertTrue(minimalFaultEventScenarios.contains(scenario),
                        String.format("Scenario at index %d \"%s\" should a part of the result of " +
                                "minimalFaultEventScenarios but it isn't.", i, scenario.getScenarioParts().toString()));
            else
                assertFalse(minimalFaultEventScenarios.contains(scenario),
                        String.format("Scenario at index %d \"%s\" should not be a part of the result of " +
                                "minimalFaultEventScenarios but it is.", i, scenario.getScenarioParts().toString()));
        }
    }

    private FaultEventScenario create(Integer ... ints){
        return new FaultEventScenario(Stream.of(ints).map(this::createFaultEvent).collect(Collectors.toSet()));
    }

    private FaultEvent createFaultEvent(int i){
        FaultEvent fe = new FaultEvent();
        fe.setUri(createURI(i));
        return fe;
    }
    private URI createURI(int i){
        return URI.create("http://" + i);
    }

    private Set<Integer> set(Integer ... ints){
        return Stream.of(ints).collect(Collectors.toSet());
    }
}