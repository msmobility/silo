package de.tum.bgu.msm.SyntheticPopulationGenerator;

import com.pb.common.calculator.UtilityExpressionCalculator;
import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.data.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Generates a simple synthetic population for the MSTM Study Area
 * @author Shihang Zhang (Technical University of Munich)
 * Created on April 12, 2017 in Munich
 *
 */

public class TeleworkChoice {
    private ResourceBundle rb;
    static Logger logger = Logger.getLogger(TeleworkChoice.class);
    static Logger traceLogger = Logger.getLogger("trace");

    protected static final String PROPERTIES_Telework_UEC_FILE               = "Telework.UEC.FileName";
    protected static final String PROPERTIES_Telework_UEC_DATA_SHEET         = "Telework.UEC.DataSheetNumber";
    protected static final String PROPERTIES_Telework_UEC_UTILITY            = "Telework.UEC.UtilitySheetNumber";
    protected static final String PROPERTIES_LOG_UTILITY_CALCULATION_TELEWORK = "log.util.teleWork";
    public geoDataI geoData;
    private double [][][][][][][][] teleworkStatus; //[3 teleowrk probability][age][gender][hasElderPerson][nationality][householdStructure][education level][household income level]
    private double[] teleworkPro= new double[3]; // The probability for three type of telework choice.

    public TeleworkChoice(ResourceBundle rb) {
        // Constructor
        this.rb = rb;
    }

    public void run () {
        // Main run method
        logger.info("Started assigning telework");
        readSyntheticPopulation();
        selectTeleworkStatus ();
        for (Person pp: Person.getPersonArray()) {
            simulateTeleworkStatus(pp);
        }
        summarizeData.writeOutSyntheticPopulationDE(rb, 2011, "_result_");
        logger.info("Finished assigning telework");
    }

    // select telework for each person
    private void selectTeleworkStatus() {

        String uecFileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_Telework_UEC_FILE);
        int utilitySheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_Telework_UEC_UTILITY);
        int dataSheetNumber = ResourceUtil.getIntegerProperty(rb, PROPERTIES_Telework_UEC_DATA_SHEET);
        boolean logCalculation = ResourceUtil.getBooleanProperty(rb, PROPERTIES_LOG_UTILITY_CALCULATION_TELEWORK);

        UtilityExpressionCalculator teleworkUtility = new UtilityExpressionCalculator(new File(uecFileName),
                utilitySheetNumber,
                dataSheetNumber,
                SiloUtil.getRbHashMap(),
                TeleworkDMU.class);

        TeleworkDMU teleworkDMU = new TeleworkDMU();
        int numAltsTelework = teleworkUtility.getNumberOfAlternatives();
        int[] teAvail = new int[numAltsTelework + 1];
        for (int i = 1; i < teAvail.length; i++) {
            teAvail[i] = 1;
        }

        teleworkStatus = new double [3][96][2][2][2][3][3][3];

        for(int age=15;age<96;age++){
            for(int gender=0; gender<2; gender++){
                for(int hasElderlyPerson=0; hasElderlyPerson<2; hasElderlyPerson++){
                    for(int nationality=0; nationality<2; nationality++){
                        for(int hhStructure=0; hhStructure<3; hhStructure++){
                            for(int newEducationLevel=0; newEducationLevel<3; newEducationLevel++){
                                for(int hhIncomeLevel=0; hhIncomeLevel<3; hhIncomeLevel++){
                                    teleworkDMU.setAge(age);
                                    teleworkDMU.setGender(gender);
                                    teleworkDMU.setHasElderlyPerson(hasElderlyPerson);
                                    teleworkDMU.setNationality(nationality);
                                    teleworkDMU.setHhStructure(hhStructure);
                                    teleworkDMU.setNewEducationLevel(newEducationLevel);
                                    teleworkDMU.setHhIncomeLevel(hhIncomeLevel);

                                    double util[] = teleworkUtility.solve(teleworkDMU.getDmuIndexValues(), teleworkDMU, teAvail);

                                    for (int i = 1; i < teAvail.length; i++) {
                                        util[i-1] = Math.exp(util[i-1]);
                                    }
                                    double prob0telework = 1d / (SiloUtil.getSum(util) + 1d);
                                    for (int i = 1; i < teAvail.length; i++) {
                                        teleworkStatus[i-1][age][gender][hasElderlyPerson][nationality][hhStructure][newEducationLevel][hhIncomeLevel] = util[i-1] * prob0telework;
                                    }

                                    if (logCalculation){
                                        // log UEC values for each person type
                                        teleworkUtility.logAnswersArray(logger, "Telework");
                                        for (int i=0; i< util.length;i++) logger.info("Position "+i+": "+util[i]);
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void simulateTeleworkStatus(Person pp){
        // simulate only for employed person, exclude all unemployed person.
        if(pp.getOccupation()!=1) return;
        Household hh = Household.getHouseholdFromId(pp.getHhId());

        int telework; //telework: 0 no telework; 1 half or more worktime telework; 2 less than half worktime telework
        int age = pp.getAge();
        int gender=pp.getGender();
        int nationality=pp.getNationality();
        int newNationality;
        int hasElderlyPerson=0;
        boolean hasKid=false;
        PersonRole personRole=pp.getRole();
        int hhStructure;
        int educationLevel=pp.getEducationLevel();
        int newEducationLevel;
        int hhInc=hh.getHhIncome();
        int hhIncomeLevel;

        if(nationality==8) newNationality=0;
        else newNationality=1;

        // Variable hasElderlyPerson: whether or not the household has Elderly person that aged at least 65 years old.
        for(Person ppinHousehold:hh.getPersons()){
            if(ppinHousehold.getAge()>=65) hasElderlyPerson=1;
            if(ppinHousehold.getAge()<=15) hasKid=true;
        }

        // Variable hhStructure: 1 for Single without kids; 2 for Single with kids; 3 for Others(Married or Partner).
        if(personRole.equals("single") && hasKid==false) hhStructure=0;
        else if(personRole.equals("single") && hasKid==true) hhStructure=1;
        else hhStructure=2;

        // Classify household income into 3 levels: hhIncomeLevel=1 for 0-2600 Euro; hhIncomeLevel=2 for 2600-5000 Euro; hhIncomeLevel=3 for 5000- Euro.
        if(hhInc<=2600)hhIncomeLevel=0;
        else if(hhInc<=5000) hhIncomeLevel=1;
        else hhIncomeLevel=2;

        // Classify educationLevel into 3 levels: newEducationLevel=1 for original category educationLevel 1-2 ("Without beruflichen Abschluss", "Lehre, Berufausbildung im dual System", "Fachschulabschluss“， ”Abschluss einer Fachakademie“)；
        // newEducationLevel=2 for original category educationLevel 3 (Fachhochschulabschluss);
        // newEducationLevel=3 for original category educationLevel 4 (Hochschulabschluss - Uni, Promotion).
        // Original category educationLevel came from personEducation in SyntheticPopDe 1: without beruflichen Abschluss, 2: Lehre, Berufausbildung im dual System, Fachschulabschluss, Abschluss einer Fachakademie, 3: Fachhochschulabschluss, 4: Hochschulabschluss - Uni, Promotion.
        if(educationLevel<=2)newEducationLevel=0;
        else if(educationLevel==3) newEducationLevel=1;
        else newEducationLevel=2;

        for(int i=1; i<3; i++){
            teleworkPro[i]=teleworkStatus[i-1][age][gender-1][hasElderlyPerson][newNationality][hhStructure][newEducationLevel][hhIncomeLevel];
        }
        teleworkPro[0]=1 - SiloUtil.getSum(teleworkPro);
        telework=SiloUtil.select(teleworkPro);
        pp.setTelework(telework);
    }

    public void readSyntheticPopulation () {
        // read synthetic population, only temporarily used until creation of synthetic population is set up properly

        geoData = new geoDataMuc(rb);
        geoData.setInitialData();
        RealEstateDataManager realEstateData = new RealEstateDataManager(rb, geoData);
        HouseholdDataManager householdData = new HouseholdDataManager(rb, realEstateData);
        JobDataManager jobData = new JobDataManager(rb, geoData);
        householdData.readPopulation(true, 10);
        realEstateData.readDwellings(true, 10);
        jobData.readJobs(true, 10);
        householdData.connectPersonsToHouseholds();
        householdData.setTypeOfAllHouseholds();
        //writeOutSmallSP(10000);
    }

    private void writeOutSmallSP(int count) {
        // write out count number of households to have small file for running tests

        logger.info("  Writing out smaller files of synthetic population with " + count + " households only");
        String filehh = SiloUtil.baseDirectory + "microData/small_40k_hh_2011.csv";
        String filepp = SiloUtil.baseDirectory + "microData/small_40k_pp_2011.csv";
        String filedd = SiloUtil.baseDirectory + "microData/small_40k_dd_2011.csv";
        String filejj = SiloUtil.baseDirectory + "microData/small_40k_jj_2011.csv";
        PrintWriter pwh = SiloUtil.openFileForSequentialWriting(filehh, false);
        PrintWriter pwp = SiloUtil.openFileForSequentialWriting(filepp, false);
        PrintWriter pwd = SiloUtil.openFileForSequentialWriting(filedd, false);
        PrintWriter pwj = SiloUtil.openFileForSequentialWriting(filejj, false);
        pwh.println("id,dwelling,zone,hhSize,autos");
        pwp.println("id,hhID,age,gender,relationShip,race,occupation,driversLicense,workplace,income");
        pwd.println("id,zone,type,hhID,bedrooms,quality,monthlyCost,restriction,yearBuilt");
        pwj.println("id,zone,personId,type");
        Household[] hhs = Household.getHouseholdArray();
        int counter = 0;
        for (Household hh : hhs) {
            counter++;
            if (counter > count) break;
            // write out household attributes
            pwh.print(hh.getId());
            pwh.print(",");
            pwh.print(hh.getDwellingId());
            pwh.print(",");
            pwh.print(hh.getHomeZone());
            pwh.print(",");
            pwh.print(hh.getHhSize());
            pwh.print(",");
            pwh.println(hh.getAutos());
            // write out person attributes
            for (Person pp : hh.getPersons()) {
                pwp.print(pp.getId());
                pwp.print(",");
                pwp.print(pp.getHhId());
                pwp.print(",");
                pwp.print(pp.getAge());
                pwp.print(",");
                pwp.print(pp.getGender());
                pwp.print(",\"");
                pwp.print(pp.getRole());
                pwp.print("\",\"");
                pwp.print(pp.getRace());
                pwp.print("\",");
                pwp.print(pp.getOccupation());
                pwp.print(",0,");
                pwp.print(pp.getWorkplace());
                pwp.print(",");
                pwp.println(pp.getIncome());
                // write out job attributes (if person is employed)
                int job = pp.getWorkplace();
                if (job > 0 && pp.getOccupation() == 1) {
                    Job jj = Job.getJobFromId(job);
                    pwj.print(jj.getId());
                    pwj.print(",");
                    pwj.print(jj.getZone());
                    pwj.print(",");
                    pwj.print(jj.getWorkerId());
                    pwj.print(",\"");
                    pwj.print(jj.getType());
                    pwj.println("\"");
                }
            }
            // write out dwelling attributes
            Dwelling dd = Dwelling.getDwellingFromId(hh.getDwellingId());
            pwd.print(dd.getId());
            pwd.print(",");
            pwd.print(dd.getZone());
            pwd.print(",\"");
            pwd.print(dd.getType());
            pwd.print("\",");
            pwd.print(dd.getResidentId());
            pwd.print(",");
            pwd.print(dd.getBedrooms());
            pwd.print(",");
            pwd.print(dd.getQuality());
            pwd.print(",");
            pwd.print(dd.getPrice());
            pwd.print(",");
            pwd.print(dd.getRestriction());
            pwd.print(",");
            pwd.println(dd.getYearBuilt());
        }
        pwh.close();
        pwp.close();
        pwd.close();
        pwj.close();
        //System.exit(0);
    }

}
