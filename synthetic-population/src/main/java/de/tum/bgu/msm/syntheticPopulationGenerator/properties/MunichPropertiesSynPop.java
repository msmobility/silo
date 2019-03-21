package de.tum.bgu.msm.syntheticPopulationGenerator.properties;

import com.pb.common.datafile.TableDataSet;
import com.pb.common.util.ResourceUtil;
import de.tum.bgu.msm.properties.PropertiesUtil;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.commons.math.distribution.GammaDistributionImpl;

import java.util.ResourceBundle;

public class MunichPropertiesSynPop extends AbstractPropertiesSynPop {

    public MunichPropertiesSynPop(ResourceBundle bundle) {

        PropertiesUtil.newPropertySubmodule("SP: main properties");

        runMicrolocation = PropertiesUtil.getBooleanProperty(bundle, "run.sp.microlocation", false);

        //todo I would read these attributes from a file, probable, the same as read in the next property

        // todo this table is not a property but a data container, "ID_city" might be a property? (if this is applciable to other implementations)

        marginalsMunicipality.buildIndex(marginalsMunicipality.getColumnPosition("ID_city"));


        //todo same as municipalities
        marginalsCounty.buildIndex(marginalsCounty.getColumnPosition("ID_county"));

        selectedMunicipalities.buildIndex(selectedMunicipalities.getColumnPosition("ID_city"));

        cellsMatrix.buildIndex(cellsMatrix.getColumnPosition("ID_cell"));

        //todo this cannot be the final name of the matrix

        double incomeShape = PropertiesUtil.getDoubleProperty(bundle, "income.gamma.shape", 1.0737036186);
        double incomeRate = PropertiesUtil.getDoubleProperty(bundle, "income.gamma.rate", 0.0006869439);
        //todo consider to read it from another source e.g. a JS calculator or CSV file
        //this is not a property but a variable?
        incomeGammaDistribution = new GammaDistributionImpl(incomeShape, 1 / incomeRate);

        //todo this properties will be doubled with silo model run properties

        //todo do not need to ride always?
        if (runMicrolocation) {
            buildingLocationlist = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "buildingLocation.list", "input/syntheticPopulation/buildingLocation.csv"));
            jobLocationlist = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "jobLocation.list", "input/syntheticPopulation/jobLocation.csv"));
            schoolLocationlist = SiloUtil.readCSVfile(PropertiesUtil.getStringProperty(bundle, "schoolLocation.list", "input/syntheticPopulation/schoolLocation.csv"));
        } else {
            buildingLocationlist = null;
            jobLocationlist = null;
            schoolLocationlist = null;
        }
        zonalDataIPU = null;
    }

}
