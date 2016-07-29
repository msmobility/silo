package edu.umd.ncsg.transportModel;

import com.pb.common.datafile.TableDataSet;
import edu.umd.ncsg.SiloUtil;
import org.apache.log4j.Logger;

import java.util.ResourceBundle;

/**
 * Stores data for the travel demand model
 * Author: Rolf Moeckel, Technical University of Munich
 * Created on 6 May 2016 in Munich, Germany
 **/

public class TravelDemandData {

    static Logger logger = Logger.getLogger(TravelDemandModel.class);
    protected static final String PROPERTIES_HH_TRAVEL_SURVEY_REG   = "household.travel.survey.reg";

    private ResourceBundle rb;


    public TravelDemandData(ResourceBundle rb) {
        this.rb = rb;
    }


    public void readData () {
        // read in general data for travel demand model

        TableDataSet regionDefinition = SiloUtil.readCSVfile(rb.getString(PROPERTIES_HH_TRAVEL_SURVEY_REG));
        regionDefinition.buildIndex(regionDefinition.getColumnPosition("SMZRMZ"));

    }
}
