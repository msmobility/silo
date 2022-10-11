package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.ResourceBundle;

public final class MovesProperties {

    public final double racialRelevanceInZone;
    public final boolean provideLowIncomeSubsidy;


    public final PopulationControlTotalMethod populationControlTotal;
    public final String populationCOntrolTotalFile;
    public final String migrationFile;
    public final double populationGrowthRateInPercentage;

    public final boolean trackRelocations;

    public final float B_TIME;
    public final float B_PT;
    public final float B_EXP_HOUSING_UTILITY;

    public enum PopulationControlTotalMethod {
        POPULATION, MIGRATION, RATE
    }

    public MovesProperties(ResourceBundle bundle) {
        PropertiesUtil.newPropertySubmodule("Relocation properties - control of population");
        populationControlTotal = PopulationControlTotalMethod.valueOf(
                PropertiesUtil.getStringProperty(bundle, "population.control.total", "rate").toUpperCase());
        populationCOntrolTotalFile = PropertiesUtil.getStringProperty(bundle, "total.population.control.total.file", "input/assumptions/populationControlTotal.csv");
        migrationFile = PropertiesUtil.getStringProperty(bundle, "inmigration.outmigration.file", "input/assumptions/inOutMigration.csv");
        populationGrowthRateInPercentage = PropertiesUtil.getDoubleProperty(bundle, "population.growth.rate", 0. );

        PropertiesUtil.newPropertySubmodule("Relocation properties - social security parameters MSTM");
        racialRelevanceInZone = PropertiesUtil.getDoubleProperty(bundle, "relevance.of.race.in.zone.of.dwelling", 0.5);
        provideLowIncomeSubsidy = PropertiesUtil.getBooleanProperty(bundle, "provide.housing.subsidy.to.low.inc", false);

        trackRelocations = PropertiesUtil.getBooleanProperty(bundle, "track.relocations", true);

        B_TIME = (float) PropertiesUtil.getDoubleProperty(bundle, "mode.choice.impedance.coefficient", 10f);
        B_PT = (float) PropertiesUtil.getDoubleProperty(bundle, "mode.choice.pt.asc", 0f);
        B_EXP_HOUSING_UTILITY = (float) PropertiesUtil.getDoubleProperty(bundle, "mc.housing.time.coefficient.ratio", 20f);

    }
}
