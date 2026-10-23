package de.tum.bgu.msm.matsim;

import de.tum.bgu.msm.data.person.Occupation;
import org.junit.jupiter.api.Test;

import static de.tum.bgu.msm.matsim.SimpleCommuteModeChoiceMatsimScenarioAssembler.*;
import static org.junit.jupiter.api.Assertions.*;

class SimpleCommuteModeChoiceMatsimScenarioAssemblerTest {

    @Test
    void onlyEmployedWithRealJobCounts() {
        assertTrue(isEmployedWithJob(Occupation.EMPLOYED, 5));
        assertFalse(isEmployedWithJob(Occupation.EMPLOYED, -2));
        assertFalse(isEmployedWithJob(Occupation.UNEMPLOYED, 5));
        assertFalse(isEmployedWithJob(Occupation.UNEMPLOYED, -2));
    }

    @Test
    void homeOrWorkChange() {
        assertFalse(homeOrWorkChanged(1, 2, 1, 2));
        assertTrue(homeOrWorkChanged(1, 2, 9, 2));
        assertTrue(homeOrWorkChanged(1, 2, 1, 9));
    }

    @Test
    void classifyCoversAllCases() {
        assertEquals(Outcome.DROP,   classify(false, true,  false, false));
        assertEquals(Outcome.DROP,   classify(true,  false, false, false));
        assertEquals(Outcome.KEEP,   classify(true,  true,  false, false));
        assertEquals(Outcome.REPLAN, classify(true,  true,  true,  false));
        assertEquals(Outcome.REPLAN, classify(true,  true,  false, true));
    }


//    @Test
//    void randomSampleIsDistinctSubsetAndDeterministic() {
//        List<Integer> src = IntStream.range(0, 50).boxed().toList();
//        List<Integer> a = getRandomSample(src, 10, new Random(1));
//        assertEquals(10, new HashSet<>(a).size());
//        assertTrue(src.containsAll(a));
//        assertEquals(a, getRandomSample(src, 10, new Random(1)));
//        assertThrows(IllegalArgumentException.class, () -> getRandomSample(src, 51, new Random(1)));
//    }
}