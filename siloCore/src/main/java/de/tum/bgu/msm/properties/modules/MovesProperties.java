package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.models.relocation.InOutMigration;
import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.ResourceBundle;

public class MovesProperties {

    public final double racialRelevanceInZone;
    public final boolean provideLowIncomeSubsidy;


    public final String populationControlTotal;
    public final String populationCOntrolTotalFile;
    public final String migrationFile;
    public final double populationGrowthRateInPercentage;

    public MovesProperties(ResourceBundle bundle) {
        PropertiesUtil.newPropertySubmodule("Relocation properties - control of population");
        populationControlTotal = PropertiesUtil.getStringProperty(bundle, "population.control.total", "population");
        populationCOntrolTotalFile = PropertiesUtil.getStringProperty(bundle, "total.population.control.total.file", "input/assumptions/populationControlTotal.csv");
        migrationFile = PropertiesUtil.getStringProperty(bundle, "inmigration.outmigration.file", "input/assumptions/inOutMigration.csv");
        populationGrowthRateInPercentage = PropertiesUtil.getDoubleProperty(bundle, "population.growth.rate", 0. );

        PropertiesUtil.newPropertySubmodule("Relocation properties - social security parameters MSTM");
        racialRelevanceInZone = PropertiesUtil.getDoubleProperty(bundle, "relevance.of.race.in.zone.of.dwelling", 0.8);
        provideLowIncomeSubsidy = PropertiesUtil.getBooleanProperty(bundle, "provide.housing.subsidy.to.low.inc", false);


    }
}
