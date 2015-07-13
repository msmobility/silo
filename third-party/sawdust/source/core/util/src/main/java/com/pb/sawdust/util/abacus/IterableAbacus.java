package com.pb.sawdust.util.abacus;

/**
 * The {@code IterableAbacus} class provides an abacus-like implementation of the {@code Iterable} interface. It simply
 * wraps a fresh clone of an {@code Abacus} into the appropriate interface, and is intended for one-time use (such as
 * with a for-each loop).
 *
 * @author crf <br/>
 *         Started: Jun 4, 2009 3:04:00 PM
 */
public class IterableAbacus implements Iterable<int[]> {
    private final Abacus abacus;

    public IterableAbacus(Abacus abacus) {
        this.abacus = abacus.freshClone();
    }

    public Abacus iterator() {
        return abacus.freshClone();
    }

    public static IterableAbacus getIterableAbacus(boolean reverse, int ... dimensions) {
        return new IterableAbacus(new Abacus(reverse,dimensions));
    }

    public static IterableAbacus getIterableAbacus(int ... dimensions) {
        return new IterableAbacus(new Abacus(dimensions));
    }
}
