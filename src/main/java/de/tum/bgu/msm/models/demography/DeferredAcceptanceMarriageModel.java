package de.tum.bgu.msm.models.demography;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Accessibility;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.data.PersonRole;
import de.tum.bgu.msm.events.impls.MarriageEvent;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.DeferredAcceptanceMatching;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DeferredAcceptanceMarriageModel implements MarriageModel {

    private final SiloDataContainer dataContainer;
    private final Accessibility accessibility;
    private MarryDivorceJSCalculator calculator;


    public DeferredAcceptanceMarriageModel(SiloDataContainer dataContainer, Accessibility accessibility) {
        this.dataContainer = dataContainer;
        this.accessibility = accessibility;
        setupModel();
    }

    private void setupModel() {
        // localMarriageAdjuster serves to adjust from national marriage rates to local conditions
        double scale = Properties.get().demographics.localMarriageAdjuster;

        Reader reader;
        if (Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("MarryDivorceCalcMuc"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("MarryDivorceCalcMstm"));
        }
        calculator = new MarryDivorceJSCalculator(reader, scale);
    }

    @Override
    public Collection<MarriageEvent> prepareYear(int year) {
        Collection<MarriageEvent> couples = determineCouples();
        return couples;
    }

    @Override
    public boolean handleEvent(MarriageEvent event) {
        return false;
    }

    @Override
    public void finishYear(int year) {

    }

    private Collection<MarriageEvent> determineCouples() {

        Set<MarriageEvent> couples = new HashSet<>();
        Set<Integer> bachelors = new HashSet<>();
        Set<Integer> bachelorettes = new HashSet<>();

        for(Person person: dataContainer.getHouseholdData().getPersons()) {
            if(ruleGetMarried(person) && SiloUtil.getRandomNumberAsDouble() <= 15 * getMarryProb(person)) {
                if(person.getGender() == 1) {
                    bachelors.add(person.getId());
                } else {
                    if(bachelorettes.size()< bachelors.size()) {
                        bachelorettes.add(person.getId());
                    }
                }
            }
        }

        DoubleMatrix2D preferences = new DenseDoubleMatrix2D(
                bachelors.stream().mapToInt(Integer::intValue).max().getAsInt()+1,
                bachelorettes.stream().mapToInt(Integer::intValue).max().getAsInt()+1);
        for(Integer id: bachelors) {
            Person bachelor = dataContainer.getHouseholdData().getPersonFromId(id);
            int bachelorZone = dataContainer.getRealEstateData().getDwelling(bachelor.getHh().getDwellingId()).getZone();
            for(Integer id2: bachelorettes) {
                Person bachelorette = dataContainer.getHouseholdData().getPersonFromId(id2);
                int bacheloretteZone = dataContainer.getRealEstateData().getDwelling(bachelorette.getHh().getDwellingId()).getZone();
                double travelTime = accessibility.getPeakAutoTravelTime(bachelorZone, bacheloretteZone);
                double ageBias = (bachelor.getAge() -1) - bachelorette.getAge();
                double educationOffset = Math.abs(bachelor.getEducationLevel() - bachelorette.getEducationLevel());
                double nationalityBias = bachelor.getNationality() == bachelorette.getNationality() ? 1: 0.5;
                double utility = 40./travelTime + 1./ageBias + 1/(1.+educationOffset) + nationalityBias;
                preferences.setQuick(bachelor.getId(), bachelorette.getId(), utility);
            }
        }
        Map<Integer, Integer> matches = DeferredAcceptanceMatching.match(bachelors, bachelorettes, preferences);
        for(Map.Entry<Integer, Integer> match: matches.entrySet()) {
            couples.add(new MarriageEvent(match.getKey(), match.getValue()));
        }
        return couples;
    }


    private boolean ruleGetMarried (Person per) {
        PersonRole role = per.getRole();
        return (role == PersonRole.SINGLE || role == PersonRole.CHILD)
                && per.getAge() >= Properties.get().demographics.minMarryAge
                && per.getAge() < 100;
    }

    /**
     * returns marriage probability for a person. Single-person households tend to be more likely to get married,
     * thus, emphasize prop to initialize marriage for people from single-person households.
     */
    private double getMarryProb(Person pp) {
        double marryProb = calculator.calculateMarriageProbability(pp);
        if (pp.getHh().getHhSize() == 1) {
            marryProb *= Properties.get().demographics.onePersonHhMarriageBias;
        }
        return marryProb;
    }
}
