package de.tum.bgu.msm.data.development;

import de.tum.bgu.msm.data.dwelling.DwellingType;

import java.util.Map;


public class DevelopmentImpl implements Development {

    private double developableArea;
    private int dwellingCapacity;
    private final Map<DwellingType, Boolean> developmentConstraints;
    private final boolean useDwellingCapacity;

    public DevelopmentImpl(double developableArea, int dwellingCapacity, Map<DwellingType, Boolean> developmentConstraints, boolean useDwellingCapacity) {
        this.developableArea = developableArea;
        this.dwellingCapacity = dwellingCapacity;
        this.developmentConstraints = developmentConstraints;
        this.useDwellingCapacity = useDwellingCapacity;
    }


    @Override
    public double getDevelopableArea() {
        return developableArea;
    }

    @Override
    public int getDwellingCapacity() {
        return dwellingCapacity;
    }

    @Override
    public boolean isThisDwellingTypeAllowed(DwellingType dwellingType) {
        return developmentConstraints.get(dwellingType);
    }

    @Override
    public void changeAreaBy(double area){
        developableArea = Math.max(0, developableArea + area);
    }

    @Override
    public void changeCapacityBy(int dwellings){
        dwellingCapacity = Math.max(0, dwellingCapacity + dwellings);
    }

    @Override
    public boolean isUseDwellingCapacity() {
        return useDwellingCapacity;
    }

}
