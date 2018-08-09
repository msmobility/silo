package de.tum.bgu.msm.utils;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class DeferredAcceptanceTest {

    @Test
    public void test() {

        List<Integer> set1 = Lists.newArrayList(1,3,5,7,9);
        List<Integer> set2 = Lists.newArrayList(2,4,6,8,10);

        DoubleMatrix2D preferences = new DenseDoubleMatrix2D(11,11);
        preferences.setQuick(1,2, 7.);
        preferences.setQuick(1,4, 8.);
        preferences.setQuick(1, 6, 4.);
        preferences.setQuick(1, 8, 12.);
        preferences.setQuick(1, 10, 6.);

        preferences.setQuick(3, 2, 6);
        preferences.setQuick(3, 4, 9);
        preferences.setQuick(3, 6, 5);
        preferences.setQuick(3, 8, 4);
        preferences.setQuick(3, 10, 12);

        preferences.setQuick(5, 2, 1);
        preferences.setQuick(5, 4, 15);
        preferences.setQuick(5, 6, 7);
        preferences.setQuick(5, 8, 3);
        preferences.setQuick(5, 10, 9);

        preferences.setQuick(7,2, 1);
        preferences.setQuick(7, 4, 6);
        preferences.setQuick(7, 6, 2);
        preferences.setQuick(7, 8, 4);
        preferences.setQuick(7, 10, 8);

        preferences.setQuick(9, 2, 2);
        preferences.setQuick(9, 4, 7);
        preferences.setQuick(9, 6, 5);
        preferences.setQuick(9, 8, 1);
        preferences.setQuick(9, 10, 14);


        Map<Integer, Integer> matches = DeferredAcceptanceMatching.match(set1, set2, preferences);
        Assert.assertEquals(3, matches.get(2).intValue());
        Assert.assertEquals(5, matches.get(4).intValue());
        Assert.assertEquals(7, matches.get(6).intValue());
        Assert.assertEquals(1, matches.get(8).intValue());
        Assert.assertEquals(9, matches.get(10).intValue());
    }
}
