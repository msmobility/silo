/*
 * Copyright  2005 PB Consult Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package de.tum.bgu.msm.data.dwelling;

import de.tum.bgu.msm.data.MicroLocation;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.utils.objectattributes.attributable.Attributes;
import org.matsim.utils.objectattributes.attributable.AttributesImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Greg Erhardt
 * Created on Dec 2, 2009
 */
public final class DwellingImpl implements Dwelling, MicroLocation {

    private final Attributes attributes = new AttributesImpl();

    private final int id;

    private final int zoneId;
    private Coordinate coordinate;

    private final DwellingType type;
    private final int bedrooms;
    private final int yearBuilt;
    private int hhId;
    private int quality;
    private int price;

    //Attributes that could be additionally defined from the synthetic population. Remember to use "set"
    private int floorSpace = 0;
    private DwellingUsage usage = DwellingUsage.GROUP_QUARTER_OR_DEFAULT;

    DwellingImpl(int id, int zoneId, Coordinate coordinate,
                 int hhId, DwellingType type, int bedrooms,
                 int quality, int price, int year) {
        this.id = id;
        this.zoneId = zoneId;
        this.coordinate = coordinate;
        this.hhId = hhId;
        this.type = type;
        this.bedrooms = bedrooms;
        this.quality = quality;
        this.price = price;
        this.yearBuilt = year;
    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public int getZoneId() {
        return zoneId;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getQuality() {
        return quality;
    }

    @Override
    public int getResidentId() {
        return hhId;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public DwellingType getType() {
        return type;
    }

    @Override
    public int getBedrooms() {
        return bedrooms;
    }

    @Override
    public int getYearBuilt() {
        return yearBuilt;
    }

    @Override
    public void setResidentID(int residentID) {
        this.hhId = residentID;
    }

    @Override
    public void setQuality(int quality) {
        this.quality = quality;
    }

    @Override
    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public void setFloorSpace(int floorSpace) {
        this.floorSpace = floorSpace;
        //Usable square meters of the dwelling.
        //Numerical value from 1 to 9999
    }

    @Override
    public int getFloorSpace() {
        return floorSpace;
    }

    @Override
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    //TODO: use case specific
    @Override
    public void setUsage(DwellingUsage usage) {
        this.usage = usage;
    }

    //TODO: use case specific
    @Override
    public DwellingUsage getUsage() {
        return usage;
    }

    @Override
    public Optional<Object> getAttribute(String key) {
        return Optional.ofNullable(attributes.getAttribute(key));
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.putAttribute(key, value);
    }

    @Override
    public String toString() {
        return "Attributes of dwelling  " + id
                +"\nLocated in zone         "+ zoneId
                + "\nLocated at		        " + (coordinate.toString()) // TODO implement toString methods
                + "\nOccupied by household   " + (hhId)
                + "\nDwelling type           " + (type.toString())
                + "\nNumber of bedrooms      " + (bedrooms)
                + "\nQuality (1 low, 4 high) " + (quality)
                + "\nMonthly price in US$    " + (price)
                + "\nYear dwelling was built " + (yearBuilt);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Dwelling && ((Dwelling) o).getId() == this.id;
    }
}
