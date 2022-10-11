package de.tum.bgu.msm.scenarios.ev;

import de.tum.bgu.msm.container.*;
import de.tum.bgu.msm.data.AreaTypes;
import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.geo.ZoneMuc;
import de.tum.bgu.msm.data.household.*;
import de.tum.bgu.msm.data.vehicle.*;
import de.tum.bgu.msm.models.*;
import de.tum.bgu.msm.models.carOwnership.*;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.*;
import org.apache.log4j.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class models households decisions of replacing one conventional car by one electric car.
 */
public class SwitchToElectricVehicleModelMuc extends AbstractModel implements ModelUpdateListener {

    private final static Logger logger = Logger.getLogger(SwitchToElectricVehicleModelMuc.class);


    /**
     * this variable stores a summary for print out purposes
     */
    private Map<String, Integer> summary = new HashMap<>();

    public SwitchToElectricVehicleModelMuc(DataContainer dataContainer, Properties properties, Random rnd) {
        super(dataContainer, properties, rnd);
    }

    @Override
    public void setup() {
    }

    @Override
    public void prepareYear(int year) {
        summary.clear();

    }

    @Override
    public void endYear(int year) {
        switchToEV(year);

    }

    @Override
    public void endSimulation() {

    }

    private void switchToEV(int year) {

        int event_counter = 0; // number of events change to AV
        int autos_counter = 0; //number of cars (all)
        int ev_counter = 0; //numbre of avs

        HouseholdDataManager householdDataManager = dataContainer.getHouseholdDataManager();

        // return HashMap<Household, ArrayOfHouseholdAttributes>. These are the households eligible for switching
        // to electric cars
        for (Household hh : householdDataManager.getHouseholds()) {
            int numberOfElectric = (int) hh.getVehicles().stream().
                    filter(vv -> vv.getType().equals(VehicleType.CAR)).
                    filter(vv -> ((Car) vv).getCarType().equals(CarType.ELECTRIC)).count();
            ev_counter += numberOfElectric;

            int numberOfAutosInThisHh = (int) hh.getVehicles().stream().
                    filter(vv -> vv.getType().equals(VehicleType.CAR)).count();
            autos_counter += numberOfAutosInThisHh;

            if (numberOfAutosInThisHh > numberOfElectric) {
                int income = HouseholdUtil.getAnnualHhIncome(hh);
                double utility = -1.5;
                if (numberOfAutosInThisHh == 1) {
                    utility += -0.510;
                }
                int dwellingId = hh.getDwellingId();
                Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(dwellingId);
                final DefaultDwellingTypes.DefaultDwellingTypeImpl type =
                        (DefaultDwellingTypes.DefaultDwellingTypeImpl) dwelling.getType();

                switch (type){
                    case SFD:
                        utility += 1.;
                        break;
                    case SFA:
                        utility += 1.;
                        break;
                    case MF234:
                        utility += 0.5;
                        break;
                    case MF5plus:
                        utility += 0.;
                        break;
                    case MH:
                        utility += 0.;
                        break;
                }

                int zoneId = dwelling.getZoneId();
                ZoneMuc zone = (ZoneMuc) (dataContainer.getGeoData().getZones().get(zoneId));
                /*switch (zone.getAreaTypeSG()) {
                    case CORE_CITY:
                    case MEDIUM_SIZED_CITY:
                        break;
                    case TOWN:
                        utility += 0.297;
                        break;
                    case RURAL:
                        utility += 0.208;
                        break;
                }*/

                int hhSize = hh.getHhSize();
                if (hhSize == 2) {
                    utility += -0.578;
                } else if (hhSize > 2) {
                    utility += -0.490;
                }

                int yearAtZero = 2027;
                final double beta = 5.;
                final double alpha = 0.5;
                final double gamma = 0.7;
                double yearDependentVariable = beta * (1. - 1./(1. + Math.exp(alpha * (year - yearAtZero)))) - beta * gamma;
                utility += yearDependentVariable;

                utility = Math.exp(utility);

                double prob = utility / (1 + utility);

                if (random.nextDouble() < prob) {
                    Vehicle vehicleToRemove = hh.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.CAR)).findAny().orElse(null);
                    if (vehicleToRemove != null){
                        for (Vehicle vehicle : hh.getVehicles().stream().filter(vv-> vv.getType().equals(VehicleType.CAR)).collect(Collectors.toSet())) {
                            if (vehicle.getAge() > vehicleToRemove.getAge()){
                                vehicleToRemove = vehicle;
                            }
                        }
                    }

                    boolean ok = hh.getVehicles().remove(vehicleToRemove);

                    if (!ok){
                        throw new RuntimeException();
                    }

                    hh.getVehicles().add(new Car(VehicleUtil.getHighestVehicleIdInHousehold(hh), CarType.ELECTRIC, VehicleUtil.getVehicleAgeWhenReplaced()));
                    event_counter++;
                }
            }
        }


        int hh = dataContainer.getHouseholdDataManager().getHouseholds().size(); // number of hh
        summary.put("hh", hh);
        summary.put("autos", autos_counter);
        summary.put("avs", ev_counter);
        summary.put("events", event_counter);


        logger.info(" Simulated household switched to EV " + event_counter + " (" +
                SiloUtil.rounder((100. * event_counter / hh), 0) + "% of hh)");
    }

    public Map<String, Integer> getSummaryForThisYear() {
        return summary;
    }
}
