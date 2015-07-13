package com.pb.sawdust.model.models;

import com.pb.sawdust.calculator.Function1;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.probability.AbstractDiscreteProbabilisticDistribution;
import com.pb.sawdust.util.probability.DiscreteProbabilisticDistribution;
import com.pb.sawdust.util.probability.MonteCarlo;
import com.pb.sawdust.util.collections.SetList;
import com.pb.sawdust.util.concurrent.DnCRecursiveAction;
import com.pb.sawdust.util.concurrent.ForkJoinPoolFactory;
import com.pb.sawdust.util.concurrent.IteratorAction;

import java.util.*;

/**
 * The {@code MonteCarloChoiceSelector} ...
 *
 * @author crf
 *         Started 4/12/12 1:25 PM
 */
public class MonteCarloChoiceSelector<C extends Choice> extends AbstractChoiceSelector<C> {
    private final Random random;

    public MonteCarloChoiceSelector(Random random) {
        this.random = random;
    }

    private class MonteCarloChoiceAction extends DnCRecursiveAction {
        private static final long serialVersionUID = 7436146403766985808L;

        private final Choice[] results;
        private final DiscreteProbabilisticDistribution[] distributions;
        private final Object[] choices;

        private MonteCarloChoiceAction(Choice[] results, DiscreteProbabilisticDistribution[] distributions, Object[] choices, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.results = results;
            this.distributions = distributions;
            this.choices = choices;
        }

        protected MonteCarloChoiceAction(Choice[] results, List<DiscreteProbabilisticDistribution> distributions, SetList<C> choices) {
            super(0,results.length);
            this.results = results;
            this.distributions = distributions.toArray(new DiscreteProbabilisticDistribution[distributions.size()]);
            this.choices = new Object[choices.size()];
            int counter = 0;
            for (C choice : choices)
                this.choices[counter++] = choice;
        }

        @Override
        @SuppressWarnings("unchecked") //this will be a "C"; choices as Object[] (instead of C[]) is due to inability to build generic arrays
        protected void computeAction(long start, long length) {
            long end = start + length;
            for (int i = (int) start; i < end; i++)
                results[i] = (C) choices[new MonteCarlo(distributions[i],random).draw()];
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new MonteCarloChoiceAction(results,distributions,choices,start,length,next);
        }

        @Override
        protected boolean continueDividing(long length) {
            return length > 5000 && getSurplusQueuedTaskCount() <= 3;
        }
    }

    public List<C> selectChoices(Map<C,DoubleVector> probabilities) {
        SetList<C> choices = new LinkedSetList<>(probabilities.keySet());
        List<DiscreteProbabilisticDistribution> distributions = new LinkedList<>();
        for (int i : range(probabilities.values().iterator().next().size(0)))
            distributions.add(new ProbabilityMapDistribution(probabilities,choices,i));
        return selectChoices(choices,distributions);
    }

    private class ProbabilityMapDistribution extends AbstractDiscreteProbabilisticDistribution {
        private final List<C> choiceOrder;
        private final DoubleVector[] baseProbabilities;
        private final int position;

        private ProbabilityMapDistribution(Map<C,DoubleVector> baseProbabilities, List<C> choiceOrder, int position) {
            this.baseProbabilities = new DoubleVector[choiceOrder.size()];
            int counter = 0;
            for (C choice : choiceOrder)
                this.baseProbabilities[counter++] = baseProbabilities.get(choice);
            this.choiceOrder = choiceOrder;
            this.position = position;
        }

        @Override
        public int getLength() {
            return choiceOrder.size();
        }

        @Override
        public double getCumulativeValue(int point) { //todo: not efficient; maybe should cache?
            double sum = 0.0;
            for (int i : range(point+1))
                sum += getProbability(i);
            return sum;
        }

        @Override
        public double getProbability(int point) {
            return baseProbabilities[point].getCell(position);
        }

        @Override
        public double[] getDistribution() {
            double[] distribution = new double[getLength()];
            for (int i : range(distribution.length))
                distribution[i] = baseProbabilities[i].getCell(position);
            return distribution;
        }
    }

    public List<C> selectChoices(SetList<C> choices, List<DiscreteProbabilisticDistribution> distributions) {
        Choice[] c = new Choice[distributions.size()];
        DnCRecursiveAction action = new MonteCarloChoiceAction(c,distributions,choices);
        ForkJoinPoolFactory.getForkJoinPool().execute(action);
        action.getResult();
        List cList =  Arrays.asList(c);
        @SuppressWarnings("unchecked")
        List<C> choiceList = (List<C>) cList;
        return choiceList;
    }

    @Override
    public List<C> selectChoices(SetList<C> choices, Iterable<DiscreteProbabilisticDistribution> distributions) {
        Function1<MonteCarloFunctionContainer,Void> monteCarloFunction = new Function1<MonteCarloFunctionContainer,Void>() {
            @Override
            public Void apply(MonteCarloFunctionContainer c) {
                c.choiceArrayHolder[0][c.location] = c.orderedChoices.get(c.monteCarlo.draw());
                return null;
            }
        };
        List<MonteCarloFunctionContainer> containers = new LinkedList<>();
        int counter = 0;
        final Object[][] choiceArrayHolder = new Object[1][];
        for (DiscreteProbabilisticDistribution distribution : distributions)
            containers.add(new MonteCarloFunctionContainer(distribution,counter++,choiceArrayHolder,choices));
        choiceArrayHolder[0] = new Object[counter]; //have to do double array so we can intialize this after iterating all of the way through
        IteratorAction<MonteCarloFunctionContainer> action =  new IteratorAction<>(containers,monteCarloFunction);
        ForkJoinPoolFactory.getForkJoinPool().execute(action);
        action.waitForCompletion();
        List list = Arrays.asList(choiceArrayHolder[0]);
        @SuppressWarnings("unchecked") //this is valid because we are only putting C's in the holder, but because we are using arrays, there is no generics allowed
        List<C> choiceList = (List<C>) list;
        return choiceList;
    }

    private class MonteCarloFunctionContainer {
        private final MonteCarlo monteCarlo;
        private final int location;
        private final Object[][] choiceArrayHolder;
        private final SetList<C> orderedChoices;

        private MonteCarloFunctionContainer(DiscreteProbabilisticDistribution distribution, int location, Object[][] choiceArrayHolder, SetList<C> orderedChoices) {
            this.choiceArrayHolder = choiceArrayHolder;
            this.orderedChoices = orderedChoices;
            monteCarlo = new MonteCarlo(distribution,random);
            this.location = location;
        }
    }
}
