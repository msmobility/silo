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

/**
 * done 1: Read interested variables like household structure, number of Olders in the household .. from data resource/ (it is not in the current data input files)
 * done 2: Careful with the categories of variables, like gender (0 male; 1 female. or 1 male; 2 female); hhInc, nationality, educationLevel (3 categories? 2 categories?)
 * 3: Use the Utility Expression Calculator method to calculate the teleworkStatus Probability, which need to creat DataSheet and UtilitySheet in new excel file,
 * and add the file name into the siloMuc.properities file.
 * */
public class TeleworkChoice {
    private ResourceBundle rb;
    static Logger logger = Logger.getLogger(TeleworkChoice.class);
    static Logger traceLogger = Logger.getLogger("trace");

    protected static final String PROPERTIES_Telework_UEC_FILE               = "Telework.UEC.FileName";
    protected static final String PROPERTIES_Telework_UEC_DATA_SHEET         = "Telework.UEC.DataSheetNumber";
    protected static final String PROPERTIES_Telework_UEC_UTILITY            = "Telework.UEC.UtilitySheetNumber";
    protected static final String PROPERTIES_LOG_UTILITY_CALCULATION_TELEWORK = "log.util.teleWork";

    //Read the synthetic population
    protected static final String PROPERTIES_HOUSEHOLD_SYN_POP            = "household.file.ascii";
    protected static final String PROPERTIES_PERSON_SYN_POP               = "person.file.ascii";
    protected static final String PROPERTIES_DWELLING_SYN_POP             = "dwelling.file.ascii";
    protected static final String PROPERTIES_JOB_SYN_POP                  = "job.file.ascii";
    protected static final String PROPERTIES_ATRIBUTES_MICRODATA_PP       = "read.attributes.pp";
    protected static final String PROPERTIES_ATRIBUTES_MICRODATA_HH       = "read.attributes.hh";
    protected static final String PROPERTIES_SCHOOL_DESCRIPTION           = "school.dictionary";
    protected static final String PROPERTIES_PP_FILE_ASCII                = "person.file.ascii";
    protected TableDataSet schoolLevelTable;
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

        teleworkStatus = new double [2][96][2][2][2][3][3][3];

        for(int age=20;age<96;age++){
            for(int gender=1; gender<3; gender++){
                for(int hasElderlyPerson=1; hasElderlyPerson<3; hasElderlyPerson++){
                    for(int nationality=0; nationality<2; nationality++){
                        for(int hhStructure=1; hhStructure<4; hhStructure++){
                            for(int newEducationLevel=1; newEducationLevel<4; newEducationLevel++){
                                for(int hhIncomeLevel=1; hhIncomeLevel<4; hhIncomeLevel++){
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

                                    teleworkPro[0] = 1d / (SiloUtil.getSum(util) + 1d);
                                    for (int i = 1; i < teAvail.length; i++) {
                                        teleworkStatus[i-1][age][gender-1][hasElderlyPerson-1][nationality][hhStructure-1][newEducationLevel-1][hhIncomeLevel-1] = util[i-1] * teleworkPro[0];
                                    }
/*
                                    if (logCalculation) {
                                        // log UEC values for each person type
                                        teleworkUtility.logAnswersArray(traceLogger, "Telework Choice Model. age: " + age +
                                                ", gender: " + gender + ", hasElderlyPerson: " + hasElderlyPerson + ", nationality: " + nationality + ", hhStructure: " + hhStructure +
                                                ", educationLevel: " + newEducationLevel + ", hhIncomeLevel: " + hhIncomeLevel);

                                        logger.info(age + "," + gender + "," + hasElderlyPerson + "," + nationality + "," + hhStructure + "," + newEducationLevel + "," + hhIncomeLevel +
                                                "," + teleworkPro[0] + "," +
                                                teleworkStatus[0][age][gender][hasElderlyPerson][nationality][hhStructure][newEducationLevel][hhIncomeLevel] + "," +
                                                teleworkStatus[1][age][gender][hasElderlyPerson][nationality][hhStructure][newEducationLevel][hhIncomeLevel] + "," +
                                                teleworkStatus[2][age][gender][hasElderlyPerson][nationality][hhStructure][newEducationLevel][hhIncomeLevel]);
                                    }
                                    */
                                }
                            }
                        }
                    }
                }
            }
        }

        //////estimate the Probability for different telework status///////////
        /*
        if(educationLevel==1) {
            if(hhInc==1){
                teleworkPro[1] = 1 - 1d / (1d + Math.exp(-3.394 + 0.032 * age + 0.119 * gender + 0.225 * nationality - 1.263 * educationLevel - 0.615 * hhInc));
                teleworkPro[2] = 1 - 1d / (1d + Math.exp(-0.703 + 0.012 * age - 0.363 * gender + 0.296 * nationality - 1.926 * educationLevel - 1.215 * hhInc));
            }else if(hhInc==2) {
                teleworkPro[1] = 1 - 1d / (1d + Math.exp(-3.394 + 0.032 * age + 0.119 * gender + 0.225 * nationality - 1.263 * educationLevel - 0.577 * (hhInc-1)));
                teleworkPro[2] = 1 - 1d / (1d + Math.exp(-0.703 + 0.012 * age - 0.363 * gender + 0.296 * nationality - 1.926 * educationLevel - 0.708 * (hhInc-1)));
            }
            else {
                teleworkPro[1] = 1 - 1d / (1d + Math.exp(-3.394 + 0.032 * age + 0.119 * gender + 0.225 * nationality - 1.263 * educationLevel - 0.000 * hhInc));
                teleworkPro[2] = 1 - 1d / (1d + Math.exp(-0.703 + 0.012 * age - 0.363 * gender + 0.296 * nationality - 1.926 * educationLevel - 0.000 * hhInc));
            }
        }else if(educationLevel==2){
            if(hhInc==1) {
                teleworkPro[1] = 1 - 1d / (1d + Math.exp(-3.394 + 0.032 * age + 0.119 * gender + 0.225 * nationality - 0.637 * (educationLevel-1) - 0.615 * hhInc));
                teleworkPro[2] = 1 - 1d / (1d + Math.exp(-0.703 + 0.012 * age - 0.363 * gender + 0.296 * nationality - 0.889 * (educationLevel-1) - 1.215 * hhInc));
            }else if(hhInc==2) {
                teleworkPro[1] = 1 - 1d / (1d + Math.exp(-3.394 + 0.032 * age + 0.119 * gender + 0.225 * nationality - 0.637 * (educationLevel-1) - 0.577 * (hhInc-1)));
                teleworkPro[2] = 1 - 1d / (1d + Math.exp(-0.703 + 0.012 * age - 0.363 * gender + 0.296 * nationality - 0.889 * (educationLevel-1) - 0.708 * (hhInc-1)));
            }else {
                teleworkPro[1] = 1 - 1d / (1d + Math.exp(-3.394 + 0.032 * age + 0.119 * gender + 0.225 * nationality - 0.637 * (educationLevel-1) - 0.000 * hhInc));
                teleworkPro[2] = 1 - 1d / (1d + Math.exp(-0.703 + 0.012 * age - 0.363 * gender + 0.296 * nationality - 0.889 * (educationLevel-1) - 0.000 * hhInc));
            }
        }else {
            if(hhInc==1) {
                teleworkPro[1] = 1 - 1d / (1d + Math.exp(-3.394 + 0.032 * age + 0.119 * gender + 0.225 * nationality - 0.000 * educationLevel - 0.615 * hhInc));
                teleworkPro[2] = 1 - 1d / (1d + Math.exp(-0.703 + 0.012 * age - 0.363 * gender + 0.296 * nationality - 0.000 * educationLevel - 1.215 * hhInc));
            }else if(hhInc==2) {
                teleworkPro[1] = 1 - 1d / (1d + Math.exp(-3.394 + 0.032 * age + 0.119 * gender + 0.225 * nationality - 0.000 * educationLevel - 0.577 * (hhInc-1)));
                teleworkPro[2] = 1 - 1d / (1d + Math.exp(-0.703 + 0.012 * age - 0.363 * gender + 0.296 * nationality - 0.000 * educationLevel - 0.708 * (hhInc-1)));
            }else {
                teleworkPro[1] = 1 - 1d / (1d + Math.exp(-3.394 + 0.032 * age + 0.119 * gender + 0.225 * nationality - 0.000 * educationLevel - 0.000 * hhInc));
                teleworkPro[2] = 1 - 1d / (1d + Math.exp(-0.703 + 0.012 * age - 0.363 * gender + 0.296 * nationality - 0.000 * educationLevel - 0.000 * hhInc));
            }
        }

        teleworkPro[0]=1-teleworkPro[1]-teleworkPro[2];
        double randomNumber;
        randomNumber=Math.random();
        if (randomNumber>=0 && randomNumber<=teleworkPro[0]){telework=0;}
        else if (randomNumber>=teleworkPro[0] && randomNumber<=teleworkPro[0]+teleworkPro[1]) {telework=1;}
        else {telework=2;}
        */
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
        //boolean hasElderlyPerson=false;
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
            //if(ppinHousehold.getAge()>=65) hasElderlyPerson=true;
            if(ppinHousehold.getAge()>=65) hasElderlyPerson=1;
            if(ppinHousehold.getAge()<=16) hasKid=true;
        }

        // Variable hhStructure: 1 for Single without kids; 2 for Single with kids; 3 for Others(Married or Partner).
        if(personRole.equals("single") && hasKid==false) hhStructure=1;
        else if(personRole.equals("single") && hasKid==true) hhStructure=2;
        else hhStructure=3;

        // Classify household income into 3 levels: hhIncomeLevel=1 for 0-2600 Euro; hhIncomeLevel=2 for 2600-5000 Euro; hhIncomeLevel=3 for 5000- Euro.
        if(hhInc<=2600)hhIncomeLevel=1;
        else if(hhInc<=5000) hhIncomeLevel=2;
        else hhIncomeLevel=3;

        // Classify educationLevel into 3 levels: newEducationLevel=1 for original category educationLevel 1-2 ("Without beruflichen Abschluss", "Lehre, Berufausbildung im dual System", "Fachschulabschluss“， ”Abschluss einer Fachakademie“)；
        // newEducationLevel=2 for original category educationLevel 3 (Fachhochschulabschluss);
        // newEducationLevel=3 for original category educationLevel 4 (Hochschulabschluss - Uni, Promotion).
        // Original category educationLevel came from personEducation in SyntheticPopDe 1: without beruflichen Abschluss, 2: Lehre, Berufausbildung im dual System, Fachschulabschluss, Abschluss einer Fachakademie, 3: Fachhochschulabschluss, 4: Hochschulabschluss - Uni, Promotion.
        if(educationLevel<=2)newEducationLevel=1;
        else if(educationLevel==3) newEducationLevel=2;
        else newEducationLevel=3;

        for(int i=1; i<3; i++){
            teleworkPro[i]=teleworkStatus[i-1][age][gender-1][hasElderlyPerson][nationality][hhStructure-1][newEducationLevel-1][hhIncomeLevel-1];
        }

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
