package cz.cvut.kbss.analysis.model.fta;

import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultEventScenario;
import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.service.strategy.DirectFtaEvaluation;
import cz.cvut.kbss.analysis.service.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FTAMinimalCutSetEvaluation {

    protected CutSetExtractor cutSetExtractor = new CutSetExtractor();
    protected DirectFtaEvaluation directFtaEvaluation = new DirectFtaEvaluation();
    protected List<FaultEventScenario> minScenarios;

    /**
     * Evaluate fault tree probability of top and intermediate nodes and extract min cut sets of top event.
     * If parts of top or intermediate events are not independent minimal cut set evaluation method is used. Direct
     * evaluation is used otherwise.
     *
     * @apiNote this method is not thread safe
     * @param faultTree
     */
    public void evaluate(FaultTree faultTree){
        minScenarios = null;
        evaluate(faultTree.getManifestingEvent());

        if(minScenarios == null)
            minScenarios = cutSetExtractor.extractMinimalScenarios(faultTree.getManifestingEvent());

        faultTree.setFaultEventScenarios(new HashSet<>(minScenarios));
    }


    /**
     * Traverse the input faultEvent and its parts and evaluate their probabilities.
     * If parts of faultEvent are dependent minimal cut-set evaluation method is used. Direct method is used otherwise.
     * @param faultEvent
     * @return a pair where left is true if faultEvent contains dependent events, false otherwise. Right contains the set of all basic events in the subtree of faultEvent.
     */
    public Pair<Boolean, Set<FaultEvent>> evaluate(FaultEvent faultEvent){
        if(faultEvent.getEventType() == FtaEventType.BASIC || faultEvent.getChildren() == null || faultEvent.getChildren().isEmpty())
            return Pair.of(false, Collections.singleton(faultEvent));

        List<Pair<Boolean, Set<FaultEvent>>> l = faultEvent.getChildren().stream().map(this::evaluate).collect(Collectors.toList());// RECURSION !for()
        boolean dependent = l.stream().anyMatch(p -> p.getFirst());

        if(!dependent)// if some of the child events are dependent faultEvent is also dependent
            dependent = isDepended(l.stream().map(p -> p.getSecond()).collect(Collectors.toList()));

        if(dependent){ // evaluate using minimal cut sets method
            minScenarios = cutSetExtractor.extractMinimalScenarios(faultEvent);
            double prob = evaluate(minScenarios);
            faultEvent.setProbability(prob);
        }else{//evaluate using direct method
            directFtaEvaluation.propagateProbabilities(faultEvent);
        }
        Set<FaultEvent> references = new HashSet<>();
        l.forEach(p -> references.addAll(p.getSecond()));
        return  Pair.of(dependent, references) ;
    }

    /**
     *
     * @param childReferences
     * @return true if there is a FaultEvent present in at least two fault event sets in   childReferences, false otherwise
     */
    public boolean isDepended(List<Set<FaultEvent>> childReferences){

        Set<FaultEvent> set = new HashSet<>();
        for(int i = 0; i < childReferences.size() ; i ++)
            for(FaultEvent fe : childReferences.get(i))
                if(!set.add(fe))
                    return true;
        return false;
    }

    /**
     * Evaluate probability of minScenarios. Evaluation processes the OR of ANDs of FaultEventScenario.scenarioParts
     * @param minScenarios
     * @return probability of provided minScenarios list
     */
    public Double evaluate(List<FaultEventScenario> minScenarios){
        // This should be simple optimization, it should not cause different results
        minScenarios.sort(Comparator.comparing(s -> s.getScenarioParts().size()));

        OpExpression sceExpression = or(minScenarios.stream().map( s -> and(s)).collect(Collectors.toList()));
        log.trace(sceExpression.toString());

        Expression transformed = sceExpression.transform();
        log.trace(transformed.toString());

        return transformed.evaluate();
    }

    /**
     * Converts scenario.scenarioParts to a list of variables
     * @param scenario
     * @return list of variables corresponding to scenario.scenarioParts
     */
    protected static List<Var> toVars(FaultEventScenario scenario){
        return scenario.getScenarioParts().stream()
                .map(fe -> var(fe)).toList();
    }

    /**
     * Construct an AND OpExpression from scenario.scenarioParts
     * @param scenario
     * @return
     */
    protected static OpExpression and(FaultEventScenario scenario){
        return and(toVars(scenario));
    }

    /**
     * Construct an AND OpExpression from args
     * @param args
     * @return
     */
    protected static OpExpression and(Expression ... args){
        return and(Stream.of(args).collect(Collectors.toList()));
    }

    /**
     * Construct an AND OpExpression from args
     * @param args
     * @return
     */
    protected static OpExpression and(List<? extends Expression> args){
        return new OpExpression(args, GateType.AND);
    }

    /**
     * Merge args into a list, keep only one occurrence of each variable and construct AND OpExpression
     * @param args
     * @return
     */
    protected static OpExpression and(List<? extends Expression> ... args){
        List<? extends Expression> merged = Stream.of(args).flatMap(List::stream).distinct().collect(Collectors.toList());
        return and(merged);
    }


    /**
     * Construct an OR OpExpression from args
     * @param args
     * @return
     */
    protected static OpExpression or(Expression ... args){
        return or(Stream.of(args).distinct().collect(Collectors.toList()));
    }

    /**
     * Construct an OR OpExpression from args
     * @param args
     * @return
     */
    protected static OpExpression or(List<? extends Expression> args){
        return new OpExpression(args, GateType.OR);
    }

    /**
     * Construct Var from fe
     * @param fe
     * @return
     */
    static Var var(FaultEvent fe){
        return new Var(false, fe);
    }

    /**
     * Construct negated Var from fe
     * @param fe
     * @return
     */
    static Var not(FaultEvent fe){
        return new Var(true, fe);
    }



    static abstract class Expression{
        /**
         * negate the expression
         * @return new expression which is a logical negation of this expression
         */
        abstract Expression not();

        /**
         * evaluate probability of the expression. Requires that the grounded terms in the expression,
         * i.e. the Vars, have specified FaultEvent with a specified probability.
         * @return calculated probability
         */
        abstract Double evaluate();

        /**
         * Apply Shannon decomposition of this expression, convert to normal disjunctive form.
         * @return the converted expression
         */
        abstract Expression transform();

        /**
         * @return the direct arguments of the expression. If this a Var expression, returns this. IF this is
         * OpExpression it returns this.expressions
         */
        abstract List<? extends Expression> args();

        /**
         * @return list of Vars in this expression
         */
        abstract List<Var> vars();
    }

    static class OpExpression extends Expression {
        List<? extends Expression> expressions;
        GateType op;

        public OpExpression(List<? extends Expression> expressions, GateType op) {
            this.expressions = expressions;
            this.op = op;
        }

        @Override
        public Expression not(){
            switch (op){
                case OR: return and(expressions.stream().map(Expression::not).toList());
                case AND: return or(expressions.stream().map(Expression::not).toList());
                default: return null;
            }
        }

        /**
         * Assumes that this expression is in disjunctive form.
         * @return
         */
        @Override
        Expression transform() {
            if(op == GateType.AND)
                throw new UnsupportedOperationException("Cannot transform OpExpression with AND operator. Expected Expression in disjunctive form" );
            List<Expression> rexpArgs = new ArrayList<>();
            rexpArgs.add(expressions.get(expressions.size() - 1));

            for(int i = expressions.size() - 2; i > -1; i --){
                Expression lexp = expressions.get(i);
                Expression notLexp = lexp.not().transform();
                OpExpression t = multiplyAndSimplify(notLexp, or(rexpArgs));

                rexpArgs = new ArrayList<>();
                rexpArgs.add(lexp);
                rexpArgs.addAll(t.expressions);
            }

            return or(rexpArgs);
        }

        /**
         *
         * @param l expected in disjunctive form
         * @param r expected in disjunctive form
         * @return expression obtained converting l AND r into disjunctive form and removing clauses that contain contradiction (e.g. A and !A)
         */
        protected OpExpression multiplyAndSimplify(Expression l, Expression r){
            ArrayList<Expression> expArgs = new ArrayList<>();

            Map<Expression, Map<FaultEvent, Var>> mapOfMaps = new HashMap<>();
            r.args().forEach(e -> mapOfMaps.put(e, createMap(e)));

            for(Expression lexp : l.args()){
                Map<FaultEvent, Var> lmap = createMap(lexp);
                if(lmap == null)// filter clauses containing contradiction
                    continue;

                for(Expression rexp : r.args()){
                    Map<FaultEvent, Var> rmap = mapOfMaps.get(rexp);
                    if(rmap == null || isMergeContradiction(lmap, rmap)) // filter clauses containing contradiction
                        continue;

                    Expression expPart = and(lexp.args(), rexp.args());
                    expArgs.add(expPart);
                }
            }

            return new OpExpression(expArgs, GateType.OR);
        }

        /**
         * An auxiliary function that extracts a map between events and their usage in Var expressions in the input AND
         * clause exp.
         * @param exp assuming this is a Var or AND of Vars
         * @return map of FaultEvent to Var or null if the input AND clause exp contains a contradiction, e.g. (A and !A)
         */
        protected Map<FaultEvent, Var> createMap(Expression exp){
            Map<FaultEvent, Var> map = new HashMap<>();
            for(Var v : exp.vars()){
                Var otherV = map.put(v.event, v);
                if(otherV != null && otherV.not !=  v.not)
                    return null;
            }
            return map;
        }

        /**
         * Assumes inputs are cross-reference maps of FaultEvents to Vars in two AND of Vars clauses.
         * @param m1
         * @param m2
         * @return true is there is v1 in m1 and v2 in m2 such that v1 = !v2, false otherwise
         */
        protected boolean isMergeContradiction(Map<FaultEvent, Var> m1, Map<FaultEvent, Var> m2){
            return m1.entrySet().stream().anyMatch(e -> {
                Var otherV = m2.get(e.getKey());
                if(otherV != null && otherV.not != e.getValue().not)
                    return true;
                return false;
            });
        }

        @Override
        Double evaluate() {
            switch (op){
                case OR: return expressions.stream().mapToDouble(Expression::evaluate).sum();
                case AND: return expressions.stream().mapToDouble(Expression::evaluate).reduce(1, (d1, d2) -> d1*d2);
                default: return null;
            }
        }

        @Override
        List<? extends Expression> args() {
            return expressions;
        }

        @Override
        List<Var> vars() {
            return expressions.stream().flatMap(e -> e.vars().stream()).distinct().toList();
        }

        public String toString(){
            return String.format("(%s)", expressions.stream().map(Object::toString).collect(Collectors.joining(op == GateType.AND ? "*" : "+")));
        }
    }

    static class Var extends Expression{
        boolean not;
        FaultEvent event;

        List<Var> args = Collections.singletonList(this);

        public Var(boolean not, FaultEvent event) {
            this.not = not;
            this.event = event;
        }

        @Override
        Var not(){
            return new Var(!not, event);
        }

        @Override
        Expression transform() {
            return this;
        }

        @Override
        Double evaluate() {
            if(not)
                return 1 - event.getProbability();
            return event.getProbability();
        }

        @Override
        List<? extends Expression> args() {
            return args;
        }

        @Override
        List<Var> vars() {
            return args;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Var var)) return false;
            return not == var.not && Objects.equals(event, var.event);
        }

        @Override
        public int hashCode() {
            return Objects.hash(not, event);
        }

        public String toString(){
            return (not ? "!" : "") + event.getName();
        }
    }
}
