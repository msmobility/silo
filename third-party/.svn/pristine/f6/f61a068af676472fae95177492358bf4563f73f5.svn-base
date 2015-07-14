package com.pb.sawdust.model.models;

import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;

import static com.pb.sawdust.util.Range.*;

import java.util.Arrays;
import java.util.List;

/**
 * The {@code ChoiceUtil} class provides utility methods for creating choice sets for use with choice models.
 *
 * @author crf <br/>
 *         Started Sep 3, 2010 12:37:20 PM
 */
public class ChoiceUtil {

    /**
     * Get a setlist of choices built from a list of strings. The order of the choices will be the same as the order
     * passed to this method.
     *
     * @param choiceNames
     *        The names of the choices.
     *
     * @return a setlist of choices based on {@code choiceNames}.
     *
     * @throws IllegalArgumentException if any of the names in {@code choiceNames} is repeated.
     */
    public static SetList<StringChoice> getStringChoiceList(List<String> choiceNames) {
        SetList<StringChoice> c = new LinkedSetList<StringChoice>();
        for (String choiceName : choiceNames)
            if (!c.add(new StringChoice(choiceName)))
                throw new IllegalArgumentException("Repeated (non-unique) choice: " + choiceName);
        return c;
    }

    /**
     * Get a setlist of choices built from a series of strings. The order of the choices will be the same as the order
     * passed to this method.
     *
     * @param choiceNames
     *        The names of the choices.
     *
     * @return a setlist of choices based on {@code choiceNames}.
     *
     * @throws IllegalArgumentException if any of the names in {@code choiceNames} is repeated.
     */
    public static SetList<StringChoice> getStringChoiceList(String ... choiceNames) {
        return getStringChoiceList(Arrays.asList(choiceNames));
    }

    /**
     * Get a setlist of choices built from a list of integers. The order of the choices will be the same as the order
     * passed to this method.
     *
     * @param choices
     *        The choices.
     *
     * @return a setlist of choices based on {@code choices}.
     *
     * @throws IllegalArgumentException if any of the names in {@code choiceNames} is repeated.
     */
    public static SetList<IntChoice> getIntChoiceList(List<Integer> choices) {
        SetList<IntChoice> c = new LinkedSetList<IntChoice>();
        for (Integer choice : choices)
            if (!c.add(new IntChoice(choice)))
                throw new IllegalArgumentException("Repeated (non-unique) choice: " + choice);
        return c;
    }

    /**
     * Get a setlist of choices built from a series of strings. The order of the choices will be the same as the order
     * passed to this method.
     *
     * @param choices
     *        The names of the choices.
     *
     * @return a setlist of choices based on {@code choices}.
     *
     * @throws IllegalArgumentException if any of the names in {@code choices} is repeated.
     */
    public static SetList<IntChoice> getStringChoiceList(int ... choices) {
        return getIntChoiceList(Arrays.asList(ArrayUtil.toIntegerArray(choices)));
    }

    /**
     * Get a setlist of choices built from a range of {@code int}s. A {@link com.pb.sawdust.util.Range#Range(int, int)}
     * instance is used to build the list of integers, and that constructor should be referenced as such.
     *
     * @param start
     *        The start of the choice range (inclusive).
     *
     * @param end
     *        The end of the choice range (exclusive).
     *
     * @return a setlist of choices on {@code [start,end)}.
     *
     * @throws IllegalArgumentException if {@code start} and {@code end} are equal.
     */
    public static SetList<IntChoice> getChoiceRange(int start, int end) {
        SetList<IntChoice> c = new LinkedSetList<IntChoice>();
        for (int i : range(start,end))
            c.add(new IntChoice(i));
        return c;
    }

    /**
     * Get a setlist of choices containing all of the integers between zero and the specified length.
     *
     * @param length
     *        The end of the choices (exclusive).
     *
     * @return a setlist of choices from {@code 0} up to {@code length}.
     *
     * @throws IllegalArgumentException if {@code length} is zero.
     */
    public static SetList<IntChoice> getChoiceRange(int length) {
        return getChoiceRange(0,length);
    }

    /**
     * The {@code DefaultContainerChoice} class is a basic implementation of {@code ContainerChoice} whose choice contents
     * are specified at construction time. By default, the choice's unique id will be the string value (from invoking
     * {@code toString()} of the contents.
     *
     * @param <C>
     *        The type of the contents held by the choice.
     */
    public static abstract class DefaultContainerChoice<C> implements ContainerChoice<C> {
        private final C contents;

        /**
         * Constructor specifying the contents held by the choice.
         *
         * @param contents
         *        The contents held by the choice.
         */
        protected DefaultContainerChoice(C contents) {
            this.contents = contents;
        }

        @Override
        public C getChoiceContents() {
            return contents;
        }

        @Override
        public String getChoiceIdentifier() {
            return contents.toString();
        }
    }

    /**
     * The {@code IntChoice} class is an {@code int} wrapped as a choice.
     */
    public static class IntChoice extends DefaultContainerChoice<Integer> {
        private final int choice;

        public IntChoice(int choice) {
            super(choice);
            this.choice = choice;
        }

        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (o == null)
                return false;
            if (!(o instanceof IntChoice))
                return false;
            return ((IntChoice) o).choice == choice;
        }

        public int hashCode() {
            return 17 + 31*choice;
        }

        public String toString() {
            return "choice<" + choice + ">";
        }

        @Override
        public Integer getChoiceContents() {
            return choice;
        }
    }

    /**
     * The {@code StringChoice} class is a {@code String} wrapped as a choice.
     */
    public static class StringChoice extends DefaultContainerChoice<String> {
        private final String choice;
        public StringChoice(String choice) {
            super(choice);
            this.choice = choice;
        }

        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (o == null)
                return false;
            if (!(o instanceof StringChoice))
                return false;
            return ((StringChoice) o).choice == choice;
        }

        public int hashCode() {
            return 17 + 31*choice.hashCode();
        }

        public String toString() {
            return "choice<" + choice + ">";
        }

        @Override
        public String getChoiceContents() {
            return choice;
        }
    }
}
