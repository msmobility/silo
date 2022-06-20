package de.tum.bgu.msm.scenarios.decisionTreeRelocation.models.relocation;

import de.tum.bgu.msm.container.DataContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.events.impls.household.MoveEvent;
import de.tum.bgu.msm.models.AbstractModel;
import de.tum.bgu.msm.models.relocation.moves.HousingStrategy;
import de.tum.bgu.msm.models.relocation.moves.MovesModel;
import de.tum.bgu.msm.models.relocation.moves.MovesStrategy;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class DecissionTreeRelocationModel extends AbstractModel implements MovesModel {

    private final Logger logger = Logger.getLogger(DecissionTreeRelocationModel.class);


    public DecissionTreeRelocationModel(DataContainer dataContainer, Properties properties, Random random) {
        super(dataContainer, properties, random);

    }



    @Override
    public Collection<MoveEvent> getEventsForCurrentYear(int year) {
        final List<MoveEvent> events = new ArrayList<>();
        for (Household hh : dataContainer.getHouseholdDataManager().getHouseholds()) {
            events.add(new MoveEvent(hh.getId()));
        }
        return events;
    }


    @Override
    public int searchForNewDwelling(Household household) {

        //find a new dwelling for household, return a dwelling id of the selected household

        //example: choose a random vacant dwelling in the current household region
        final int dwellingId = household.getDwellingId();
        final Dwelling dwelling = dataContainer.getRealEstateDataManager().getDwelling(dwellingId);
        final int zoneId;
        final int regionId;
        if (dwelling == null){
            //this household does not have a dwelling currently, so a random region is chosen
            final Set<Integer> regions = dataContainer.getGeoData().getRegions().keySet();
            Map<Integer, Double> regionProbability = new HashMap<>();
            for (int r : regions){
                regionProbability.put(r, 1.0);
            }
            regionId = SiloUtil.select(regionProbability, random);
        } else {
            zoneId = dwelling.getZoneId();
            regionId = dataContainer.getGeoData().getZones().get(zoneId).getRegion().getId();
        }
        Map<Integer, Double> dwellings = new HashMap<>();
        for (Dwelling dd : dataContainer.getRealEstateDataManager().getListOfVacantDwellingsInRegion(regionId)) {
            dwellings.put(dd.getId(), 1.0);
        }
        if (dwellings.size() == 0){
            return -1;
        } else {
            int newDwellingId = SiloUtil.select(dwellings, random);
            return newDwellingId;
        }

    }


    @Override
    public boolean handleEvent(MoveEvent event) {
        // Simulates (a) if this household moves and (b) where this household moves
        int hhId = event.getHouseholdId();
        Household household = dataContainer.getHouseholdDataManager().getHouseholdFromId(hhId);
        if (household == null) {
            // Household does not exist anymore
            return false;
        }

        boolean willMove = random.nextDouble() < 0.1 ? true : false;

        // Step 1: Consider relocation if household is not very satisfied or if
        // household income exceed restriction for low-income dwelling
        if (!willMove) {
            return false;
        }

        final int idOldDd = household.getDwellingId();
        // Step 2: Choose new dwelling
        int idNewDD = searchForNewDwelling(household);

        if (idNewDD > 0) {

            // Step 3: Move household
            dataContainer.getHouseholdDataManager().saveHouseholdMemento(household);
            moveHousehold(household, idOldDd, idNewDD);
            if (hhId == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Household " + hhId + " has moved to newDwelling " +
                        idOldDd);
            }
            return true;
        } else {
            if (hhId == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Household " + hhId + " intended to move but " +
                        "could not find an adequate dwelling.");
            }
            return false;
        }
    }

    @Override
    public void moveHousehold(Household hh, int idOldDD, int idNewDD) {
        //move a hh from idOldDD to a idNewDD
        // if this household had a dwelling in this study area before, vacate old dwelling
        if (idOldDD > 0) {
            dataContainer.getRealEstateDataManager().vacateDwelling(idOldDD);
        }
        try {
            dataContainer.getRealEstateDataManager().removeDwellingFromVacancyList(idNewDD);
        } catch (NullPointerException e){
            logger.warn("eh");
        }
        dataContainer.getRealEstateDataManager().getDwelling(idNewDD).setResidentID(hh.getId());
        hh.setDwelling(idNewDD);
        if (hh.getId() == SiloUtil.trackHh) {
            SiloUtil.trackWriter.println("Household " +
                    hh.getId() + " moved from dwelling " + idOldDD + " to dwelling " + idNewDD + ".");
        }
    }

    @Override
    public void setup() {
        //to be done at start of the silo run, e.g. load data of hh, dd, pp, etc.
    }

    @Override
    public void prepareYear(int year) {
        // to be done at the start of the simulated year: e.g. check who wants to move
    }

    @Override
    public void endYear(int year) {
        //e.g. print out intermediate result

    }

    @Override
    public void endSimulation() {
        //e.g. final documentation or summaries
    }
}
