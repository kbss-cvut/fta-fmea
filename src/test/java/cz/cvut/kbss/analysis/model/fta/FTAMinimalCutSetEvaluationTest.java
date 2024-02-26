package cz.cvut.kbss.analysis.model.fta;

import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultTree;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static cz.cvut.kbss.analysis.model.fta.FTAMinimalCutSetEvaluation.*;
import static org.junit.jupiter.api.Assertions.*;

class FTAMinimalCutSetEvaluationTest {


    static List<Var> vars = IntStream.range(0,6).mapToObj(i -> (char)('a' + i) + "").map(n ->{
        FaultEvent fe = new FaultEvent();
        fe.setProbability(0.1);
        fe.setName(n);
        return fe;
    }).map(f -> var(f)).collect(Collectors.toList());

    static List<TestParams> expressions(){
        return Stream.of(
                new TestParams(
                        or(vars),
                        "(a+(!a*b)+(!a*!b*c)+(!a*!b*!c*d)+(!a*!b*!c*!d*e)+(!a*!b*!c*!d*!e*f))",
                            0.1 + (1-0.1)*0.1 + (1-0.1)*(1-0.1)*0.1 + (1-0.1)*(1-0.1)*(1-0.1)*0.1 + (1-0.1)*(1-0.1)*(1-0.1)*(1-0.1)*0.1 + (1-0.1)*(1-0.1)*(1-0.1)*(1-0.1)*(1-0.1)*0.1
                ),
                new TestParams(or(vars.get(0), and(vars.get(1),vars.get(2))), "(a+(!a*b*c))", 0.1 + (1-0.1)*0.1*0.1),
                new TestParams(or(and(vars.get(0),vars.get(1)), vars.get(2)), "((a*b)+(!a*c)+(a*!b*c))", 0.01 + (1-0.1)*0.1 + 0.1*(1-0.1)*0.1),
                new TestParams(or(vars.get(0), and(vars.get(1), vars.get(3)), and(vars.get(2), vars.get(3), vars.get(4))), "(a+(!a*b*d)+(!a*!b*c*d*e))",
                        0.1 + 0.9*0.1*0.1 + 0.9*0.9*0.1*0.1*0.1)
        ).collect(Collectors.toList());
    }

    static class TestParams{

        public TestParams(OpExpression exp, String expectedSerialization, Double expectedValue) {
            this.exp = exp;
            this.expectedSerialization = expectedSerialization;
            this.expectedValue = expectedValue;
        }

        OpExpression exp;
        String expectedSerialization;
        Double expectedValue;
    }

    @ParameterizedTest
    @MethodSource("cz.cvut.kbss.analysis.model.fta.FTAMinimalCutSetEvaluationTest#expressions")
    void testExpression_transform(TestParams p){
        Expression exp = p.exp.transform();
        String expStr = exp.toString();

        assertEquals(p.expectedSerialization, expStr);
        double actual = exp.evaluate();
        assertTrue(actual == p.expectedValue || Math.abs(p.expectedValue - exp.evaluate())  < 0.0000000000000001);
    }

    @Test
    void testExpression_Evaluate(){
        double res = and(vars.get(0), vars.get(1)).evaluate();
        Double expected = 0.1*0.1;
        System.out.println(Math.abs(expected - res));
        System.out.println(Double.MIN_VALUE);
        assertTrue(Math.abs(expected - res) < Double.MIN_VALUE);
    }


    @Test
    void test_evaluate_fault_tree(){
        FaultTree faultTree = new FaultTree();
        List<FaultEvent> bs = IntStream.range(0,5).mapToObj(i -> (char)('a' + i) + "").map(n -> {
            FaultEvent fe = new FaultEvent();
            fe.setProbability(0.1);
            fe.setName(n);
            fe.setEventType(FtaEventType.BASIC);
            return fe;
        }).collect(Collectors.toList());

        List<FaultEvent> is = IntStream.range(0,6).mapToObj(i -> "G" + i).map(n -> {
            FaultEvent fe = new FaultEvent();
            fe.setName(n);
            fe.setEventType(FtaEventType.INTERMEDIATE);
            return fe;
        }).collect(Collectors.toList());

        faultTree.setManifestingEvent(is.get(0));
        is.get(0).setGateType(GateType.OR);
        is.get(0).setChildren(Stream.of(is.get(1), is.get(2)).collect(Collectors.toSet()));

        is.get(1).setGateType(GateType.AND);
        is.get(1).setChildren(Stream.of(is.get(3), is.get(4)).collect(Collectors.toSet()));

        is.get(3).setGateType(GateType.OR);
        is.get(3).setChildren(Stream.of(bs.get(1), bs.get(2)).collect(Collectors.toSet()));

        is.get(4).setGateType(GateType.AND);
        is.get(4).setChildren(Stream.of(bs.get(3), bs.get(4)).collect(Collectors.toSet()));

        is.get(2).setGateType(GateType.OR);
        is.get(2).setChildren(Stream.of(bs.get(0), is.get(5)).collect(Collectors.toSet()));

        is.get(5).setGateType(GateType.AND);
        is.get(5).setChildren(Stream.of(bs.get(1), bs.get(3)).collect(Collectors.toSet()));

        FTAMinimalCutSetEvaluation sut = new FTAMinimalCutSetEvaluation();
        sut.evaluate(faultTree);

        assertNotNull(faultTree.getFaultEventScenarios());
        assertEquals(3, faultTree.getFaultEventScenarios().size());

        Set<Set<FaultEvent>> scenarios = faultTree.getFaultEventScenarios().stream().map(s -> s.getScenarioParts()).collect(Collectors.toSet());
        assertTrue(scenarios.contains(Stream.of(bs.get(0)).collect(Collectors.toSet())));
        assertTrue(scenarios.contains(Stream.of(bs.get(1),bs.get(3)).collect(Collectors.toSet())));
        assertTrue(scenarios.contains(Stream.of(bs.get(2),bs.get(3), bs.get(4)).collect(Collectors.toSet())));

        for(FaultEvent fe : is)
            assertNotNull(fe.getProbability());

        double maxError = 0.0000000000000001;
        TestParams tp = expressions().get(3);

        double actual = is.get(0).getProbability();
        double expected = tp.expectedValue;
        assertTrue(Math.abs(actual - expected) < maxError, String.format("Actual probability of node %s significantly deviates from expected, \n%f \n%f", is.get(0).getName(), actual, expected));

        actual = is.get(1).getProbability();
        expected = (1 - 0.9*0.9)*(0.1*0.1);
        assertTrue(Math.abs(actual - expected) < maxError, String.format("Actual probability of node %s significantly deviates from expected, \n%f \n%f", is.get(1).getName(), actual, expected));

        actual = is.get(2).getProbability();
        expected = (1 - (1-0.1)*(1 - 0.1*0.1));
        assertTrue(Math.abs(actual - expected) < maxError, String.format("Actual probability of node %s significantly deviates from expected, \n%f \n%f", is.get(2).getName(), actual, expected));

        actual = is.get(3).getProbability();
        expected = 1 - 0.9*0.9;
        assertTrue(Math.abs(actual - expected) < maxError, String.format("Actual probability of node %s significantly deviates from expected, \n%f \n%f", is.get(3).getName(), actual, expected));

        actual = is.get(4).getProbability();
        expected = (0.1*0.1);
        assertTrue(Math.abs(actual - expected) < maxError, String.format("Actual probability of node %s significantly deviates from expected, \n%f \n%f", is.get(4).getName(), actual, expected));

        actual = is.get(5).getProbability();
        expected = (0.1*0.1);
        assertTrue(Math.abs(actual - expected) < maxError, String.format("Actual probability of node %s significantly deviates from expected, \n%f \n%f", is.get(5).getName(), actual, expected));
    }
}