package de.tum.bgu.msm.properties.modules;

import com.pb.common.util.ResourceUtil;

import java.util.ResourceBundle;

public class MovesProperties {
    public final String uecFileName;
    public final int dataSheet;
    public final boolean logHhRelocation;
    public final boolean logHhRelocationRegion;
    public final double racialRelevanceInZone;
    public final boolean provideLowIncomeSubsidy;
    public final int dwellingUtilSheet;
    public final int moveOrNotSheet;
    public final double moveOrNotSlope;
    public final double moveOrNotShift;
    public final int selectRegionModelSheet;
    public final int selectDwellingSheet;
    public final double selectDwellingParameter;
    public final String populationControlTotal;
    public final String populationCOntrolTotalFile;
    public final String migrationFile;

    public MovesProperties(ResourceBundle bundle) {
        uecFileName =  ResourceUtil.getProperty(bundle, "HH.Moves.UEC.FileName");
        dataSheet = ResourceUtil.getIntegerProperty(bundle, "HH.Moves.UEC.DataSheetNumber");
        logHhRelocation = ResourceUtil.getBooleanProperty(bundle, "log.util.hhRelocation.dd");
        logHhRelocationRegion = ResourceUtil.getBooleanProperty(bundle, "log.util.hhRelocation.rg");
        racialRelevanceInZone = ResourceUtil.getDoubleProperty(bundle, "relevance.of.race.in.zone.of.dwelling");
        provideLowIncomeSubsidy = ResourceUtil.getBooleanProperty(bundle, "provide.housing.subsidy.to.low.inc", false);
        dwellingUtilSheet = ResourceUtil.getIntegerProperty(bundle, "HH.Moves.UEC.Dwelling.Utility");
        moveOrNotSheet = ResourceUtil.getIntegerProperty(bundle, "HH.Moves.UEC.ModelSheetNumber.moveOrNot");
        moveOrNotSlope = ResourceUtil.getDoubleProperty(bundle, "move.or.not.binomial.log.model.parameter");
        moveOrNotShift = ResourceUtil.getDoubleProperty(bundle, "move.or.not.binomial.log.shift.parameter");
        selectRegionModelSheet = ResourceUtil.getIntegerProperty(bundle, "HH.Moves.UEC.ModelSheetNumber.selectRegion");
        selectDwellingSheet = ResourceUtil.getIntegerProperty(bundle, "HH.Moves.UEC.ModelSheetNumber.selDwelling");
        selectDwellingParameter = ResourceUtil.getDoubleProperty(bundle, "select.dwelling.mn.log.model.parameter");
        populationControlTotal = ResourceUtil.getProperty(bundle, "population.control.total");
        populationCOntrolTotalFile = ResourceUtil.getProperty(bundle, "total.population.control.total.file");
        migrationFile = ResourceUtil.getProperty(bundle, "inmigration.outmigration.file");
    }
}
