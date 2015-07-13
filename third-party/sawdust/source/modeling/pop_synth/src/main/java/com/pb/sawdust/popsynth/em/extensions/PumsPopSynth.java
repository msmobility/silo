package com.pb.sawdust.popsynth.em.extensions;

import com.pb.sawdust.data.census.pums.*;
import com.pb.sawdust.geography.*;
import com.pb.sawdust.geography.tensor.GeographicBooleanMatrix;
import com.pb.sawdust.popsynth.em.BalanceDimensionClassifier;
import com.pb.sawdust.popsynth.em.BalanceElementGroup;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.util.Filter;
import static com.pb.sawdust.util.Range.*;

import java.util.*;


/**
 * The {@code PumsPopSynth} is a constraint table population synthesizer whose balance elements correspond to household
 * entries from a PUMS data set.
 *
 * @param <G>
 *        The type of geography element in the basis geography of this procedure.
 *
 * @author crf
 *         Started 1/13/12 9:03 AM
 */
public abstract class PumsPopSynth<G extends GeographyElement<?>> extends ConstraintTablePopSynth<G,PumsBalanceElement> {
    public static final int DEFAULT_SAMPLE_SIZE_MINIMUM = 10;

    private BalanceElementGroup<PumsBalanceElement> masterGroup;

    /**
     * Get the PUMA tables from which the population will be built from.
     *
     * @return the PUMA tables used for building the population.
     */
    protected abstract PumaTables getPumaTables();

    /**
     * Get the PUMA data dictionary corresponding to the tables used to build the population.
     *
     * @return the PUMA data dictionary for this population synthesizer.
     */
    protected abstract PumaDataDictionary getDataDictionary();

    /**
     * Get the geographic mapping from the basis geography to the PUMAs holding the (PUMS) households from which the population
     * will be built.
     *
     * @return the geographic mapping from the PUMAs for this synthesizer to its basis geography.
     */
    protected abstract GeographicMapping<G,? extends GeographyElement<Puma>> getBasisToPumaMapping();

    /**
     * Scale the sample size for a given balance element. That is, for a given balance element, determine the sample size
     * (of PUMS households) that should be used to build the population from. By default, this method does not scale the
     * size but enforces a minimum sample size of {@link #DEFAULT_SAMPLE_SIZE_MINIMUM}; that is, it returns:
     * <p><code>
     *     Math.max(size,DEFAULT_SAMPLE_SIZE_MINIMUM)
     * </code></p>
     *
     * @param size
     *        The (final) size for the geography element.
     *
     * @return the sample size to use, given the input {@code size}.
     */
    protected int scaleRepresentativeSampleSize(int size) {
        return Math.max(size,DEFAULT_SAMPLE_SIZE_MINIMUM);
    }

    /**
     * Get the filters for given constraint table columns which are "optional" in the sense that they will be enforced if
     * possible, but relaxed when necessary. Specifically, if a sample size cannot be made purely from balance elements
     * which pass the filter, then those which did not will be allowed to be placed in the balance group. This is in contrast
     * to the (strict) partition filters, which must be enforced when building the balance group. By default, this mapping
     * is empty.
     *
     * @return the optional filters for building a balance group.
     */
    protected Map<String,Filter<PumsBalanceElement>> getRelaxedFilters() {
        return new HashMap<>();
    }

    @SuppressWarnings("unchecked") //person/hh column types must match reader, and exception should be thrown by reader if they don't
    private synchronized BalanceElementGroup<PumsBalanceElement> getMasterGroup() {
        if (masterGroup == null)
            masterGroup = new BalanceElementGroup<>(getBalanceElements(getPumaTables()));
        return masterGroup;
    }

    /**
     * Get the allowed PUMAs for a given geography basis element. That is, it returns the set of PUMAs that correspond to
     * a given geography element. This method uses the geography mapping to specify this relationship, but this could be
     * overridden, if needed (for example, to allow all region-wide PUMAs for certain geography elements).
     *
     * @param basisElement
     *        The geography element in question.
     *
     * @return the set of PUMAs from which the synthesizer will draw from for {@code basisElement}.
     */
    @SuppressWarnings("unchecked") //index wil be holding GeographyElement<Puma> as identifiers in dimension 1, so this is ok
    public Set<Puma> getAllowedPumas(G basisElement) {
        GeographicBooleanMatrix<G,? extends GeographyElement<Puma>> usageMapping = getBasisToPumaMapping().getUsageOverlay();
        Index<GeographyElement<?>> index = usageMapping.getIndex();
        Set<Puma> pumas = new HashSet<>();
        int from = index.getIndex(0,basisElement);
        for (int to : range(usageMapping.size(1)))
            if (usageMapping.getCell(from,to))
                pumas.add(((GeographyElement<Puma>) index.getIndexId(1,to)).getIdentifier());
        return pumas;
    }

    @Override
    public Map<G,BalanceElementGroup<PumsBalanceElement>> getBalanceElementGroups(GeographicNeighborhood<G> neighborhood) {
        Map<String,Filter<PumsBalanceElement>> strictFilters = getPartitionFilters();
        Map<String,Filter<PumsBalanceElement>> relaxedFilters = getRelaxedFilters();
        Collection<BalanceDimensionClassifier> classifiers = getClassifiers();
        Random random = getRandom();

        Map<G,BalanceElementGroup<PumsBalanceElement>> elementGroups = new HashMap<>();
        DataTable basisTable = getTargetTables().get(getBaseGeography());
        for (G basisElement : neighborhood.getNeighborhood()) {
            DataRow row = basisTable.getRowByKey(basisElement.getIdentifier());
            List<BalanceElementGroup<PumsBalanceElement>> groups = new LinkedList<>();

            Set<Puma> allowedPumas = getAllowedPumas(basisElement);
            Filter<PumsBalanceElement> pumaFilter = getBasisToPumaMapping().getToGeography().getGeography().size() > allowedPumas.size() ? getPumaFilter(allowedPumas,getDataDictionary()) : null;
            for (String column : strictFilters.keySet()) {
                int control = row.getCellAsInt(column);
                BalanceElementGroup<PumsBalanceElement> group = getMasterGroup().getFilteredElements(strictFilters.get(column)).freshCopy();
                List<Filter<PumsBalanceElement>> balanceElementFilters = new LinkedList<>();
                if (pumaFilter != null)
                    balanceElementFilters.add(pumaFilter);
                if (relaxedFilters.containsKey(column))
                    balanceElementFilters.add(relaxedFilters.get(column));
                groups.add(BalanceElementGroup.buildRepresentativeSample(group,classifiers,balanceElementFilters,scaleRepresentativeSampleSize(control),random));
            }
            elementGroups.put(basisElement,BalanceElementGroup.joinGroups(groups));
        }
        return elementGroups;
    }

    private Iterable<PumsBalanceElement> getBalanceElements(PumaTables tables) {
        PumaDataDictionary<?,?> dataDictionary = tables.getDataDictionary();
        int counter = 0;
        List<PumsBalanceElement> elements = new LinkedList<>();
        for (PumaDataGroup group : tables)
            elements.add(new PumsBalanceElement(counter++,group,dataDictionary.getHouseholdWeightField().getColumnName(),dataDictionary.getPersonWeightField().getColumnName()));
       return elements;
    }

    private Filter<PumsBalanceElement> getPumaFilter(final Set<Puma> allowedPumas, final PumaDataDictionary<?,?> dataDictionary) {
        return new Filter<PumsBalanceElement>() {
            @Override
            public boolean filter(PumsBalanceElement input) {
                DataRow row = input.getElementData().get(PumsBalanceElement.HOUSEHOLD_ROW_KEY);
                return allowedPumas.contains(new Puma(row.getCellAsInt(dataDictionary.getStateFipsField().getColumnName()),row.getCellAsInt(dataDictionary.getPumaField().getColumnName())));
            }
        };
    }
}
