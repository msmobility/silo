package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonRole;
import de.tum.bgu.msm.events.MicroEventModel;
import de.tum.bgu.msm.events.impls.person.DeathEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.properties.Properties;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

/**
 * @author Greg Erhardt, Rolf Moeckel, Ana Moreno
 * Created on Dec 2, 2009
 * Revised on Jan 19, 2018
 * Revised on Nov 14, 2018 to use precalculated probabilities from parametrized distribution
 */
public class DeathModel extends AbstractModel implements MicroEventModel<DeathEvent> {

    private DeathJSCalculator calculator;
    private HashMap<Gender, double[]> deathProbabilities;
    private Map<String, Double> parametersMap;

    public DeathModel(SiloDataContainer dataContainer,  Map<String, Double> parametersMap) {
        super(dataContainer);
        this.parametersMap = parametersMap;
        setupDeathModel();
        //setupDeathModelDistribution();

    }

    private void setupDeathModel() {
        Reader reader;
        if (Properties.get().main.implementation == Implementation.MUNICH) {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("DeathProbabilityCalcMuc"));
        } else {
            reader = new InputStreamReader(this.getClass().getResourceAsStream("DeathProbabilityCalcMstm"));
        }
        calculator = new DeathJSCalculator(reader);
    }

    private void setupDeathModelDistribution(){

        double alphaFemale = parametersMap.get("DeathFemaleAlpha");
        double scaleFemale = parametersMap.get("DeathFemaleScale");

        double alphaMale = parametersMap.get("DeathMaleAlpha");
        double scaleMale = parametersMap.get("DeathMaleScale");

        double[] probFemale = new double[101];
        double[] probMale = new double[101];
        for (int age = 55; age <= 100; age++){
            probFemale[age] = scaleFemale * Math.exp(age * alphaFemale);
            probMale[age] = scaleMale * Math.exp(age * alphaMale);
        }
        probFemale = copyDeathProbabilityFemaleYounger55(probFemale);
        probMale = copyDeathProbabilityMaleYounger55(probMale);
        deathProbabilities = new HashMap<>();
        deathProbabilities.put(Gender.FEMALE,probFemale);
        deathProbabilities.put(Gender.MALE, probMale);
    }

    @Override
    public boolean handleEvent(DeathEvent event) {

        // simulate if person with ID perId dies in this simulation period
        HouseholdDataManager householdData = dataContainer.getHouseholdData();
        final Person person = householdData.getPersonFromId(event.getPersonId());
        if (person != null) {
            final int age = Math.min(person.getAge(), 100);
            if (SiloUtil.getRandomNumberAsDouble() < calculator.calculateDeathProbability(age, person.getGender())) {
            //if (SiloUtil.getRandomNumberAsDouble() < deathProbabilities.get(person.getGender())[age]) {
                return die(person);
            }
        }
        return false;
    }

    @Override
    public void finishYear(int year) {
    }

    @Override
    public Collection<DeathEvent> prepareYear(int year) {
        final List<DeathEvent> events = new ArrayList<>();
        for (Person person : dataContainer.getHouseholdData().getPersons()) {
            events.add(new DeathEvent(person.getId()));
        }
        return events;
    }

    boolean die(Person person) {
        final HouseholdDataManager householdData = dataContainer.getHouseholdData();

        //removed for machine learning exercise
/*        if (person.getWorkplace() > 0) {
            dataContainer.getJobData().quitJob(true, person);
        }*/
        final Household hhOfPersonToDie = person.getHousehold();

        if (person.getRole() == PersonRole.MARRIED) {
            Person widow = HouseholdUtil.findMostLikelyPartner(person, hhOfPersonToDie);
            widow.setRole(PersonRole.SINGLE);
        }
        householdData.removePerson(person.getId());
        householdData.addHouseholdThatChanged(hhOfPersonToDie);

        final boolean onlyChildrenLeft = HouseholdUtil.checkIfOnlyChildrenRemaining(hhOfPersonToDie);
        if (onlyChildrenLeft) {
            for (Person pp : hhOfPersonToDie.getPersons().values()) {
                if (pp.getId() == SiloUtil.trackPp || hhOfPersonToDie.getId() == SiloUtil.trackHh) {
                    SiloUtil.trackWriter.println("Child " + pp.getId() + " was moved from household " + hhOfPersonToDie.getId() +
                            " to foster care as remaining child just before head of household (ID " +
                            person.getId() + ") passed away.");
                }
            }
            householdData.removeHousehold(hhOfPersonToDie.getId());
        }

        if (person.getId() == SiloUtil.trackPp || hhOfPersonToDie.getId() == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("We regret to inform that person " + person.getId() + " from household " + hhOfPersonToDie.getId() +
                    " has passed away.");
        }

        return true;
    }

    private double[] copyDeathProbabilityFemaleYounger55(double[] deathProbability){

        deathProbability[0] = 0.00300213;
        deathProbability[1] = 0.00025727;
        deathProbability[2] = 0.00011624;
        deathProbability[3] = 0.00011728;
        deathProbability[4] = 0.00009002;
        deathProbability[5] = 0.00007165;
        deathProbability[6] = 0.00006568;
        deathProbability[7] = 0.00005983;
        deathProbability[8] = 0.00006457;
        deathProbability[9] = 0.00005999;
        deathProbability[10] = 0.00005817;
        deathProbability[11] = 0.00006773;
        deathProbability[12] = 0.0000829;
        deathProbability[13] = 0.00007989;
        deathProbability[14] = 0.0001079;
        deathProbability[15] = 0.00012969;
        deathProbability[16] = 0.00015128;
        deathProbability[17] = 0.00014984;
        deathProbability[18] = 0.00019652;
        deathProbability[19] = 0.00019595;
        deathProbability[20] = 0.00019742;
        deathProbability[21] = 0.00017717;
        deathProbability[22] = 0.00016509;
        deathProbability[23] = 0.00023189;
        deathProbability[24] = 0.00019089;
        deathProbability[25] = 0.00020002;
        deathProbability[26] = 0.00020167;
        deathProbability[27] = 0.00021866;
        deathProbability[28] = 0.0002504;
        deathProbability[29] = 0.00027532;
        deathProbability[30] = 0.00028406;
        deathProbability[31] = 0.00032232;
        deathProbability[32] = 0.00033938;
        deathProbability[33] = 0.00037054;
        deathProbability[34] = 0.00039259;
        deathProbability[35] = 0.00042245;
        deathProbability[36] = 0.00046718;
        deathProbability[37] = 0.00047345;
        deathProbability[38] = 0.00057037;
        deathProbability[39] = 0.00063424;
        deathProbability[40] = 0.00067117;
        deathProbability[41] = 0.00074618;
        deathProbability[42] = 0.00084868;
        deathProbability[43] = 0.00092229;
        deathProbability[44] = 0.00104142;
        deathProbability[45] = 0.00111667;
        deathProbability[46] = 0.00131667;
        deathProbability[47] = 0.00146544;
        deathProbability[48] = 0.00158135;
        deathProbability[49] = 0.00182147;
        deathProbability[50] = 0.00200394;
        deathProbability[51] = 0.00230274;
        deathProbability[52] = 0.00251072;
        deathProbability[53] = 0.00285827;
        deathProbability[54] = 0.00307015;
        deathProbability[55] = 0.00336385;

        return deathProbability;
    }

    private double[] copyDeathProbabilityMaleYounger55(double[] deathProbability){

        deathProbability[0] = 0.0035171;
        deathProbability[1] = 0.00027502;
        deathProbability[2] = 0.00015091;
        deathProbability[3] = 0.00014069;
        deathProbability[4] = 0.00010809;
        deathProbability[5] = 0.00009131;
        deathProbability[6] = 0.00009202;
        deathProbability[7] = 0.00008643;
        deathProbability[8] = 0.00007416;
        deathProbability[9] = 0.00009636;
        deathProbability[10] = 0.00006965;
        deathProbability[11] = 0.00008112;
        deathProbability[12] = 0.00008548;
        deathProbability[13] = 0.00009094;
        deathProbability[14] = 0.0001113;
        deathProbability[15] = 0.00015519;
        deathProbability[16] = 0.0002484;
        deathProbability[17] = 0.00028661;
        deathProbability[18] = 0.00039548;
        deathProbability[19] = 0.00043643;
        deathProbability[20] = 0.00044619;
        deathProbability[21] = 0.00046719;
        deathProbability[22] = 0.00042421;
        deathProbability[23] = 0.00047249;
        deathProbability[24] = 0.00047635;
        deathProbability[25] = 0.00050404;
        deathProbability[26] = 0.00050092;
        deathProbability[27] = 0.0005178;
        deathProbability[28] = 0.00054553;
        deathProbability[29] = 0.00058138;
        deathProbability[30] = 0.00061109;
        deathProbability[31] = 0.00068366;
        deathProbability[32] = 0.00069727;
        deathProbability[33] = 0.00072194;
        deathProbability[34] = 0.00079542;
        deathProbability[35] = 0.00088198;
        deathProbability[36] = 0.00087339;
        deathProbability[37] = 0.00090841;
        deathProbability[38] = 0.00103223;
        deathProbability[39] = 0.00111491;
        deathProbability[40] = 0.00121116;
        deathProbability[41] = 0.00132725;
        deathProbability[42] = 0.00147968;
        deathProbability[43] = 0.00167684;
        deathProbability[44] = 0.00177439;
        deathProbability[45] = 0.00208116;
        deathProbability[46] = 0.00225744;
        deathProbability[47] = 0.00252991;
        deathProbability[48] = 0.00284653;
        deathProbability[49] = 0.00322336;
        deathProbability[50] = 0.00358139;
        deathProbability[51] = 0.00401607;
        deathProbability[52] = 0.00458896;
        deathProbability[53] = 0.00515636;
        deathProbability[54] = 0.00573763;
        deathProbability[55] = 0.0063011;
        return deathProbability;
    }
}
