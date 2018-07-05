package de.tum.bgu.msm.properties.modules;

import de.tum.bgu.msm.properties.PropertiesUtil;

import java.util.ResourceBundle;

public class MovesProperties {

    public final double racialRelevanceInZone;
    public final boolean provideLowIncomeSubsidy;


    public final String populationControlTotal;
    public final String populationCOntrolTotalFile;
    public final String migrationFile;

    public MovesProperties(ResourceBundle bundle) {
        PropertiesUtil.printOutModuleTitle("Relocation properties - control of population");
        populationControlTotal = PropertiesUtil.getStringProperty(bundle, "population.control.total", "population");
        populationCOntrolTotalFile = PropertiesUtil.getStringProperty(bundle, "total.population.control.total.file", "input/assumptions/populationControlTotal.csv");
        migrationFile = PropertiesUtil.getStringProperty(bundle, "inmigration.outmigration.file", "input/assumptions/inOutMigration.csv");
//        uecFileName =  PropertiesUtil.getStringProperty(bundle, "HH.Moves.UEC.FileName");
//        dataSheet = PropertiesUtil.getIntProperty(bundle, "HH.Moves.UEC.DataSheetNumber");
//        logHhRelocation = PropertiesUtil.getBooleanProperty(bundle, "log.util.hhRelocation.dd");
//        logHhRelocationRegion = PropertiesUtil.getBooleanProperty(bundle, "log.util.hhRelocation.rg");
        PropertiesUtil.printOutModuleTitle("Relocation properties - social parameters MSTM");
        //todo has no default
        racialRelevanceInZone = PropertiesUtil.getDoubleProperty(bundle, "relevance.of.race.in.zone.of.dwelling", 0.8);
        provideLowIncomeSubsidy = PropertiesUtil.getBooleanProperty(bundle, "provide.housing.subsidy.to.low.inc", false);
//        dwellingUtilSheet = PropertiesUtil.getIntProperty(bundle, "HH.Moves.UEC.Dwelling.Utility");
//        moveOrNotSheet = PropertiesUtil.getIntProperty(bundle, "HH.Moves.UEC.ModelSheetNumber.moveOrNot");
//        moveOrNotSlope = PropertiesUtil.getDoubleProperty(bundle, "move.or.not.binomial.log.model.parameter");
//        moveOrNotShift = PropertiesUtil.getDoubleProperty(bundle, "move.or.not.binomial.log.shift.parameter");
//        selectRegionModelSheet = PropertiesUtil.getIntProperty(bundle, "HH.Moves.UEC.ModelSheetNumber.selectRegion");
//        selectDwellingSheet = PropertiesUtil.getIntProperty(bundle, "HH.Moves.UEC.ModelSheetNumber.selDwelling");
//        selectDwellingParameter = PropertiesUtil.getDoubleProperty(bundle, "select.dwelling.mn.log.model.parameter");

    }
}
