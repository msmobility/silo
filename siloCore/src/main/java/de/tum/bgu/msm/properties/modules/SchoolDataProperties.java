package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.ResourceBundle;

public final class SchoolDataProperties {

    public final String schoolsFileName;


    public SchoolDataProperties(ResourceBundle bundle) {

        PropertiesUtil.newPropertySubmodule("School - synthetic schools input");
        schoolsFileName = PropertiesUtil.getStringProperty(bundle, "school.file.ascii", "microData/ss");

    }
}
