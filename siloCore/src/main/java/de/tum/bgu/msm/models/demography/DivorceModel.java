package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdFactory;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonRole;
import de.tum.bgu.msm.events.IssueCounter;
import de.tum.bgu.msm.events.MicroEventModel;
import de.tum.bgu.msm.events.impls.person.DivorceEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.autoOwnership.munich.CreateCarOwnershipModel;
import de.tum.bgu.msm.models.relocation.MovesModelI;
import de.tum.bgu.msm.properties.Properties;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class DivorceModel extends AbstractModel implements MicroEventModel<DivorceEvent> {

    private final MovesModelI movesModel;
    private final CreateCarOwnershipModel carOwnership;
    private final HouseholdFactory hhFactory;
    private Map<String, Double> parametersMap;

    private MarryDivorceJSCalculator calculator;
    private HashMap<Gender, double[]> divorceProbabilities;

    public DivorceModel(SiloDataContainer dataContainer, MovesModelI movesModel, CreateCarOwnershipModel carOwnership, HouseholdFactory hhFactory, Map<String, Double> parametersMap) {
        super(dataContainer);
        this.hhFactory = hhFactory;
        this.parametersMap = parametersMap;
        //setupModel();
        setupModelDistribution();
        this.movesModel = movesModel;
        this.carOwnership = carOwnership;
    }

    private void setupModel() {
        Reader reader;
        if (Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("MarryDivorceCalcMuc"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("MarryDivorceCalcMstm"));
        }
        calculator = new MarryDivorceJSCalculator(reader, 0);
    }


    private void setupModelDistribution(){

        LogNormalDistribution femaleNormalDistribution = new LogNormalDistribution(
                parametersMap.get("DivorceFemaleLogMean"), parametersMap.get("DivorceFemaleLogShape"));
        double scaleFemaleNormal = parametersMap.get("DivorceFemaleLogScale");
        GammaDistribution femaleGammaDistribution = new GammaDistribution(
                parametersMap.get("DivorceFemaleGammaMean"), parametersMap.get("DivorceFemaleGammaShape"));
        double scaleFemaleGamma = parametersMap.get("DivorceFemaleGammaScale");

        LogNormalDistribution maleNormalDistribution = new LogNormalDistribution(
                parametersMap.get("DivorceMaleLogMean"), parametersMap.get("DivorceMaleLogShape"));
        double scaleMaleNormal = parametersMap.get("DivorceMaleLogScale");
        GammaDistribution maleGammaDistribution = new GammaDistribution(
                parametersMap.get("DivorceMaleGammaMean"), parametersMap.get("DivorceMaleGammaShape"));
        double scaleMaleGamma = parametersMap.get("DivorceMaleGammaScale");

        double[] probFemale = new double[101];
        double[] probMale = new double[101];
        for (int age = 15; age <= 100; age++){
            probFemale[age] = scaleFemaleNormal * femaleNormalDistribution.density((double) age) +
                    scaleFemaleGamma * femaleGammaDistribution.density((double) age);
            probMale[age] = scaleMaleNormal * maleNormalDistribution.density((double) age) +
                    scaleMaleGamma * maleGammaDistribution.density((double) age);
        }
        divorceProbabilities = new HashMap<>();
        divorceProbabilities.put(Gender.FEMALE,probFemale);
        divorceProbabilities.put(Gender.MALE, probMale);
    }

    @Override
    public Collection<DivorceEvent> prepareYear(int year) {
        final List<DivorceEvent> events = new ArrayList<>();
        for(Person person: dataContainer.getHouseholdData().getPersons()) {
            if (person.getRole() == PersonRole.MARRIED) {
                events.add(new DivorceEvent(person.getId()));
            }
        }
        return events;
    }

    @Override
    public boolean handleEvent(DivorceEvent event) {
        return chooseDivorce(event.getPersonId());    }

    @Override
    public void finishYear(int year) {

    }

    private boolean chooseDivorce(int perId) {
        // select if person gets divorced/leaves joint dwelling

        final HouseholdDataManager householdData = dataContainer.getHouseholdData();
        Person per = householdData.getPersonFromId(perId);
        if (per != null && per.getRole() == PersonRole.MARRIED) {
            //final double probability = calculator.calculateDivorceProbability(per.getType().ordinal()) / 2;
            final double probability = divorceProbabilities.get(per.getGender())[Math.min(per.getAge(),100)] / 2;
            if (SiloUtil.getRandomNumberAsDouble() < probability) {
                // check if vacant dwelling is available

                Household fakeHypotheticalHousehold = hhFactory.createHousehold(-1,-1,0);
                fakeHypotheticalHousehold.addPerson(per);
                int newDwellingId = movesModel.searchForNewDwelling(fakeHypotheticalHousehold);
                if (newDwellingId < 0) {
                    if (perId == SiloUtil.trackPp || per.getHousehold().getId() == SiloUtil.trackHh) {
                        SiloUtil.trackWriter.println(
                                "Person " + perId + " wanted to but could not divorce from household "
                                        + per.getHousehold().getId() + " because no appropriate vacant dwelling was found.");
                    }
                    IssueCounter.countLackOfDwellingFailedDivorce();
                    return false;
                }

                // divorce
                Household oldHh = householdData.getHouseholdFromId(per.getHousehold().getId());
                Person divorcedPerson = HouseholdUtil.findMostLikelyPartner(per, oldHh);
                divorcedPerson.setRole(PersonRole.SINGLE);
                per.setRole(PersonRole.SINGLE);
                householdData.removePersonFromHousehold(per);

                int newHhId = householdData.getNextHouseholdId();
                Household newHh = hhFactory.createHousehold(newHhId, -1, 0);
                householdData.addHousehold(newHh);
                householdData.addPersonToHousehold(per, newHh);

                // move divorced person into new dwelling
                movesModel.moveHousehold(newHh, -1, newDwellingId);
                if (perId == SiloUtil.trackPp || newHh.getId() == SiloUtil.trackHh ||
                        oldHh.getId() == SiloUtil.trackHh) SiloUtil.trackWriter.println("Person " + perId +
                        " has divorced from household " + oldHh + " and established the new household " +
                        newHhId + ".");
                householdData.addHouseholdThatChanged(oldHh); // consider original household for update in car ownership
                if (Properties.get().main.implementation == Implementation.MUNICH) {
                    carOwnership.simulateCarOwnership(newHh); // set initial car ownership of new household
                }
                return true;
            }
        }
        return false;
    }
}
