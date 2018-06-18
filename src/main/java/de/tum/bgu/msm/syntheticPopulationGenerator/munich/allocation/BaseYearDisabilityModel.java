package de.tum.bgu.msm.syntheticPopulationGenerator.munich.allocation;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Disability;
import de.tum.bgu.msm.data.Person;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.io.Reader;

public class BaseYearDisabilityModel {

    private static final Logger logger = Logger.getLogger(BaseYearDisabilityModel.class);

    private final SiloDataContainer dataContainer;
    private final BaseYearDisabilityJSCalculator calculator;

    private TableDataSet counts;


    public BaseYearDisabilityModel(SiloDataContainer dataContainer){
        logger.info(" Setting up probabilities for car ownership model");
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("BaseYearDisabilityCalc"));
        calculator = new BaseYearDisabilityJSCalculator(reader);
        this.dataContainer = dataContainer;
    }

    protected void run(){
        logger.info("   Running module: base year disability");
        initializeSummary();
        for (Person person : dataContainer.getHouseholdData().getPersons()){
            assignDisability(person);
            updateSummary(person);
        }
        SiloUtil.writeTableDataSet(counts, "microData/interimFiles/disabilityProb.csv");
    }

    private void initializeSummary() {
        counts = new TableDataSet();
        SiloUtil.addIntegerColumnToTableDataSet(counts, "withoutMale",100);
        SiloUtil.addIntegerColumnToTableDataSet(counts, "mentalMale",100);
        SiloUtil.addIntegerColumnToTableDataSet(counts, "physicalMale",100);
        SiloUtil.addIntegerColumnToTableDataSet(counts, "withoutFemale",100);
        SiloUtil.addIntegerColumnToTableDataSet(counts, "mentalFemale",100);
        SiloUtil.addIntegerColumnToTableDataSet(counts, "physicalFemale",100);


    }

    private void assignDisability(Person person){
        double disabilityProb = calculator.calculateBaseYearDisabilityProbability(person.getAge(), person.getGender());
        if (SiloUtil.getRandomNumberAsDouble() < disabilityProb){
            double disabilityTypeProb = calculator.probabilityForPhysicalDisability(person.getAge(), person.getGender());
            if (SiloUtil.getRandomNumberAsDouble() < disabilityTypeProb){
                person.setDisability(Disability.physical);
            } else {
                person.setDisability(Disability.mental);
            }
        }
    }


    private void updateSummary(Person person){
        String column = "";
        if (person.getGender() == 1) {
            if (person.getDisability().equals(Disability.physical)) {
                column = "physicalMale";
            } else if (person.getDisability().equals(Disability.mental)) {
                column = "mentalMale";
            } else if (person.getDisability().equals(Disability.without)) {
                column = "withoutMale";
            }
        } else if (person.getGender() == 2){
            if (person.getDisability().equals(Disability.physical)) {
                column = "physicalFemale";
            } else if (person.getDisability().equals(Disability.mental)) {
                column = "mentalFemale";
            } else if (person.getDisability().equals(Disability.without)) {
                column = "withoutFemale";
            }
        }
        int row = person.getAge();
        row = Math.min(row, 100);
        row = Math.max(1, row);
        //logger.info(person.getAge() + " and row " + row);
        float count = counts.getValueAt(row,column);
        counts.setValueAt(row, column, count + 1);
    }

}
