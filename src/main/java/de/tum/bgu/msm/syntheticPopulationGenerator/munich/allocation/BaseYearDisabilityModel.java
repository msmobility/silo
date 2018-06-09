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
        SiloUtil.addIntegerColumnToTableDataSet(counts, "without",100);
        SiloUtil.addIntegerColumnToTableDataSet(counts, "mental",100);
        SiloUtil.addIntegerColumnToTableDataSet(counts, "physical",100);

    }

    private void assignDisability(Person person){
        double disabilityProb = calculator.calculateBaseYearDisabilityProbability(person.getAge(), person.getGender());
        if (SiloUtil.getRandomNumberAsDouble() < disabilityProb){
            double disabilityTypeProb = calculator.calculateDisabilityTypeProbability(person.getAge(), person.getGender());
            if (SiloUtil.getRandomNumberAsDouble() < disabilityTypeProb){
                person.setDisability(Disability.physical);
            } else {
                person.setDisability(Disability.mental);
            }
        }
    }


    private void updateSummary(Person person){
        String column = "";
        if (person.getDisability().equals(Disability.physical)){
            column = "physical";
        } else if (person.getDisability().equals(Disability.mental)){
            column = "mental";
        } else if (person.getDisability().equals(Disability.without)){
            column = "without";
        }
        int row = person.getAge();
        row = Math.min(row, 100);
        row = Math.max(1, row);
        //logger.info(person.getAge() + " and row " + row);
        float count = counts.getValueAt(row,column);
        counts.setValueAt(row, column, count + 1);
    }

}
