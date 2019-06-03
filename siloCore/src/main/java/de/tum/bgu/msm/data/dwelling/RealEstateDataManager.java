package de.tum.bgu.msm.data.dwelling;

import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.models.ModelUpdateListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RealEstateDataManager extends ModelUpdateListener {

    DwellingFactory getDwellingFactory();

    Map<Integer, Float> getRentPaymentsForIncomeGroup(IncomeCategory incomeCategory);

    int getNextDwellingId();

    double[] getInitialQualShares();

    double[] getCurrentQualShares();

    List<Dwelling> getListOfVacantDwellingsInRegion(int region);

    int getNumberOfVacantDDinRegion(int region);

    List<DwellingType> getDwellingTypes();

    Dwelling getDwelling(int dwellingId);

    Collection<Dwelling> getDwellings();

    void removeDwelling(int id);

    void addDwelling(Dwelling dwelling);

    int[] getDwellingsByQuality();

    Map<Integer, Double> calculateRegionalPrices();

    void removeDwellingFromVacancyList(int ddId);

    void addDwellingToVacancyList(Dwelling dd);

    double[][] getVacancyRateByTypeAndRegion();

    void setAvePriceByDwellingType(double[] newAvePrice);

    double[] getAveragePriceByDwellingType();

    double[] getAverageVacancyByDwellingType();

    int[][] getDwellingCountByTypeAndRegion();

    double getAvailableCapacityForConstruction(int zone);

    void convertLand(int zone, float acres);

    void vacateDwelling(int idOldDD);
}
