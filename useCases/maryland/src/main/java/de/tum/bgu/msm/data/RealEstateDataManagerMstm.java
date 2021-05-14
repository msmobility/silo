package de.tum.bgu.msm.data;

import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.geo.GeoData;
import de.tum.bgu.msm.data.geo.MstmZone;
import de.tum.bgu.msm.data.household.HouseholdData;
import de.tum.bgu.msm.data.household.IncomeCategory;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;

import java.util.*;

public class RealEstateDataManagerMstm implements RealEstateDataManager {

    private final RealEstateDataManager delegate;
    private final GeoData geoData;
    private final Properties properties;

    private float[] medianRent;

    public RealEstateDataManagerMstm (
            DwellingFactory dwellingFactory, DwellingData dwellingData,
            DwellingTypes dwellingTypes, HouseholdData householdData,
            GeoData geoData, Properties properties) {
        delegate = new RealEstateDataManagerImpl(
                dwellingTypes, dwellingData,
                householdData, geoData, dwellingFactory, properties);
        this.geoData = geoData;
        this.properties = properties;
    }

    private void calculateMedianRentByMSA() {
        Map<Integer, ArrayList<Integer>> rentHashMap = new HashMap<>();
        for (Dwelling dd : delegate.getDwellings()) {
            int dwellingMSA = ((MstmZone) geoData.getZones().get(dd.getZoneId())).getMsa();
            if (rentHashMap.containsKey(dwellingMSA)) {
                ArrayList<Integer> rents = rentHashMap.get(dwellingMSA);
                rents.add(dd.getPrice());
            } else {
                ArrayList<Integer> rents = new ArrayList<>();
                rents.add(dd.getPrice());
                rentHashMap.put(dwellingMSA, rents);
            }
        }
        medianRent = new float[99999];
        for (Integer thisMsa : rentHashMap.keySet()) {
            medianRent[thisMsa] = SiloUtil.getMedian(rentHashMap.get(thisMsa).stream().mapToInt(Integer::intValue).toArray());
        }
    }

    public float getMedianRent(int msa) {
        return medianRent[msa];
    }

    @Override
    public void setup() {
        delegate.setup();
        if (properties.moves.provideLowIncomeSubsidy) {
            calculateMedianRentByMSA();
        }
    }

    @Override
    public DwellingFactory getDwellingFactory() {
        return delegate.getDwellingFactory();
    }

    @Override
    public DwellingData getDwellingData() {
        return delegate.getDwellingData();
    }

    @Override
    public Map<Integer, Float> getRentPaymentsForIncomeGroup(IncomeCategory incomeCategory) {
        return delegate.getRentPaymentsForIncomeGroup(incomeCategory);
    }

    @Override
    public int getNextDwellingId() {
        return delegate.getNextDwellingId();
    }

    @Override
    public Map<Integer, Double> getInitialQualShares() {
        return delegate.getInitialQualShares();
    }

    @Override
    public Map<Integer, Double> getUpdatedQualityShares() {
        return delegate.getUpdatedQualityShares();
    }


    @Override
    public List<Dwelling> getListOfVacantDwellingsInRegion(int region) {
        return delegate.getListOfVacantDwellingsInRegion(region);
    }

    @Override
    public int getNumberOfVacantDDinRegion(int region) {
        return delegate.getNumberOfVacantDDinRegion(region);
    }

    @Override
    public DwellingTypes getDwellingTypes() {
        return delegate.getDwellingTypes();
    }

    @Override
    public Dwelling getDwelling(int dwellingId) {
        return delegate.getDwelling(dwellingId);
    }

    @Override
    public Collection<Dwelling> getDwellings() {
        return delegate.getDwellings();
    }

    @Override
    public void removeDwelling(int id) {
        delegate.removeDwelling(id);
    }

    @Override
    public void addDwelling(Dwelling dwelling) {
        delegate.addDwelling(dwelling);
    }


    @Override
    public Map<Integer, Double> calculateRegionalPrices() {
        return delegate.calculateRegionalPrices();
    }

    @Override
    public void removeDwellingFromVacancyList(int ddId) {
        delegate.removeDwellingFromVacancyList(ddId);
    }

    @Override
    public void addDwellingToVacancyList(Dwelling dd) {
        delegate.addDwellingToVacancyList(dd);
    }

    @Override
    public double[][] getVacancyRateByTypeAndRegion() {
        return delegate.getVacancyRateByTypeAndRegion();
    }

    @Override
    public void setAvePriceByDwellingType(double[] newAvePrice) {
        delegate.setAvePriceByDwellingType(newAvePrice);
    }

    @Override
    public double[] getAveragePriceByDwellingType() {
        return delegate.getAveragePriceByDwellingType();
    }

    @Override
    public double[] getAverageVacancyByDwellingType() {
        return delegate.getAverageVacancyByDwellingType();
    }

    @Override
    public int[][] getDwellingCountByTypeAndRegion() {
        return delegate.getDwellingCountByTypeAndRegion();
    }

    @Override
    public double getAvailableCapacityForConstruction(int zone) {
        return delegate.getAvailableCapacityForConstruction(zone);
    }

    @Override
    public void convertLand(int zone, float acres) {
        delegate.convertLand(zone, acres);
    }

    @Override
    public void vacateDwelling(int idOldDD) {
        delegate.vacateDwelling(idOldDD);
    }

    @Override
    public void prepareYear(int year) {
        delegate.prepareYear(year);
    }

    @Override
    public void endYear(int year) {
        delegate.endYear(year);
    }

    @Override
    public void endSimulation() {
        delegate.endSimulation();
    }
}
