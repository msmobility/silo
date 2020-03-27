package de.tum.bgu.msm.io.output;

import de.tum.bgu.msm.data.Zone;
import de.tum.bgu.msm.data.geo.GeoData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringJoiner;

public class DefaultZonesWriter implements ZonesWriter {

    private final GeoData geoData;

    public DefaultZonesWriter(GeoData geoData) {
        this.geoData = geoData;
    }

    @Override
    public void writeZones(String path) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write("Id,Area,Region");
            writer.newLine();
            for(Zone zone: geoData.getZones().values()) {
                StringJoiner joiner = new StringJoiner(",");
                joiner.add(String.valueOf(zone.getId())).add(String.valueOf(zone.getArea_sqmi()));
                writer.write(zone.getZoneId());
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
