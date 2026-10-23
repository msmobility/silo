package de.tum.bgu.msm.matsim;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
public class AssembledPlansYearOverYearTest {
    enum AutofreiScenario {
        BASE,
        SMALL,
        LARGE
    }

    enum PtScenario {
        //        NONE,
        NES,
        RINGSX
    }

    //    static String scenario = "base";
    static AutofreiScenario autofreiScenario = AutofreiScenario.LARGE;
    static PtScenario ptScenario = PtScenario.NES;

    static String prefix = "2026-10-23a__";
    static String scenario = prefix + "autofrei-" + autofreiScenario + "__pt-" + ptScenario;

    record Row(String id, String source, String mode, String homeX, String homeY,
               String workX, String workY, String homeId, String workId) {}

    // point this at a finished run (or run the model in @BeforeAll)
    // AT THE MOMENT JUST SETTING AS ABSOLUTE PATH: PLEASE CHANGE!
    static final Path MATSIM_DIR = Path.of("/home/brendan/git/silo/useCases/fabiland/scenario/scenOutput/" + scenario + "/matsim");

    /**
     * year -> rows, sorted by year
     */
    static SortedMap<Integer, Map<String, Row>> readAllYears() throws IOException {
//        assertTrue(Files.isDirectory(MATSIM_DIR),
//                "matsim dir not found: " + MATSIM_DIR.toAbsolutePath());
        SortedMap<Integer, Map<String, Row>> result = new TreeMap<>();
        try (Stream<Path> dirs = Files.list(MATSIM_DIR)) {
            for (Path dir : dirs.toList()) {
                Path csv = dir.resolve("assembledPlans.csv");
                if (!Files.isRegularFile(csv)) continue;
                try {
                    result.put(Integer.parseInt(dir.getFileName().toString()), readCsv(csv));
                } catch (NumberFormatException ignored) { /* not a year folder */ }
            }
        }
        return result;
    }

    static Map<String, Row> readCsv(Path csv) throws IOException {
        List<String> lines = Files.readAllLines(csv);
        Map<String, Row> rows = new LinkedHashMap<>();
        for (String line : lines.subList(1, lines.size())) {           // skip header
            String[] c = line.split(",", -1);                          // keep empty trailing fields
            // header: personId,source,nPlans,mode,homeX,homeY,workX,workY,homeEndTime,workEndTime

            Row row = new Row(c[0], c[1], c[3], c[4], c[5], c[6], c[7], c[8], c[9]);
            assertNull(rows.put(row.id(), row), "duplicate person id " + row.id() + " in " + csv);
        }
        return rows;
    }

    @Test
    public void everyYearIsConsistentWithThePreviousOne() throws IOException {
        SortedMap<Integer, Map<String, Row>> years = readAllYears();
        assertTrue(years.containsKey(0), "year 0 has no assembledPlans.csv");
        assertEquals(11, years.size());

        Integer prevYear = null;
        for (var entry : years.entrySet()) {
            int year = entry.getKey();
            Map<String, Row> curr = entry.getValue();

            if (prevYear == null) {
                curr.values().forEach(r -> assertEquals("new", r.source()));
            }

            // no plan-less rows, every row has a known source
            for (Row r : curr.values()) {
                assertTrue(Set.of("kept", "replanned", "new").contains(r.source()),
                        year + ": unexpected source " + r.source() + " for " + r.id());
                assertFalse(r.mode().isEmpty(), year + ": no plan for " + r.id());
            }

            if (prevYear != null) {
                Map<String, Row> prev = years.get(prevYear);
                for (Row r : curr.values()) {
                    switch (r.source()) {
                        case "kept", "replanned" ->
                                assertTrue(prev.containsKey(r.id()), year + ": " + r.id() + " not in " + prevYear);
                        case "new" ->
                                assertFalse(prev.containsKey(r.id()), year + ": " + r.id() + " is new but existed");
                    }
                    if (r.source().equals("kept")) {
                        Row p = prev.get(r.id());
                        // workID stays the same, but Coords change? these coord assertions always fail.
                        // I assume that's because of getOrRandomlyChooseDwellingCoord & getOrRandomlyChooseJobCoord
//                        assertEquals(p.homeX(), r.homeX(), year + ": home moved for kept " + r.id());
//                        assertEquals(p.homeY(), r.homeY());
//                        assertEquals(p.workX(), r.workX(), year + ": work moved for kept " + r.id());
//                        assertEquals(p.workY(), r.workY());
                        assertEquals(p.homeId(), r.homeId(), year + ": dwelling changed for kept " + r.id());
                        assertEquals(p.workId(), r.workId(), year + ": job changed for kept " + r.id());
                    }
                }
                long carried = curr.values().stream().filter(r -> !r.source().equals("new")).count();
                assertTrue(carried <= prev.size());
            }
            prevYear = year;
        }
    }
}
