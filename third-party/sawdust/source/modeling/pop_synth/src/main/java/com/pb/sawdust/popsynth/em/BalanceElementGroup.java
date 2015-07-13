package com.pb.sawdust.popsynth.em;

import com.pb.sawdust.util.probability.Weight;
import com.pb.sawdust.util.AntiFilter;
import com.pb.sawdust.util.Filter;
import com.pb.sawdust.util.FilterChain;
import com.pb.sawdust.util.collections.iterators.ChainedIterator;
import com.pb.sawdust.util.collections.iterators.FilteredIterator;
import com.pb.sawdust.util.collections.iterators.IterableIterator;

import java.util.*;

/**
 * The {@code BalanceElementGroup} class is a collection of balance elements which is used in the balancing procedure. Specifically,
 * the  balance procedure acts on {@code BalanceElementGroup}s, whose component elements's weights are balanced against
 * the control totals for the dimensions of the procedure. The balance element group not only allows multiple balance elements
 * for a single geography element to be collected for the balancing procedure, but also different balance groups for each
 * dimension so that different geographies (for each dimension) can be used.
 * <p>
 * The iterator for this class loops over the balance elements that it holds.
 *
 * @param <B>
 *        The type of balance element the group holds.
 *
 * @author crf
 *         Started 10/1/11 8:22 AM
 */
public class BalanceElementGroup<B extends BalanceElement> implements Iterable<B> {
    private final List<B> balanceElements;

    /**
     * Constructor specifying the balance elements to be held by the instance.
     *
     * @param balanceElements
     *        The balance elements for the group.
     */
    public BalanceElementGroup(Iterable<? extends B> balanceElements) {
        this.balanceElements = new LinkedList<>();
        for (B b : balanceElements)
            this.balanceElements.add(b);
    }

    /**
     * Get a balance element group holding the elements from this gropu that pass a filter.
     *
     * @param filter
     *        The balance element filter.
     *
     * @return a new balance element group holding the elements from this group for which <code>filter.filter(element) == true</code>.
     */
    public BalanceElementGroup<B> getFilteredElements(Filter<? super B> filter) {
        return new BalanceElementGroup<>(new IterableIterator<>(new FilteredIterator<>(balanceElements.iterator(),filter)));
    }

    /**
     * Join other balance element groups with this one into a single group.
     *
     * @param otherGroups
     *        The other groups to join with this one.
     *
     * @return a new balance element group comprised of the elements from this group, as well as those in {@code otherGroups}.
     */
    public BalanceElementGroup<B> join(Collection<BalanceElementGroup<B>> otherGroups) {
        List<BalanceElementGroup<B>> groups = new LinkedList<>();
        groups.add(this);
        groups.addAll(otherGroups);
        return joinGroups(groups);
    }

    /**
     * Get a new balance element group holding fresh copies of the elements in this group. That is, for each element in
     * this group, the new group will contain an element produced by {@link com.pb.sawdust.popsynth.em.BalanceElement#freshCopy()}.
     *
     * @return a new balance element group of fresh copies of this group's elements.
     */
    @SuppressWarnings("unchecked") //need to document this, but freshCopy should always return a type of B
    public BalanceElementGroup<B> freshCopy() {
        List<B> elementList = new LinkedList<>();
        for (B balanceElement : balanceElements)
            elementList.add((B) balanceElement.freshCopy());
        return new BalanceElementGroup<>(elementList);
    }

    private Set<Set<Object>> x(List<BalanceDimensionClassifier<?>> classifiers, int position) {
        if (position == classifiers.size())
            return new HashSet<Set<Object>>(Arrays.asList(new HashSet<>()));
        Set<Set<Object>> nextSet = x(classifiers,position+1);
        Set<Set<Object>> set = new HashSet<>();
        for (Object o : classifiers.get(position).getClassificationCategories()) {
            for (Set<Object> s : nextSet) {
                Set<Object> sn = new HashSet<>(s);
                sn.add(o);
                set.add(sn);
            }
        }
        return set;
    }

    /**
     * Get the number of elements in this group. If a single element is held multiple times, it will count multiple times
     * in the size.
     *
     * @return this group's element count.
     */
    public int size() {
        return balanceElements.size();
    }

    /**
     * Reset all of the elements in this group. That is, for each element in this group, call {@link com.pb.sawdust.popsynth.em.BalanceElement#reset()}.
     */
    public void reset() {
        for (B balanceElement : balanceElements)
            balanceElement.reset();
    }

    public Iterator<B> iterator() {
        return balanceElements.iterator();
    }

    /**
     * Get the set of weights for the elements in this group. If this group holds an element multiple times (or has elements
     * which share a single weight), then the returned set will only hold that weight once.
     *
     * @return the set of weights for the elements in this group.
     */
    public Set<Weight> getWeightSet() {
        Set<Weight> set = new HashSet<>();
        for (B balanceElement : balanceElements)
            set.add(balanceElement.getWeight());
        return set;
    }

    /**
     * Build a balance element group comprised of the elements in a collection of balance element groups. If a single element
     * is repeated within or across groups, the returned group will hold it multiple times.
     *
     * @param groups
     *        The groups to join.
     *
     * @param <B>
     *        The type of balance element the returned group will contain.
     *
     * @return a balance element group holding the elements in {@code groups}.
     */
    @SuppressWarnings("unchecked")
    public static <B extends BalanceElement> BalanceElementGroup<B> joinGroups(Collection<BalanceElementGroup<B>> groups) {
        return new BalanceElementGroup<>(new IterableIterator<>(new ChainedIterator<>(groups.toArray((BalanceElementGroup<B>[]) new BalanceElementGroup[groups.size()]))));
    }


    /**
     * Get a balance element group of a specified size comprised of elements from a given group which pass a series of filters
     * and which collectively cover the categories in a series of {@code BalanceDimensionClassifier}s. Specifically, this
     * method randomly picks elements (filtered from this group) sequentially from a series of bins holding elements which
     * count towards one of the categories in the specified classifiers.
     * <p>
     * This method can be used to create a balance element group which is of a specific size and which is more likely to
     * cover all possible categories in the various dimensions of a balance procedure than a purely random procedure. Each
     * added element is counted as one towards the size specification; that is, the {@code size} parameters represents number
     * of elements, not sum of element weights.
     * <p>
     * If the number of unique elements in the source group is less than {@code size}, then the returned balance element
     * group will hold less than {@code size} elements.
     *
     * @param balanceElementGroup
     *        The source balance element group from which the balance elements will be drawn.
     *
     * @param classifiers
     *        The classifiers holding the categories that the sample should be representative of.
     *
     * @param filters
     *        The filters that the elements in the returned group must pass.
     *
     * @param size
     *        The (goal) size for the returned group.
     *
     * @param random
     *        The random number generator used when selecting the elements in the sample.
     *
     * @return a balance element group of {@code size} elements (if possible) from {@code group} which pass {@code filters}
     *         and which should cover each of the categories in {@code classifiers}.
     */
    public static <B extends BalanceElement> BalanceElementGroup<B> buildRepresentativeSample(BalanceElementGroup<B> balanceElementGroup, Collection<BalanceDimensionClassifier> classifiers, List<Filter<B>> filters, int size, Random random) {
        Filter<B> filter = new FilterChain<>(filters);
        BalanceElementGroup<B> filteredGroup = balanceElementGroup.getFilteredElements(filter);
        BalanceElementGroup<B> extraGroup = balanceElementGroup.getFilteredElements(new AntiFilter<>(filter));
        List<Map<Object,List<B>>> elementMaps = new LinkedList<>();
        //place all balance elements in the correct classification bins
        for (BalanceDimensionClassifier<?> classifier : classifiers) {
            Map<Object,List<B>> elementMap =  new HashMap<>();
            for (Object t : classifier.getClassificationCategories())
                elementMap.put(t,new LinkedList<B>());
            //place balance elements in the correct buckets
            for (B balanceElement : filteredGroup) {
                Map<?,Double> participationMap = classifier.getParticipationMap(balanceElement);
                for (Object o : participationMap.keySet())
                    if (participationMap.get(o) > 0.0)
                        elementMap.get(o).add(balanceElement);
            }
            //add filtered elements if category class is not available from filtered side
            for (Object o : elementMap.keySet())
                if (elementMap.get(o).isEmpty())
                    for (B balanceElement : extraGroup)
                        if (classifier.getParticipationMap(balanceElement).get(o) > 0.0)
                            elementMap.get(o).add(balanceElement);
            elementMaps.add(elementMap);
        }
        //now build sample
        //question: should the selection of an element be weighted by its weight? or should all elements get an even chance,
        //          and their weightedness is taken care of by the use of their weight in a balancing procedure?
        //          I'm opting for the latter, mainly because it is easier.
        Set<B> elements = new HashSet<>();
        while (elements.size() < size && elementMaps.size() > 0) { //either we hit the desired size, or there are no more elements to add
            Iterator<Map<Object,List<B>>> elementMapIt = elementMaps.iterator();
            while (elementMapIt.hasNext()) {
                Map<Object,List<B>> elementMap = elementMapIt.next();
                Iterator<Map.Entry<Object,List<B>>> elementIt = elementMap.entrySet().iterator();
                while (elementIt.hasNext()) {
                    Map.Entry<Object,List<B>> entry = elementIt.next();
                    List<B> elementList = entry.getValue();
                    //this version always adds a new element if it can (if it adds an element that is already added from another dimension, that doesn't count)
                    //  I guess this is to add some extra "diversity", though I'm not convinced this is correct
                    //while (!elementList.isEmpty() && !elements.add(elementList.remove(random.nextInt(elementList.size())))) { }

                    //this version says randomly pick an element - if it is already added, that is fine
                    //  I think this is more "correct", and will better capture whatever heterogeneity exists in the population
                    if (!elementList.isEmpty())
                        elements.add(elementList.remove(random.nextInt(elementList.size())));
                    if (elementList.isEmpty())
                        elementIt.remove();
                }
                if (elementMap.isEmpty())
                    elementMapIt.remove();
            }
        }
        return new BalanceElementGroup<>(elements);
    }
}
