package de.tum.bgu.msm.SyntheticPopulationGenerator;

import com.pb.common.calculator.IndexValues;
import de.tum.bgu.msm.autoOwnership.UpdateCarOwnershipDMU;
import org.apache.log4j.Logger;

/**
 * Created by Shihang on 7/18/2017.
 */
public class TeleworkDMU {
    protected transient Logger logger = Logger.getLogger(TeleworkDMU.class);

    // uec variables
    private IndexValues dmuIndex;
    private int age;
    private int gender;
    private int nationality;
    private int hasElderlyPerson;
    private int hhStructure;
    private int newEducationLevel;
    private int hhIncomeLevel;


    public TeleworkDMU(){
        dmuIndex = new IndexValues();
    }

    public IndexValues getDmuIndexValues(){
        return dmuIndex;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public void setGender(int gender){
        this.gender = gender;
    }
    public void setNationality(int nationality){
        this.nationality = nationality;
    }
    public void setHasElderlyPerson(int hasElderlyPerson){
        this.hasElderlyPerson = hasElderlyPerson;
    }
    public void setHhStructure(int hhStructure){
        this.hhStructure = hhStructure;
    }
    public void setNewEducationLevel(int newEducationLevel){
        this.newEducationLevel = newEducationLevel;
    }
    public void setHhIncomeLevel(int hhIncomeLevel){
        this.hhIncomeLevel = hhIncomeLevel;
    }

    // DMU methods - define one of these for every @var in the mode choice control file.
    public int getAge() {
        return age;
    }
    public int getGender(){
        return gender;
    }
    public int getNationality(){
        return nationality;
    }
    public int getHasElderlyPerson(){
        return hasElderlyPerson;
    }
    public int getHhStructure(){
        return hhStructure;
    }
    public int getNewEducationLevel(){
        return newEducationLevel;
    }
    public int getHhIncomeLevel(){
        return hhIncomeLevel;
    }

}
