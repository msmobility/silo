package com.pb.sawdust.popsynth.em.extensions;

import com.pb.sawdust.calculator.tensor.la.mm.MatrixMultiplicationFactory;
import com.pb.sawdust.geography.*;
import com.pb.sawdust.popsynth.em.BalanceDimensionClassifier;
import com.pb.sawdust.popsynth.em.BalanceElement;
import com.pb.sawdust.popsynth.em.BalanceElementGroup;
import com.pb.sawdust.popsynth.em.EntropyMaximizationPopSynth;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.util.Filter;
import com.pb.sawdust.util.collections.LinkedSetList;

import java.util.*;

/**
 * The {@code ConstraintTablePopSynth} is a entropy-maximizing population synthesizer whose constraints are read from a
 * data table. The constraints include both the targets as well as the geographies they apply to. The geographies are assumed
 * to be functional from the basis geography to any of the others; that is, any given basis geography element will overlap
 * with at most one geography element from another geography. (Note that the opposite is not true: a non-basis geography
 * element may overlap with a number of basis geography elements, but that overlap will be <i>exact</i> in that those
 * basis geography elements will exactly match the non-basis element.)
 *
 * @param <G>
 *        The type of geography element in the basis geography of this procedure.
 *
 * @param <E>
 *        The type of balance element used in this procedure.
 *
 * @author crf
 *         Started 1/14/12 6:32 AM
 */
public abstract class ConstraintTablePopSynth<G extends GeographyElement<?>,E extends BalanceElement> extends EntropyMaximizationPopSynth<G,E> {
    private final Map<Geography<?,?>,DataTable> targetTables = new HashMap<>();
    private final Map<String,Geography<?,?>> geographyTableMapping = new HashMap<>();
    private final Set<GeographicMapping<?,?>> geographicMappings = new HashSet<>();

    /**
     * Get the constraint data table for this population synthesizer.
     *
     * @return this population synthesizer's constraint table.
     */
    protected abstract DataTable getConstraintTable();

    /**
     * Get the set of labels for the columns in the constraint table corresponding to geographies.
     *
     * @return the geography column names in the constraint table.
     */
    protected abstract Set<String> getGeographyTableColumns();

    /**
     * Get the label for the basis geography column in the constraint table.
     *
     * @return the basis geography column name in the constraint table.
     */
    protected abstract String getBasisGeographyTableColumn();

    /**
     * Get the label for the geography size column in the constraint table. This is inferred to be the size of the basis
     * geography element.
     *
     * @return the basis geography size column name in the constraint table.
     */
    protected abstract String getBasisGeographySizeColumn();

    /**
     * Get filters for partitioning the balance elements into groups which are used for discretizing (and possibly other
     * constraint specifications). The keys for the returned mapping correspond to columns in the constraint table, and
     * the value of these columns specify the targets for the discretization step of the synthesizer. In general, each of
     * these filters should be mutually exclusive.
     *
     * @return a mapping from the discretization target table columns to the filter identifying the elements corresponding
     *         to it.
     */
    protected abstract Map<String,Filter<E>> getPartitionFilters();

    /**
     * Get a mapping from the geographies used in this population synthesizer to the target tables pertaining to them. Specifically,
     * because each geography will (probably) have a different number of elements (especially from the base geography),
     * the target table specific to each geography will need to have a different number of rows (since each geography element
     * is matched to one data table row).
     *
     * @return a mapping from tis population synthesizer's geographies to their respective target tables.
     */
    protected  Map<Geography<?,?>,DataTable> getTargetTables() {
        buildTargetTables();
        return targetTables;
    }

    /**
     * Get a mapping from the constraint table (geography) column names to the geographies built from them.
     *
     * @return a map connecting the constraint table columns and their respective geographies.
     */
    protected Map<String,Geography<?,?>> getGeographyTableMapping() {
        buildGeographies();
        return geographyTableMapping;
    }

    @SuppressWarnings("unchecked") //the geography mapping for the basis table column will be a geography of basis elements
     public Geography<?,G> getBaseGeography() {
         return (Geography<?,G>) getGeographyTableMapping().get(getBasisGeographyTableColumn());
     }

    @Override
    public Set<GeographicMapping<?,?>> getGeographicMappings() {
        buildGeographies();
        return geographicMappings;
    }

    @Override
    public <G extends GeographyElement<?>> DataRow getTargets(Geography<?,G> geography, G geographyElement) {
        return getTargetTables().get(geography).getRowByKey(geographyElement.getIdentifier());
    }

    @Override
    public Map<BalanceElementGroup<E>,Integer> getDiscretizeGroups(Map<G,BalanceElementGroup<E>> baseElementGroups) {
        Map<String,Filter<E>> partitionFilter = getPartitionFilters();
        Map<BalanceElementGroup<E>,Integer> discretizeGroups = new HashMap<>();
        DataTable constraintTable = getTargetTables().get(getBaseGeography());
        for (G basisElement : baseElementGroups.keySet()) {
            DataRow row = constraintTable.getRowByKey(basisElement.getIdentifier());
            for (String column : partitionFilter.keySet()) {
                int control = row.getCellAsInt(column);
                discretizeGroups.put(baseElementGroups.get(basisElement).getFilteredElements(partitionFilter.get(column)),control);
            }
        }
        return discretizeGroups;
    }

    @Override
    public double getMaxTarget(G baseGeographyElement) {
        Map<String,Filter<E>> partitionFilter = getPartitionFilters();
        DataRow elementRow = getTargetTables().get(getBaseGeography()).getRowByKey(baseGeographyElement.getIdentifier());
        int maxTarget = 0;
        for (String column : partitionFilter.keySet())
            maxTarget += elementRow.getCellAsInt(column);
        return maxTarget;
    }

    private void buildGeographies() {
        if (geographyTableMapping.size() == 0) {
            DataTable constraintTable = getConstraintTable();
            String basisColumn = getBasisGeographyTableColumn();
            String sizeColumn = getBasisGeographySizeColumn();
            geographyTableMapping.put(basisColumn,GeographicReaders.readGeography(constraintTable,basisColumn,sizeColumn));
            for (String column : getGeographyTableColumns()) {
                if (column.equals(basisColumn))
                    continue;
                GeographicMapping<?,?> mapping = GeographicReaders.<Integer,IdGeographyElement,Integer,IdGeographyElement>readFunctionalMapping(constraintTable,basisColumn,sizeColumn,column);
                geographyTableMapping.put(column,mapping.getToGeography());
                geographicMappings.add(mapping);
            }
            if (geographicMappings.size() == 0) //only basis geography
                geographicMappings.add(FunctionalGeographicMapping.getDegenerateMapping(getBaseGeography()));
        }
    }

    private void buildTargetTables() {
        if (targetTables.size() == 0) {
            Map<String,Geography<?,?>> geographyTableMapping =  getGeographyTableMapping();
            Collection<BalanceDimensionClassifier> classifiers = getClassifiers();
            Set<GeographicMapping<?,?>> geographyMappings = getGeographicMappings();

            String baseColumn = getBasisGeographyTableColumn();
            DataTable constraintTable = getConstraintTable();
            constraintTable.setPrimaryKey(baseColumn);
            Geography<?,?> baseGeography = getBaseGeography();
            targetTables.put(baseGeography,constraintTable);
            for (String column : geographyTableMapping.keySet()) {
                Geography<?,?> geography = geographyTableMapping.get(column);
                List<String> fields = new LinkedSetList<>();
                for (BalanceDimensionClassifier<?> classifier : classifiers)
                    if (classifier.getTargetGeography().equals(geography))
                        fields.addAll(classifier.getTargetFields());
                if (fields.size() > 0) {
                    GeographicMapping<?,?> geographicMapping = null;
                    for (GeographicMapping<?,?> mapping : geographyMappings) {
                        if (mapping.getFromGeography().equals(baseGeography) && mapping.getToGeography().equals(geography)) {
                            geographicMapping = mapping;
                            break;
                        }
                    }
                    if (geographicMapping == null)
                        throw new IllegalStateException("No mapping found from base geography to " + column);
                    DataTable targetTable = GeographicCalculator.transferValueTable(constraintTable,baseColumn,fields,geographicMapping,column,constraintTable.getColumnDataType(column),ArrayTensor.getFactory(),MatrixMultiplicationFactory.getMatrixMultiplication(ArrayTensor.getFactory()));
                    targetTable.setPrimaryKey(column);
                    targetTables.put(geography,targetTable);
                }
            }
        }
    }
}
