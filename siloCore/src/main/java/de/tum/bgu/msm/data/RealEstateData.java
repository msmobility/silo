package de.tum.bgu.msm.data;

import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.simulator.UpdateListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RealEstateData extends UpdateListener {

    Map<Integer, Float> getRentPaymentsForIncomeGroup(IncomeCategory incomeCategory);

    int getNextDwellingId();

    double[] getInitialQualShares();

    double[] getCurrentQualShares();

    float getMedianRent(int msa);

    int getNumberOfDDinRegion(int region);

    int[] getListOfVacantDwellingsInRegion(int region);

    int getNumberOfVacantDDinRegion(int region);

    List<DwellingType> getDwellingTypes();

    Dwelling getDwelling(int dwellingId);

    Collection<Dwelling> getDwellings();

    void removeDwelling(int id);

    void addDwelling(Dwelling dwelling);

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
