package com.pb.sawdust.popsynth.em;

import com.pb.sawdust.calculator.Function1;
import com.pb.sawdust.geography.*;
import com.pb.sawdust.io.FileUtil;
import com.pb.sawdust.util.probability.Discretizer;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.util.StatsUtil;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.concurrent.IteratorAction;

import java.util.*;
import java.util.concurrent.ForkJoinPool;

/**
 * The {@code EntropyMaximizationPopSynth} is a marginal-balancing population synthesis procedure which can balance targets
 * at various geographic and participation levels. In other words, targets which are aggregated at different geography levels
 * for different dimensions can be accomodated, as can dimension categories which may have different aggregation rates for
 * each dimension category. As an example of the latter, if the categories are number of children and number of adults across
 * the region, then a childless two-person household would contribute 0 to the child category and 2 to the adult category,
 * whereas a 4 person single parent household would contribute 3 to the child category and 1 to the adult category.
 * <p>
 * The procedure uses an iterative balancing algorithm where the weights in a given dimension are updated by the following
 * formula:
 * <p>
 * <code>W<sub>i+1</sub> = W<sub>i</sub>*F<sup>p</sup></code>
 * <p>
 * where <code>W<sub>i</sub></code> is the weight at the ith iteration, <code>P</code> is the participation rate for the
 * element, and <code>F</code> is the adjustment factor (target divided by current total).
 * <p>
 * Rather than using the entire source population as a seed for the procedure, each balance operation randomly selects a
 * group of elements which first cover all possible categories in the various dimensions, and second match the size of the
 * target population (in number, not in weight sum). This allows the use of this representative sample directly after the
 * balancing procedure has finished (including discretization), removing the extra step of Monte Carlo selection if all
 * possible elements were balanced against. This, however, may increase the possibility that convergence may not be achieved,
 * so the procedure will retry the balancing procedure on any unit that did not converge; the number of retries is specified
 * using {@link #getConvergenceRetries()}.
 * <p>
 * The procedure will attempt to break the balance procedure into the smallest independent units possible. These units are
 * determined by the relationships between geographies of the various dimensions. If one geography element for a given dimension
 * overlaps two from another, then those two must be included in the same balancing unit as the first. Applying this overlapping
 * rule yields a series of geographic neighborhoods, each of which is independent of the others. Note that it is possible
 * to create a set of geographies where the smallest neighborhood is the entire region, though such a situation should be
 * avoided as it will probably prove inefficient. Every instance of {@code EntropyMaximizationPopSynth} must select a base
 * geography, which is the smallest geography amongst those available, against which all other geographies will be referenced.
 * <p>
 * Since each neighborhood balance unit is independent, the procedure will attempt to run the balancing procedures in parallel,
 * automatically scaling depending on the number of processors in the machine. (The fork-join framework is used to invoke
 * this parallelism, and its defaults for concurrency levels are passed through to this procedure.) Additionally, once the
 * procedure is finished, summaries for both the individual units as well as the entire region are available, allowing the
 * user to determine the performance of the procedure on the particular problem.
 * <p>
 * The methods that need to be implemented are those that define the specifics of the geographies, constraints, classifications,
 * and elements of the specific balance implementation.
 *
 * @param <G>
 *        The type of geography element in the basis geography of this procedure.
 *
 * @param <E>
 *        The type of balance element used in this procedure.
 *
 * @author crf
 *         Started 10/10/11 2:33 PM
 */
public abstract class EntropyMaximizationPopSynth<G extends GeographyElement<?>,E extends BalanceElement> {
    /**
     * The default convergence criterion.
     */
    public static final double DEFAULT_CONVERGENCE_CRITERION = 0.0001;

    /**
     * The default maximum iterations for the convergence procedure.
     */
    public static final int DEFAULT_MAXIMUM_ITERATIONS = 1000;

    /**
     * The default maximum number of convergence retries.
     */
    public static final int DEFAULT_MAXIMUM_CONVERGENCE_RETRIES = 5;

    /**
     * Get the base geography for the population synthesizer.
     *
     * @return the population synthesizer's base geography.
     */
    public abstract Geography<?,G> getBaseGeography();

    /**
     * Get a set of geographic mappings which define the relationships between the different geographies used in this
     * population synthesizer. If there are geographies used in this population synthesizer which cannot be related via
     * these mappings to the base geography, an error will result when trying to run the balance procedure.
     *
     * @return a set of geographic mappings defining the relationships between the populations synthesizer's geographies.
     */
    public abstract Set<GeographicMapping<?,?>> getGeographicMappings();

    /**
     * Get a data row holding the targets for the specified geography element. This is used in concert with {@link BalanceDimensionClassifier#getTargetMap(com.pb.sawdust.tabledata.DataRow)}
     * (and {@link com.pb.sawdust.popsynth.em.BalanceDimensionClassifier#getTargetFields()} to generate target values for
     * the specified geography element.
     *
     * @param geography
     *        The geography that the element belongs to.
     *
     * @param geographyElement
     *        The geography element in question.
     *
     * @param <G>
     *        The type of {@code geographyElement}.
     *
     * @return a data row holding the target values for {@code geographyElement}.
     */
    public abstract <G extends GeographyElement<?>> DataRow getTargets(Geography<?,G> geography, G geographyElement);

    /**
     * Get the balance dimension classifiers used in this population synthesizer. There should be one classifier for each
     * dimension of the balancing procedure.
     *
     * @return the population synthesizer's balance dimension classifiers.
     */
    public abstract Collection<BalanceDimensionClassifier> getClassifiers(); //geographic mapping name to classifier

    /**
     * The the balance element groups for each basis geography element in a specified neighborhood. The balance element
     * groups should hold all of the balance elements available to the particular geography element (don't perform any
     * filtering or selection - that will be done internally).
     *
     * @param neighborhood
     *        The geographic neighborhood.
     *
     * @return a mapping from each basis geography element in a neighborhood to its full balance element group.
     */
    public abstract Map<G,BalanceElementGroup<E>> getBalanceElementGroups(GeographicNeighborhood<G> neighborhood);

    /**
     * Get a mapping from balance elment groups to their integer targets for the discretization step. Note that these groups
     * are not required to match those used in the balancing procedure.
     *
     * @param baseElementGroups
     *        A mapping from the base geography elements to their respective (full) balance element groups.
     *
     * @return a mapping from balance element groups to the integer targets that the discretizer should attempt to match.
     */
    public abstract Map<BalanceElementGroup<E>,Integer> getDiscretizeGroups(Map<G,BalanceElementGroup<E>> baseElementGroups);

    /**
     * Get the maximum target value for a given base geography element. This is used to limit the size of any element's
     * weight, which dampens large swings in the balancing procedure.
     *
     * @param baseGeographyElement
     *        The base geography element.
     *
     * @return the maximum target value for the geography element.
     */
    public abstract double getMaxTarget(G baseGeographyElement);

    /**
     * Get the random number generator used in this instance.
     *
     * @return this instance's random number generator.
     */
    public Random getRandom() {
        return new Random();
    }

    /**
     * Get the convergence criterion for this instance. This criterion will be used for all dimensions. By default, this returns
     * {@link #DEFAULT_CONVERGENCE_CRITERION}.
     *
     * @return this instance's convergence criterion.
     */
    public double getConvergenceCriterion() {
        return DEFAULT_CONVERGENCE_CRITERION;
    }

    /**
     * Get the maximum number of iterations for each balancing action. By default, this return {@link #DEFAULT_MAXIMUM_ITERATIONS}.
     *
     * @return the maximum number of iterations for each balancing action.
     */
    public int getMaxIterations() {
        return DEFAULT_MAXIMUM_ITERATIONS;
    }

    /**
     * Get the maximum number of convergence retries for this instance. That is, the balancing procedure will be retried
     * this many times (for each balance unit/neighborhood) if it does not converge. By default, this returns {@link #DEFAULT_MAXIMUM_CONVERGENCE_RETRIES}.
     *
     * @return the maximum number of convergence retries for this instance.
     */
    public int getConvergenceRetries() {
        return DEFAULT_MAXIMUM_CONVERGENCE_RETRIES;
    }

    /**
     * Finish up with a geographic neighborhood's balance elements after the balance procedure has balanced them. This
     * method does nothing by default, but can be overridden to provide reports, store results, or perform some other
     * result processing operation.
     *
     * @param neighborhoodBalanceElementGroups
     *        The balance element groups which were balanced.
     *
     * @param neighborhood
     *        The geographic neighborhood which the balance element groups pertain to.
     *
     * @param convergenceInformation
     *        The convergence information for the balance procedure.
     */
    protected void finishNeighborhoodAction(Map<G,BalanceElementGroup<E>> neighborhoodBalanceElementGroups,
                                            GeographicNeighborhood<G> neighborhood,
                                            ConvergenceInformation convergenceInformation) {
    }



    private void synthesizeOneNeighborhood(GeographicNeighborhood<G> neighborhood,
                                            Set<Geography<?,?>> geographies, //geographies we'll be working in
                                            Map<Geography<?,?>,Set<GeographicNeighborhood<G>>> geographyNeighborhoods, //mapping from geography to its neighborhoods (represented in the basis)
                                            Map<Geography<?,?>,Map<? extends GeographyElement<?>,Set<G>>> basisMappings) {  //mapping from each geography's elements to the equivalent basis elements

//        System.out.println("Synthesizing for neighborhood: " + neighborhood);
        Geography<?,G> baseGeography = getBaseGeography();
        Collection<BalanceDimensionClassifier> classifiers = getClassifiers();

        //get neighborhood of basis elements
        Set<G> basisNeighborhood = neighborhood.getNeighborhood();
        //fill in which neighborhoods (for each geography) belong to our current neighborhood
        Map<Geography<?,?>,Set<GeographicNeighborhood<G>>> currentGeographyNeighborhoods = new HashMap<>();
        for (Geography<?,?> geography : geographies) {
            Set<GeographicNeighborhood<G>> gn = new HashSet<>();
            for (GeographicNeighborhood<G> n : geographyNeighborhoods.get(geography))
                if (basisNeighborhood.containsAll(n.getNeighborhood())) //if it doesn't contain everyone, then it must not belong
                    gn.add(n);
            currentGeographyNeighborhoods.put(geography,gn);
        }
        //set max targets for this neighborhood - this will be used to limit weights when balancing
        Map<Geography<?,?>,Map<GeographyElement<?>,Double>> maxTargets = new HashMap<>();
        for (Geography<?,?> geography : geographies) {
            Map<GeographyElement<?>,Double> mxTargs = new HashMap<>();
            for (GeographicNeighborhood<G> n : currentGeographyNeighborhoods.get(geography)) {
                for (GeographyElement<?> element : geography.getGeography()) {
                    //test if it belongs in this neighborhood
                    Set<G> basisElements = basisMappings.get(geography).get(element);
                    if (!n.getNeighborhood().containsAll(basisElements))
                        continue;

                    double maxTarget = 0.0;
                    for (G basisElement : basisElements)
                        maxTarget += getMaxTarget(basisElement);
                    mxTargs.put(element,maxTarget);
                }
            }
            maxTargets.put(geography,mxTargs);
        }
        //build mapping from geographies to their classifiers
        Map<Geography<?,?>,List<BalanceDimensionClassifier>> geoBdc = new HashMap<>();
        for (BalanceDimensionClassifier c : classifiers) {
            Geography<?,?> g = c.getTargetGeography();
            if (!geoBdc.containsKey(g))
                geoBdc.put(g,new LinkedList<BalanceDimensionClassifier>());
            geoBdc.get(g).add(c);
        }

        int convergenceRetries = getConvergenceRetries()+1;
        if (convergenceRetries < 1)
            throw new IllegalStateException("Convergence retries cannot be less than 0: " + (convergenceRetries-1));

//        Map<B,BalanceElementGroup<E>> neighborhoodBalanceElementGroups = null; //balance elements for each basis geography element
//        Map<Geography<?,?>,Map<GeographyElement<?>,BalanceElementGroup<E>>> elementGroupsByGeographyNeighborhood = null; //balance elements for each geography's elements
        Balancer finalBalancer = null;
        Map<G,BalanceElementGroup<E>> finalNeighborhoodBalanceElementGroups = null; //balance elements for each basis geography element
        Map<Geography<?,?>,Map<GeographyElement<?>,BalanceElementGroup<E>>> finalElementGroupsByGeographyNeighborhood = null; //balance elements for each geography's elements


        for (int i = 0; i < convergenceRetries; i++) {
            Map<G,BalanceElementGroup<E>> neighborhoodBalanceElementGroups = getBalanceElementGroups(neighborhood); //get a new representative sample for the neighborhood
            Map<Geography<?,?>,Map<GeographyElement<?>,BalanceElementGroup<E>>> elementGroupsByGeographyNeighborhood = new HashMap<>();
            //use mapping of neighborhood elements to basis elements to build mapping of neighborhood elements to balance elements
            for (Geography<?,?> geography : geographies) {
                Map<GeographyElement<?>,BalanceElementGroup<E>> elementMap = new HashMap<>(); //map from geography's elements to the representative balance elements
                for (GeographicNeighborhood<G> n : currentGeographyNeighborhoods.get(geography)) { //go through each neighborhood we must balance against
                    for (GeographyElement<?> element : geography.getGeography()) {
                        //test if geography's element belongs in this neighborhood
                        Set<G> basisElements = basisMappings.get(geography).get(element);
                        if (!n.getNeighborhood().containsAll(basisElements))
                            continue;

                        BalanceElementGroup<E> group = null;
                        for (G basisElement : basisElements) {
                            BalanceElementGroup<E> g = neighborhoodBalanceElementGroups.get(basisElement);
                            group = group == null ? g : group.join(Arrays.asList(g));
                        }
                        elementMap.put(element,group);
                    }
                }
                elementGroupsByGeographyNeighborhood.put(geography,elementMap);
            }
            //build balance groups
            List<Balancer> balancers = new LinkedList<>();
            for (Geography<?,?> geography : geoBdc.keySet()) {
                Map<GeographyElement<?>,Double> maxTarget = maxTargets.get(geography);
                Map<GeographyElement<?>,BalanceElementGroup<E>> balanceElements = elementGroupsByGeographyNeighborhood.get(geography); //get balance elements for this geography element
                for (GeographyElement<?> element : balanceElements.keySet()) {
                    @SuppressWarnings("unchecked") //geography with ? are ok here
                    Geography<?,GeographyElement<?>> g = (Geography<?,GeographyElement<?>>) geography;
                    Balancer b = new BaseBalancer(element.toString(),geoBdc.get(geography),getConvergenceCriterion(),getMaxIterations(),balanceElements.get(element),getTargets(g,element),maxTarget.get(element));
                    balancers.add(b);
                }
            }
            Balancer balancer = new CompositeBalancer(neighborhood.toString(),balancers);

            @SuppressWarnings("unchecked") //this is the correct cast in spirit, and should not cause any errors
            Map<G,BalanceElementGroup<E>> elementGroupsMap = (Map<G,BalanceElementGroup<E>>) elementGroupsByGeographyNeighborhood.get(baseGeography);
            resetBalanceElements(elementGroupsMap);
//            resetBalanceElements((Map<B,BalanceElementGroup<E>>) elementGroupsByGeographyNeighborhood.get(baseGeography));
            //finalBalancer.getConvergenceInformation().resetHistory();
            balancer.balance();
            if (balancer.getConvergenceInformation().isConverged()) {
                finalBalancer = balancer;
                finalElementGroupsByGeographyNeighborhood = elementGroupsByGeographyNeighborhood;
                finalNeighborhoodBalanceElementGroups = neighborhoodBalanceElementGroups;
                break;
            }
            //do a comparison with previous to see which one we should keep
            if ((finalBalancer == null) || (getWeightedConvergenceSum(finalBalancer.getConvergenceInformation()) > getWeightedConvergenceSum(balancer.getConvergenceInformation()))) {
                finalBalancer = balancer;
                finalElementGroupsByGeographyNeighborhood = elementGroupsByGeographyNeighborhood;
                finalNeighborhoodBalanceElementGroups = neighborhoodBalanceElementGroups;

            }
        }
        @SuppressWarnings("unchecked") //this is the correct cast in spirit, and should not cause any errors
        Map<G,BalanceElementGroup<E>> elementGroupsMap = (Map<G,BalanceElementGroup<E>>) finalElementGroupsByGeographyNeighborhood.get(baseGeography);
        Map<BalanceElementGroup<E>,Integer> discretizeGroups = getDiscretizeGroups(elementGroupsMap);
        //Map<BalanceElementGroup<E>,Integer> discretizeGroups = getDiscretizeGroups((Map<B,BalanceElementGroup<E>>) elementGroupsByGeographyNeighborhood.get(baseGeography));
        Discretizer discretizer = new Discretizer(getRandom());
        for (Map.Entry<BalanceElementGroup<E>,Integer> discretizeGroup : discretizeGroups.entrySet()) {
            discretizer.discretize(discretizeGroup.getKey().getWeightSet(),discretizeGroup.getValue());
        }
        finalBalancer.updateControlsAndTargets();
        finishNeighborhoodAction(finalNeighborhoodBalanceElementGroups,neighborhood,finalBalancer.getConvergenceInformation());
    }

    /**
     * Run the synthesizer. This will setup the balance procedure and split it into independent units; send the balance
     * work out to a concurrent process runner; discretize the results; and perform any finish action specified by
     * {@link #finishNeighborhoodAction(java.util.Map,com.pb.sawdust.geography.GeographicNeighborhood,ConvergenceInformation)}.
     */
    public void runSynthesizer() {
        Geography<?,G> baseGeography = getBaseGeography();
        Set<GeographicMapping<?,?>> geographyMappings = getGeographicMappings();
        Collection<BalanceDimensionClassifier> classifiers = getClassifiers();
        //get set of necessary geographies for the classifiers
        final Set<Geography<?,?>> geographies = new HashSet<>();
        for (BalanceDimensionClassifier classifier : classifiers)
            geographies.add(classifier.getTargetGeography());

        //get set of minimal neighborhoods to work on
        final Set<GeographicNeighborhood<G>> neighborhoods = GeographicNeighborhoods.getMappingNeighborhoods(baseGeography,geographyMappings);
        //get map from geography to set of neighborhoods defined in basis that it maps to
        final Map<Geography<?,?>,Set<GeographicNeighborhood<G>>> geographyNeighborhoods = GeographicNeighborhoods.getMappingNeighborhoods(baseGeography,geographyMappings,geographies);
        //get mapping from various geography elements to the representative set of basis elements
        final Map<Geography<?,?>,Map<? extends GeographyElement<?>,Set<G>>> basisMappings = GeographicNeighborhoods.getBasisMappings(baseGeography,geographyMappings,geographies);

        IteratorAction<GeographicNeighborhood<G>> action = new IteratorAction<>(neighborhoods,new Function1<GeographicNeighborhood<G>,Void>() {
            @Override
            public Void apply(GeographicNeighborhood<G> neighborhood) {
                synthesizeOneNeighborhood(neighborhood,geographies,geographyNeighborhoods,basisMappings);
                return null;
            }
        });

        new ForkJoinPool().execute(action);
        action.waitForCompletion();
    }

    private void resetBalanceElements(Map<G,BalanceElementGroup<E>> baseElementGroups) {
        for (BalanceElementGroup<E> b : baseElementGroups.values())
            for (BalanceElement be : b)
                be.reset();
    }

    private static double getWeightedConvergenceSum(ConvergenceInformation ci) {
        double sum = 0.0;
        for (String dimension : ci.getDimensionNames()) {
            double subNumerator = 0.0; //sum of absolute value of errors
            double subDenominator = 0.0; //sum of targets - for normalization
            for (ConvergenceInformation.ConvergenceInformationElement cie : ci.getConvergenceInformation(dimension).values()) {
                subNumerator += Math.abs(cie.getConvergenceMeasure()*cie.getValue());
                subDenominator += cie.getTarget();
            }
            if (subDenominator > 0.0)
                sum += subNumerator/subDenominator;
        }
        return sum;
    }

    /**
     * Build a convergence summary for a given {@code ConvergenceInformation} instance. The summary reports the iteration
     * counts, convergence status, as well as the percent difference between the target and final values.
     *
     * @param convergenceInformation
     *        The specified convergence information.
     *
     * @return a text summary of {@code convergenceInformation}.
     */
    public static String buildConvergenceSummary(ConvergenceInformation convergenceInformation) {
        String lineSeparator = FileUtil.getLineSeparator();
        StringBuilder summary = new StringBuilder(String.format("***Convergence summary for %s***",convergenceInformation.getAssociationName())).append(lineSeparator);

        for (String dimensionName : convergenceInformation.getDimensionNames()) {
            double criterion = convergenceInformation.getConvergenceCriterion(dimensionName);
            int updateCount = convergenceInformation.getUpdateCount(dimensionName);
            summary.append("\t")
                   .append(dimensionName)
                   .append(String.format(" (%sconverged) (criteria: %f, iterations: %d)",convergenceInformation.isConverged(dimensionName) ? "" : "not ",criterion,Math.max(0,updateCount - 1)))
                   .append(lineSeparator);
            Map<?,ConvergenceInformation.ConvergenceInformationElement> info = convergenceInformation.getConvergenceInformation(dimensionName);
            Map<?,ConvergenceInformation.ConvergenceInformationElement> previousInfo = convergenceInformation.getPreviousUpdateConvergenceInformation(dimensionName);

            int maxLength = 0;
            for (Object dimType : info.keySet())
                maxLength = Math.max(maxLength,dimType.toString().length());
            for (Object dimType : info.keySet()) {
                ConvergenceInformation.ConvergenceInformationElement ci = info.get(dimType);
                ConvergenceInformation.ConvergenceInformationElement pci = previousInfo.get(dimType);
                summary.append("\t\t").append(String.format("%-" + maxLength + "s: ",dimType));
                switch (updateCount) {
                    case 0 : summary.append(String.format(" (final criteria: --) (target: %10.1f, final: --, %% diff: --%%)",
                            ci.getTarget())); break;
                    case 1 : summary.append(String.format(" (final criteria: --) (target: %10.1f, final: %10.1f, %% diff: %5.2f%%)",
                            ci.getTarget(),ci.getValue(),ci.getConvergenceMeasure() * 100)); break;
                    default : summary.append(String.format(" (final criteria: %13.8f) (target: %10.1f, final: %10.1f, %% diff: %5.2f%%)",
                            pci.getConvergenceMeasure(),ci.getTarget(),ci.getValue(),ci.getConvergenceMeasure() * 100)); break;
                }
                summary.append(lineSeparator);
            }
        }
        return summary.toString();
    }

    /**
     * Build a convergence summary for a series of {@code ConvergenceInformation}s instance. The summary will be similar
     * to that of {@link #buildConvergenceSummary(ConvergenceInformation)}, only the summary will be for the aggregation
     * of the various {@code ConvergenceInformation} instances. This method is useful for generating region-wide performance
     * summaries.
     *
     * @param convergenceInformations
     *        The convergence information instances.
     *
     * @param name
     *        A name to identify the collection of informations with. An example would be <code>"Entire Region"</code>.
     *
     * @return a text summary of {@code convergenceInformation}.
     */
    public static String buildConvergenceSummary(List<ConvergenceInformation> convergenceInformations, String name) {
        String lineSeparator = FileUtil.getLineSeparator();
        StringBuilder summary = new StringBuilder(String.format("***Aggregate convergence summary for %s***",name)).append(lineSeparator);

        //assume all convergence information has same dimensions
        ConvergenceInformation representativeCi = convergenceInformations.get(0);
        for (String dimensionName : representativeCi.getDimensionNames()) {
            double criterion = representativeCi.getConvergenceCriterion(dimensionName); //assume all have same criterion
            //combine data
            List<Double> updates = new LinkedList<>();
            Map<Object,List<Double>> pConvergenceValues = new LinkedHashMap<>();
            Map<Object,List<Double>> convergenceValues = new LinkedHashMap<>();
            Map<Object,Double> targets = new HashMap<>();
            Map<Object,Double> values = new HashMap<>();
            int converged = 0;
            for (Object o : representativeCi.getConvergenceInformation(dimensionName).keySet()) {
                convergenceValues.put(o,new LinkedList<Double>());
                pConvergenceValues.put(o,new LinkedList<Double>());
                targets.put(o,0.0);
                values.put(o,0.0);
            }
            for (ConvergenceInformation convergenceInformation : convergenceInformations) {
                if (convergenceInformation.getUpdateCount(dimensionName) == 0)
                    continue;
                updates.add((double) (convergenceInformation.getUpdateCount(dimensionName)-1));
                if (convergenceInformation.isConverged())
                    converged++;
                Map<?,ConvergenceInformation.ConvergenceInformationElement> cie = convergenceInformation.getConvergenceInformation(dimensionName);
                Map<?,ConvergenceInformation.ConvergenceInformationElement> pcie = convergenceInformation.getPreviousUpdateConvergenceInformation(dimensionName);
                for (Object o : cie.keySet()) {
                    convergenceValues.get(o).add(cie.get(o).getConvergenceMeasure());
                    pConvergenceValues.get(o).add(pcie.get(o).getConvergenceMeasure());
                    targets.put(o,targets.get(o) + cie.get(o).getTarget());
                    values.put(o,values.get(o) + cie.get(o).getValue());
                }
            }
            double[] its = ArrayUtil.toDoubleArray(updates);
            summary.append("\t").append(dimensionName).append(String.format(" (converged %d out of %d times) (criteria: %f, average iterations: %f on [%d,%d])",converged,updates.size(),criterion,StatsUtil.getAverage(its),(int) StatsUtil.getMinimum(its),(int) StatsUtil.getMaximum(its))).append(lineSeparator);
            int maxLength = 0;
            for (Object dimType : convergenceValues.keySet())
                maxLength = Math.max(maxLength,dimType.toString().length());
            for (Object dimType : convergenceValues.keySet()) {
                summary.append("\t\t").append(String.format("%-" + maxLength + "s: ",dimType));
                double[] crit = ArrayUtil.toDoubleArray(pConvergenceValues.get(dimType));
                double target = targets.get(dimType);
                double value = values.get(dimType);
                summary.append(String.format(" (average final criteria: %13.8f on [%-13.8f,%13.8f]) (target: %10.1f, final: %10.1f, %%diff: %5.2f%%)",
                               StatsUtil.getAverage(crit),StatsUtil.getMinimum(crit),StatsUtil.getMaximum(crit),target,value,(value/target - 1.0) * 100));
                summary.append(lineSeparator);
            }
        }
        return summary.toString();
    }
}
