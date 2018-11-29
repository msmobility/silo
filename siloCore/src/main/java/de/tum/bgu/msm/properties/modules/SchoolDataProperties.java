package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.ResourceBundle;

public final class SchoolDataProperties {

    public final String schoolsFileName;
    public final String schoolsFinalFileName;
    public final String schoolsShapeFile;


    public SchoolDataProperties(ResourceBundle bundle) {

        PropertiesUtil.newPropertySubmodule("School - synthetic schools input");
        schoolsFileName = PropertiesUtil.getStringProperty(bundle, "school.file.ascii", "microData/ss");
        schoolsFinalFileName = PropertiesUtil.getStringProperty(bundle, "school.final.file.ascii", "microData/futureYears/ss");
        schoolsShapeFile = PropertiesUtil.getStringProperty(bundle, "school.shapefile", "input/schoolShapefile/schools.shp");
    }
}
