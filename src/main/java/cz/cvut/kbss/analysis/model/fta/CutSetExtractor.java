package cz.cvut.kbss.analysis.model.fta;

import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultEventScenario;
import cz.cvut.kbss.analysis.model.FaultTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CutSetExtractor {
    private static final Logger LOG = LoggerFactory.getLogger(CutSetExtractor.class);


    /**
     *
     * @return null if the input is valid to extract cut sets from the fault tree, otherwise returns error message
     */
    public Consumer<Logger> validateTree(FaultTree faultTree){
        if(faultTree == null)
            return (l) -> l.warn("invalid input - input tree is null");
        if(faultTree.getManifestingEvent() == null)
            return (l) -> l.warn("invalid input - input tree is <{}> does not have a root event.", faultTree.getUri());


        return null;
    }

    public List<FaultEventScenario> extractMinimalScenarios(FaultTree faultTree){
        Consumer<Logger> errorMessage = validateTree(faultTree);
        if(errorMessage != null){
            errorMessage.accept(LOG);
            return null;
        }
        List<FaultEventScenario> scenarios = extract(faultTree.getManifestingEvent()).stream()
                .filter(s -> !s.isEmptyScenario()).toList();
        scenarios = extractMinimalScenarios(scenarios);
        return scenarios;
    }

    public List<FaultEventScenario> extractMinimalScenarios(List<FaultEventScenario> allScenarios){
        Map<FaultEvent, List<FaultEventScenario>> map = new HashMap<>();
        Set<FaultEventScenario> nonMinimalScenarios = new HashSet<>();

        for(int i = 0; i < allScenarios.size() ; i ++){
            for(FaultEvent faultEvent : allScenarios.get(i).getScenarioParts()) {
                List<FaultEventScenario> feScenarios = map.get(faultEvent);
                if(feScenarios == null){
                    feScenarios = new ArrayList<>();
                    map.put(faultEvent, feScenarios);
                }
                feScenarios.add(allScenarios.get(i));
            }
        }

        for(Map.Entry<FaultEvent, List<FaultEventScenario>> e : map.entrySet()){
            if(e.getValue().size() < 1)
                continue;
            List<FaultEventScenario> scenarios = e.getValue()
                    .stream().filter(fes -> !nonMinimalScenarios.contains(fes))
                    .sorted(Comparator.comparing((FaultEventScenario fes) -> fes.getScenarioParts().size()).reversed())
                    .toList();

            for( int i = 0; i < scenarios.size() - 1; i ++ ){
                for( int j = i + 1; j < scenarios.size(); j ++ ){
                    if(scenarios.get(i).contains(scenarios.get(j))){
                        nonMinimalScenarios.add(scenarios.get(i));
                        break;
                    }
                }
            }
        }
        List<FaultEventScenario> minimalScenarios = new ArrayList<>(allScenarios);
        minimalScenarios.removeAll(nonMinimalScenarios);
        return minimalScenarios;
    }


    public List<FaultEventScenario> extract(FaultEvent faultEvent){
        if(faultEvent.getGateType() == null || faultEvent.getChildren() == null || faultEvent.getChildren().isEmpty())
            return Collections.singletonList(new FaultEventScenario(Collections.singleton(faultEvent)));

        Stream<List<FaultEventScenario>> partScenariosStream = faultEvent.getChildren().stream().map(this::extract);// RECURSION !

        if(GateType.OR.equals(faultEvent.getGateType()))
            return partScenariosStream.flatMap(l -> l.stream()).toList();

        if(!GateType.AND.equals(faultEvent.getGateType()))
            throw new IllegalArgumentException(String.format("Cannot extract cut sets from fault tree, tree contains unsupported gate type \"%s\"", faultEvent.getGateType()));

        List<List<FaultEventScenario>> partScenarios = partScenariosStream.filter(l -> !l.isEmpty()).toList();
        return processAndGateScenarios(partScenarios);
    }

    protected List<FaultEventScenario> processAndGateScenarios(List<List<FaultEventScenario>> partScenarios){
        List<Integer> inds = new ArrayList<>(partScenarios.size());
        partScenarios.forEach(l -> inds.add(0));
        List<FaultEventScenario> andScenarios = new ArrayList<>();
        int i = 0;
        while( i < inds.size()){
            //create and add new scenario
            FaultEventScenario mergedScenario = new FaultEventScenario(new HashSet<>());
            for(int j = 0; j < inds.size(); j ++ )
                mergedScenario.getScenarioParts()
                        .addAll(partScenarios.get(j).get(inds.get(j)).getScenarioParts());
            andScenarios.add(mergedScenario);

            //goto next combination
            while(i < inds.size()){
                int ii = inds.get(i) + 1;
                if(ii < partScenarios.get(i).size()) {
                    inds.set(i, ii);
                    i = 0;
                    break;
                }
                inds.set(i, 0);
                i ++;
            }
        }
        return andScenarios;
    }
}
