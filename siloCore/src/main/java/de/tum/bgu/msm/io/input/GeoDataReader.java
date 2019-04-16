package de.tum.bgu.msm.io.input;

public interface GeoDataReader {

    /**
     *
     * @param path
     */
    void readZoneCsv(String path);

    /**
     *
     * @param path
     */
    void readZoneShapefile(String path);



}
