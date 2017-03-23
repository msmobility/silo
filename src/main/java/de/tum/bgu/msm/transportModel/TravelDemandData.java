package de.tum.bgu.msm.transportModel;

import com.pb.common.datafile.TableDataSet;
import de.tum.bgu.msm.SiloUtil;
import org.apache.log4j.Logger;

import java.util.ResourceBundle;

/**
 * Stores data for the travel demand model
 * Author: Rolf Moeckel, Technical University of Munich
 * Created on 6 May 2016 in Munich, Germany
 **/

public class TravelDemandData {

    static Logger logger = Logger.getLogger(TravelDemandData.class);
    protected static final String PROPERTIES_SURVEY_REGIONS         = "household.travel.survey.reg";

    private TableDataSet regionDefinition;
    private ResourceBundle rb;


    public TravelDemandData(ResourceBundle rb) {
        this.rb = rb;
    }


    public void readData () {
        // read in general data for travel demand model

        regionDefinition = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SURVEY_REGIONS));
        regionDefinition.buildIndex(regionDefinition.getColumnPosition("Zone"));

    }


    public int getRegionOfZone (int zone) {
        return (int) regionDefinition.getIndexedValueAt(zone, "Region");
    }


    public int translateIncomeIntoCategory (int hhIncome) {
        // translate income in absolute dollars into household travel survey income categories

        if (hhIncome < 10000) return 1;
        else if (hhIncome >= 10000 && hhIncome < 15000) return 2;
        else if (hhIncome >= 15000 && hhIncome < 30000) return 3;
        else if (hhIncome >= 30000 && hhIncome < 40000) return 4;
        else if (hhIncome >= 40000 && hhIncome < 50000) return 5;
        else if (hhIncome >= 50000 && hhIncome < 60000) return 6;
        else if (hhIncome >= 60000 && hhIncome < 75000) return 7;
        else if (hhIncome >= 75000 && hhIncome < 100000) return 8;
        else if (hhIncome >= 100000 && hhIncome < 125000) return 9;
        else if (hhIncome >= 125000 && hhIncome < 150000) return 10;
        else if (hhIncome >= 150000 && hhIncome < 200000) return 11;
        else if (hhIncome >= 200000) return 12;
        logger.error("Unknown HTS income: " + hhIncome);
        return -1;
    }

}
