package cz.cvut.kbss.analysis.model.fta;

import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultEventScenario;
import cz.cvut.kbss.analysis.model.FaultTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
    public List<FaultEventScenario> extract(FaultTree faultTree){
        Consumer<Logger> errorMessage = validateTree(faultTree);
        if(errorMessage != null){
            errorMessage.accept(LOG);
            return null;
        }

        return extract(faultTree.getManifestingEvent()).stream()
                .filter(s -> !s.isEmptyScenario()).toList();
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
//
//
//    public static void main(String[] args) {
//        List<Integer> sizes = Stream.of(2,3).toList();
//        List<Integer> inds = new ArrayList<>(sizes.size());
//        sizes.forEach(s -> inds.add(0));
//        int i = 0;
//        while( i < inds.size()) {
//            while (i < inds.size()) {
//                int ii = inds.get(i) + 1;
//                if (ii < sizes.get(i)) {
//                    inds.set(i, ii);
//                    i = 0;
//                    break;
//                }
//                inds.set(i, 0);
//                i++;
//            }
//            System.out.println(inds);
//        }
//        System.out.println("END");
//    }

}
