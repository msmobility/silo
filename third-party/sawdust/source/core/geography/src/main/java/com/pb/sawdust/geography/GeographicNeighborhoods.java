package com.pb.sawdust.geography;

import com.pb.sawdust.geography.tensor.GeographicBooleanMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.BooleanMatrix;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.util.Range;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.*;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code GeographyNeighborhoods} class provides methods for dealing with {@code GeographicNeighborhood}s. These methods
 * include those which determine relationships between neighborhoods specified in terms of different {@code Geography}s.
 *
 * @author crf
 *         Started 10/31/11 3:34 PM
 */
public class GeographicNeighborhoods {
    private GeographicNeighborhoods() {}  //no need to instantiate

    //todo: decide if this method is worth publishing...
    public static <G extends GeographyElement<?>> Set<GeographicNeighborhood<G>> combineNeighborhoods(Set<GeographicNeighborhood<G>> neighborhood1, Set<GeographicNeighborhood<G>> neighborhood2) {
        return new HashSet<>(combineNeighborhoodCollection(neighborhood1,neighborhood2));
    }

    /**
     * Get the neighborhoods defined by a given mapping in terms of a specified basis geography. Composite mappings are
     * allowed (for example, get the neighborhoods of geography A as defined by geography C with the mappings A->B and B->C).
     * If more than one neighborhood set are defined by the mappings, then these neighborhoods will be combined such
     * that the returned neighborhoods never split up neighborhoods defined by individual mappings.
     *
     * @param basisGeography
     *        The geography the neighborhoods will be defined in.
     *
     * @param mappings
     *        The mappings which will be used to define the neighborhoods.
     *
     * @param <B>
     *        The element type of {@code basisGeography}.
     *
     * @return the set of neighborhoods of {@code basisGeography} as defined by {@code mappings}.
     *
     * @throws IllegalArgumentException if no mapping involving {@code basisGeography} exists in {@code mapping}.
     */
    public static <B extends GeographyElement<?>> Set<GeographicNeighborhood<B>> getMappingNeighborhoods(Geography<?,B> basisGeography, Collection<GeographicMapping<?,?>> mappings) {
        //check for degeneracy
        if (mappings.size() == 1) {
            GeographicMapping<?,?> mapping = mappings.iterator().next();
            if (mapping.getFromGeography().equals(mapping.getToGeography()) && mapping.getFromGeography().equals(basisGeography))
                return getDegenerateNeighborhoods(basisGeography);
        }
        Map<Geography<?,?>,Map<Geography<?,?>,GeographicMapping<?,?>>> graph = normalizeGraph(basisGeography,buildMappingGraph(mappings));
        List<Geography<?,?>> c = new LinkedList<>(graph.keySet());
        Collections.reverse(c);
        Map<Geography<?,?>,Collection<GeographicNeighborhood<B>>> neighborhoodsMap = new HashMap<>();
        for (Geography<?,?> geography : c) {
            Map<Geography<?,?>,GeographicMapping<?,?>> maps = graph.get(geography);
            Collection<GeographicNeighborhood<B>> neighborhoods = null;
            for (Geography<?,?> n : maps.keySet()) {
                GeographicBooleanMatrix<?,?> usageMatrix = (maps.get(n).getFromGeography().equals(geography)) ? maps.get(n).getUsageOverlay() : GeographicCalculator.invertUsage(maps.get(n));
                Collection<? extends GeographicNeighborhood<?>> newNeighborhoods = getNeighborhoods(usageMatrix);
                if (geography != basisGeography) {
                    Map<?,Set<B>> basisMapping = getBasisMapping(basisGeography,mappings,geography);
                    Collection<GeographicNeighborhood<B>> nn = new LinkedList<>();
                    for (GeographicNeighborhood<? extends GeographyElement<?>> newNeighborhood : newNeighborhoods) {
                        Set<B> basisNeighborhood = new HashSet<>();
                        for (GeographyElement<?> element : newNeighborhood.getNeighborhood())
                            basisNeighborhood.addAll(basisMapping.get(element));
                        nn.add(new GeographicNeighborhood<>(basisNeighborhood));
                    }
                    newNeighborhoods = nn;
                }
                @SuppressWarnings("unchecked") //this is certainly in the basis mapping now
                Collection<GeographicNeighborhood<B>> bn = (Collection<GeographicNeighborhood<B>>) newNeighborhoods;
                neighborhoods = combineNeighborhoodCollection(bn,neighborhoods);
            }
            neighborhoodsMap.put(geography,neighborhoods);
        }
        //combine all remaining neighborhoods
        Collection<GeographicNeighborhood<B>> basisNeighborhood = neighborhoodsMap.get(basisGeography);
        for (Geography<?,?> geography : neighborhoodsMap.keySet())
            if (geography != basisGeography)
                basisNeighborhood = combineNeighborhoodCollection(basisNeighborhood,neighborhoodsMap.get(geography));
        return new HashSet<>(basisNeighborhood);
    }

    /**
     * Get a series of neighborhood mappings defined by mappings to specified geographies in terms of a basis geography.
     * This method is similar to {@link #getMappingNeighborhoods(Geography,java.util.Collection)} except it returns the
     * neighborhoods defined by specified geographies, rather than combining all neighborhood sets defined by the given
     * mappings.
     *
     * @param basisGeography
     *        The geography the neighborhoods will be defined in.
     *
     * @param mappings
     *        The mappings which will be used to define the neighborhoods.
     *
     * @param requestedGeographies
     *        The geographies for which the neighborhood sets will be determined.
     *
     * @param <B>
     *        The element type of {@code basisGeography}.
     *
     * @return a mapping from the specified geography to the set of neighborhoods of {@code basisGeography} as defined by
     *         the mapping in {@code mappings} from the basis geography to the specified geography.
     *
     * @throws IllegalArgumentException if no mapping (direct or composite) involving {@code basisGeography} and a specific
     *                                  geography from {@code requestedGeographies} exists in {@code mappings}.
     */
    public static <B extends GeographyElement<?>> Map<Geography<?,?>,Set<GeographicNeighborhood<B>>> getMappingNeighborhoods(Geography<?,B> basisGeography, Collection<GeographicMapping<?,?>> mappings, Set<Geography<?,?>> requestedGeographies) {
        checkGeographies(mappings,requestedGeographies);
        Map<Geography<?,?>,Map<Geography<?,?>,GeographicMapping<?,?>>> mappingGraph = buildMappingGraph(mappings);
        Map<Geography<?,?>,Set<GeographicNeighborhood<B>>> neighborhoods = new HashMap<>();
        for (Geography<?,?> geography : requestedGeographies)
            neighborhoods.put(geography,geography.equals(basisGeography) ?
                                            getDegenerateNeighborhoods(basisGeography) :
                                            getMappingNeighborhoods(basisGeography,getMappingChain(mappingGraph,basisGeography,geography)));
        return neighborhoods;
    }

    /**
     * Get a degenerate set of geographic neighborhoods for a given geography. For each geographic element in the geography
     * the returned set will have a single neighborhood containing only that element.
     *
     * @param basisGeography
     *        The geography the neighborhoods will be defined in.
     *
     * @param <B>
     *        The element type of {@code basisGeography}.
     *
     * @return a set of degenerate geographic neighborhoods for {@code basisGeography}.
     */
    public static <B extends GeographyElement<?>> Set<GeographicNeighborhood<B>> getDegenerateNeighborhoods(Geography<?,B> basisGeography) {
        Set<GeographicNeighborhood<B>> neighborhoods = new HashSet<>();
        for (B element : basisGeography.getGeography())
            neighborhoods.add(new GeographicNeighborhood<>(Arrays.asList(element)));
        return neighborhoods;
    }

    /**
     * Get a map from a given geography element to the set of basis geography elements that map to it. Depending on the
     * mapping, a given basis geography may map to more than one geography element. Composite mappings from geography {@code B}
     * to geography {@code C} are allowed.
     *
     * @param basisGeography
     *        The basis geography.
     *
     * @param mappings
     *        The mappings which will be used to define the relationships between the geographies.
     *
     * @param geography
     *        The geography the returned mapping will be defined for.
     *
     * @param <G>
     *        The type of the element of {@code geography}.
     *
     * @param <B>
     *        The element type of {@code basisGeography}.
     *
     * @return a mapping from the elements of {@code geography} to the set of elements of {@code basisGeography} they map
     *         to as defined by {@code mappings}.
     *
     * @throws IllegalArgumentException if no mapping (direct or composite) involving {@code basisGeography} and {@code geography}
     *                                  exists in {@code mappings}.
     */
    @SuppressWarnings("unchecked") //lots of type crap with <B> and <G>, but it is correct, so suppress them all
    public static <G extends GeographyElement<?>,B extends GeographyElement<?>> Map<G,Set<B>> getBasisMapping(Geography<?,B> basisGeography, Collection<GeographicMapping<?,?>> mappings, Geography<?,G> geography) {
        Map<Geography<?,?>,Map<Geography<?,?>,GeographicMapping<?,?>>> mappingGraph = buildMappingGraph(mappings);
        List<GeographicMapping<?,?>> mappingChain = getMappingChain(mappingGraph,basisGeography,geography);
        boolean[] path = buildToIndex(mappingChain,basisGeography);

        Map<G,Set<B>> baseMapping = new HashMap<>();
        if (basisGeography.equals(geography)) {
            //null case
            for (B b : (List<B>) mappingChain.get(0).getUsageOverlay().getIndex().getIndexIds().get(path[0] ? 1 : 0))
                baseMapping.put((G) b,new HashSet<>(Arrays.asList(b)));
            return baseMapping;
        }

        List<GeographicBooleanMatrix<?,?>> matrixChain = new LinkedList<>();
        for (GeographicMapping<?,?> mapping : mappingChain)
            matrixChain.add(mapping.getUsageOverlay());
        Index<?> basisIndex = matrixChain.get(0).getIndex();
        Index<?> index = matrixChain.get(matrixChain.size()-1).getIndex();

        int finalDim = path[path.length-1] ? 0 : 1;
        for (int i : range(index.size(finalDim)))
            baseMapping.put((G) index.getIndexId(finalDim,i),new HashSet<B>());
        int basisDim = path[0] ? 1 : 0;
        for (int i : range(basisIndex.size(basisDim)))
            fillBasisMapping(matrixChain,path,0,i,baseMapping,(B) basisIndex.getIndexId(basisDim,i));
        return baseMapping;
    }

    /**
     * Get a map from a given geography element to the set of basis geography elements that map to it for a series of specified
     * geographies. This method is essentially a bulk call to {@link #getBasisMapping(Geography,java.util.Collection,Geography)}
     * for a series of geographies (all in terms of the same basis geography).
     *
     * @param basisGeography
     *        The basis geography.
     *
     * @param mappings
     *        The mappings which will be used to define the relationships between the geographies.
     *
     * @param requestedGeographies
     *        The geographies the returned mappings will be defined for.
     *
     * @param <B>
     *        The element type of {@code basisGeography}.
     *
     * @return a mapping from each of the specified geographies in {@code requestedGeographies} to a mapping from the elements
     *         of the geography to the set of elements of {@code basisGeography} they map to as defined by {@code mappings}.
     *
     * @throws IllegalArgumentException if no mapping (direct or composite) involving {@code basisGeography} and each of
     *                                  the geographies in {@code requestedGeographies} exists in {@code mappings}.
     */
    public static <B extends GeographyElement<?>> Map<Geography<?,?>,Map<? extends GeographyElement<?>,Set<B>>> getBasisMappings(Geography<?,B> basisGeography, Collection<GeographicMapping<?,?>> mappings, Set<Geography<?,?>> requestedGeographies) {
        checkGeographies(mappings,requestedGeographies);
        Map<Geography<?,?>,Map<? extends GeographyElement<?>,Set<B>>> basisMappings = new HashMap<>();
        for (Geography<?,?> geography : requestedGeographies)
            basisMappings.put(geography,getBasisMapping(basisGeography,mappings,geography));
        return basisMappings;
    }

    private static <B extends GeographyElement<?>> void fillBasisMapping(List<GeographicBooleanMatrix<?,?>> matrixChain, boolean[] path, int current, int position, Map<? extends GeographyElement<?>,Set<B>> mapping, B currentBasisElement) {
        GeographicBooleanMatrix<?,?> matrix = matrixChain.get(current);
        int otherDim = path[current] ? 0 : 1;
        int[] index = new int[2];
        index[path[current] ? 1 : 0] = position;
        boolean end = current == path.length-1;
        Index<?> ind = matrix.getIndex();
        for (int i : range(matrix.size(otherDim))) {
            index[otherDim] = i;
            if (matrix.getCell(index)) {
                if (end)
                    mapping.get(ind.getIndexId(path[current] ? 0 : 1,i)).add(currentBasisElement);
                else
                    fillBasisMapping(matrixChain,path,current + 1,i,mapping,currentBasisElement);
            }
        }
    }

//    private static <B extends GeographyElement<?>> Set<GeographicNeighborhood<B>> getMappingNeighborhoods(Geography<?,?> geography, Map<Geography<?,?>,Map<Geography<?,?>,GeographicMapping<?,?>>> mappingGraph, Geography<?,B> basisGeography, Collection<GeographicNeighborhood<B>> basisNeighborhoods) {
//        List<GeographicMapping<?,?>> mappingChain = getMappingChain(mappingGraph,basisGeography,geography);
//        boolean[] path = buildToIndex(mappingChain,basisGeography);
//        List<GeographicBooleanMatrix<?,?>> matrixChain = new LinkedList<>();
//        for (GeographicMapping<?,?> mapping : mappingChain)
//            matrixChain.add(mapping.getUsageOverlay());
//        Set<GeographicNeighborhood<B>> neighborhoods = new HashSet<>();
//        GeographicBooleanMatrix<? extends GeographyElement<?>,? extends GeographyElement<?>> first = matrixChain.get(0);
//        for (GeographicNeighborhood<B> originalNeighborhood : basisNeighborhoods) {
//            Set<GeographyElement<?>> newNeighborhood = new HashSet<>();
//            for (Object i : originalNeighborhood.getNeighborhood())
//                buildDependantNeighborhood(matrixChain,path,0,first.getIndex().getIndex(path[0] ? 1 : 0,(GeographyElement<?>) i),newNeighborhood);
//            @SuppressWarnings("unchecked") //is definitely a Set of <B>, so suppress
//            Set<B> nn = (Set<B>) newNeighborhood;
//            neighborhoods.add(new GeographicNeighborhood<>(nn));
//        }
//        return neighborhoods;
//    }

    private static void buildDependantNeighborhood(List<GeographicBooleanMatrix<?,?>> matrixChain, boolean[] path, int current, int position, Set<GeographyElement<?>> neighborhood) {
        GeographicBooleanMatrix<?,?> matrix = matrixChain.get(current);
        int otherDim = path[current] ? 1 : 0;
        int[] index = new int[2];
        index[path[current] ? 0 : 1] = position;
        boolean end = current == path.length-1;
        Index<?> ind = matrix.getIndex();
        for (int i : range(matrix.size(otherDim))) {
            index[otherDim] = i;
            if (matrix.getCell(index)) {
                if (end)
                    neighborhood.add((GeographyElement<?>) ind.getIndexId(path[current] ? 0 : 1,i));
                else
                    buildDependantNeighborhood(matrixChain,path,current + 1,i,neighborhood);
            }
        }
    }

    private static boolean[] buildToIndex(List<GeographicMapping<?,?>> mappingChain, Geography<?,?> start) {
        Geography<?,?> current = start;
        List<Boolean> path = new LinkedList<>();
        for (GeographicMapping<?,?> mapping : mappingChain) {
            if (mapping.getFromGeography().equals(current)) {
                path.add(false);
                current = mapping.getToGeography();
            } else {
                path.add(true);
                current = mapping.getFromGeography();
            }
        }
        return ArrayUtil.toBooleanArray(path);
    }

    private static List<GeographicMapping<?,?>> getMappingChain(Map<Geography<?,?>,Map<Geography<?,?>,GeographicMapping<?,?>>> mappingGraph, Geography<?,?> from, Geography<?,?> to) {
        return getMappingChain(mappingGraph,from,to,new HashSet<Geography<?,?>>());
    }

    private static List<GeographicMapping<?,?>> getMappingChain(Map<Geography<?,?>,Map<Geography<?,?>,GeographicMapping<?,?>>> mappingGraph, Geography<?,?> from, Geography<?,?> to, Set<Geography<?,?>> used) {
        Map<Geography<?,?>,GeographicMapping<?,?>> m = mappingGraph.get(from);
        if (m.containsKey(from)) {
            //have a mapping to itself - if this is the only mapping, then we need to return degenerate chain; otherwise throw exception because of ambiguity
            if (mappingGraph.size() == 1 && m.size() == 1) {
                return Arrays.<GeographicMapping<?,?>>asList(m.get(from));
            } else {
                throw new IllegalArgumentException("Cannot have degenerate mapping (geography mapped to itself) if other mappings are being used.");
            }
        }
        used.add(from);
        for (Geography<?,?> key : m.keySet()) {
            if (used.contains(key))
                continue;
            List<GeographicMapping<?,?>> mappingList = new LinkedList<>();
            mappingList.add(m.get(key));
            if (m.get(key).getToGeography().equals(to) || m.get(key).getFromGeography().equals(to))
                return mappingList;
            List<GeographicMapping<?,?>> subsequentMapping = getMappingChain(mappingGraph,key,to,used);
            if (subsequentMapping != null) {
                mappingList.addAll(subsequentMapping);
                return mappingList;
            }
        }
        return null;
    }

    private static void checkGeographies(Collection<GeographicMapping<?,?>> mappings, Set<Geography<?,?>> geographies) {
        outer: for (Geography<?,?> geography : geographies) {
            for (GeographicMapping<?,?> mapping : mappings)
                if (mapping.getFromGeography().equals(geography) || mapping.getToGeography().equals(geography))
                    continue outer;
            throw new IllegalArgumentException("Geography not found: "+ geography);
        }
    }

    private static Map<Geography<?,?>,Map<Geography<?,?>,GeographicMapping<?,?>>> buildMappingGraph(Collection<GeographicMapping<?,?>> mappings) {
        Map<Geography<?,?>,Map<Geography<?,?>,GeographicMapping<?,?>>> graph = new HashMap<>();
        for (GeographicMapping<?,?> mapping : mappings) {
            Geography<?,?> fromGeography = mapping.getFromGeography();
            Geography<?,?> toGeography = mapping.getToGeography();
            if (!graph.containsKey(fromGeography))
                graph.put(fromGeography,new HashMap<Geography<?,?>,GeographicMapping<?,?>>());
            if (!graph.containsKey(toGeography))
                graph.put(toGeography,new HashMap<Geography<?,?>,GeographicMapping<?,?>>());
            if (graph.get(fromGeography).put(toGeography,mapping) != null)
                throw new IllegalArgumentException(String.format("Duplicate mapping found for geography pair (%s,%s)",fromGeography,toGeography));
            graph.get(toGeography).put(fromGeography,mapping); //no error checking here because would have been caught by above
        }
        return graph;
    }

    private static Map<Geography<?,?>,Map<Geography<?,?>,GeographicMapping<?,?>>> normalizeGraph(Geography<?,?> basis, Map<Geography<?,?>,Map<Geography<?,?>,GeographicMapping<?,?>>> graph) {
        if (!graph.containsKey(basis))
            throw new IllegalArgumentException("Basis geography not found in mappings: " + basis);
        return normalizeGraph(basis,graph,new HashSet<Geography<?,?>>());
    }

    private static Map<Geography<?,?>,Map<Geography<?,?>,GeographicMapping<?,?>>> normalizeGraph(Geography<?,?> basis, Map<Geography<?,?>,Map<Geography<?,?>,GeographicMapping<?,?>>> graph, Set<Geography<?,?>> finished) {
        Map<Geography<?,?>,Map<Geography<?,?>,GeographicMapping<?,?>>> newGraph = new LinkedHashMap<>();
        if (!finished.add(basis))
            return newGraph;
        Map<Geography<?,?>,GeographicMapping<?,?>> newMapping = new HashMap<>();
        Set<Geography<?,?>> nextLevel = new HashSet<>();
        for (Geography<?,?> n : graph.get(basis).keySet()) {
            if (!finished.contains(n)) {
                nextLevel.add(n);
                newMapping.put(n,graph.get(basis).get(n));
            }
        }
        newGraph.put(basis,newMapping);
        for (Geography<?,?> n : nextLevel)
            newGraph.putAll(normalizeGraph(n,graph,finished));
        return newGraph;
    }

    private static <F extends GeographyElement<?>,T extends GeographyElement<?>> Collection<GeographicNeighborhood<F>> getNeighborhoods(GeographicBooleanMatrix<F,T> usageMatrix) {
        List<Set<Integer>> neighborhoods = new ArrayList<>();
        Set<Integer> used = new HashSet<>();
        for (int i : range(usageMatrix.size(0))) {
            if (used.contains(i))
                continue;
            Set<Integer> neighborhood = new HashSet<>();
            buildNeighborhood(usageMatrix,i,used,neighborhood);
            neighborhoods.add(neighborhood);
        }
        List<GeographicNeighborhood<F>> elementNeighborhoods = new LinkedList<>();
        Index<? extends GeographyElement<?>> index = usageMatrix.getIndex();
        //replace indices with ids
        for (Set<Integer> intNeighborhood : neighborhoods) {
            Set<F> elementNeighborhood = new HashSet<>();
            for (int n : intNeighborhood) {
                @SuppressWarnings("unchecked") //this is surely an F
                F findex = (F) index.getIndexId(0,n);
                elementNeighborhood.add(findex);
            }
            elementNeighborhoods.add(new GeographicNeighborhood<>(elementNeighborhood));
        }
        return elementNeighborhoods;
    }

    private static void buildNeighborhood(BooleanMatrix usageMatrix, int point, Set<Integer> used, Set<Integer> current) {
        //dimension assumed to be 0;
        if (!used.add(point))
            return;
        current.add(point);
        int[] idx = new int[2];
        Range range = new Range(usageMatrix.size(0));
        for (int i : range(usageMatrix.size(1))) {
            idx[0] = point;
            idx[1] = i;
            if (usageMatrix.getCell(idx)) {
                //used in this mapping
                for (int j : range) {
                    idx[0] = j;
                    if (usageMatrix.getCell(idx))
                        buildNeighborhood(usageMatrix,j,used,current); //recurse on neighbors
                }
            }
        }
    }

    private static <G extends GeographyElement<?>> Collection<GeographicNeighborhood<G>> combineNeighborhoodCollection(Collection<GeographicNeighborhood<G>> neighborhoods1, Collection<GeographicNeighborhood<G>> neighborhoods2) {
        if (neighborhoods2 == null)
            return neighborhoods1;
        List<GeographicNeighborhood<G>> neighborhoods = new LinkedList<>(neighborhoods2);
        neighborhoods.addAll(neighborhoods1);
        combineUntilDisjoint(neighborhoods);
        return neighborhoods;
    }

    private static <G extends GeographyElement<?>> void combineUntilDisjoint(List<GeographicNeighborhood<G>> neighborhoods) {
        boolean disjoint = false;
        while (!disjoint) { //have to retry because neighborhoods may now overlap when combined with others
            disjoint = true;
            for (int i : range(neighborhoods.size()-1))
                disjoint &= disjoin(neighborhoods,i);
        }
    }

    private static <G extends GeographyElement<?>> boolean disjoin(List<GeographicNeighborhood<G>> neighborhoods, int point) {
        if (point >= neighborhoods.size())
            return false;
        GeographicNeighborhood<G> neighborhood = neighborhoods.get(point);
        List<Integer>  toRemove = new LinkedList<>();
        Set<GeographicNeighborhood<G>> toSum = new HashSet<>();
        for (int i : range(point+1,neighborhoods.size())) {
            GeographicNeighborhood<G> nextNeighborhood = neighborhoods.get(i);
            if (!Collections.disjoint(neighborhood.getNeighborhood(),nextNeighborhood.getNeighborhood())) {
                toRemove.add(0,i); //reverse order
                toSum.add(nextNeighborhood);
            }
        }
        for (int i : toRemove)
            neighborhoods.remove(i);
        neighborhoods.set(point,neighborhood.mergeWith(toSum));
        return toRemove.size() == 0;
    }
}
