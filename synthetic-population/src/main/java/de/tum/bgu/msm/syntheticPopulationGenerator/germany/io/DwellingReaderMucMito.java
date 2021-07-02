package de.tum.bgu.msm.syntheticPopulationGenerator.germany.io;

import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.data.dwelling.*;
import de.tum.bgu.msm.data.household.HouseholdDataManager;
import de.tum.bgu.msm.io.input.DwellingReader;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DwellingReaderMucMito implements DwellingReader {

    private final static Logger logger = Logger.getLogger(DwellingReaderMucMito.class);
    private final RealEstateDataManager dwellingData;

    public DwellingReaderMucMito(RealEstateDataManager dwellingData) {
        this.dwellingData= dwellingData;
    }

    @Override
    public void readData(String path) {
        DwellingFactory factory = DwellingUtils.getFactory();
        logger.info("Reading dwelling micro data from ascii file");
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId      = SiloUtil.findPositionInArray("id", header);
            int posZone    = SiloUtil.findPositionInArray("zone",header);
            int posHh      = SiloUtil.findPositionInArray("hhId",header);
            int posCoordX = -1;
            int posCoordY = -1;
            try {
                posCoordX = SiloUtil.findPositionInArray("coordX", header);
                posCoordY = SiloUtil.findPositionInArray("coordY", header);
            } catch (Exception e) {
                logger.warn("No coords given in dwelling input file. Models using microlocations will not work.");
            }

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id        = Integer.parseInt(lineElements[posId]);
                int zoneId      = Integer.parseInt(lineElements[posZone]);
                int hhId      = Integer.parseInt(lineElements[posHh]);
                Coordinate coordinate = null;
                if (posCoordX >= 0 && posCoordY >= 0) {
                    try {
                        coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                    } catch (Exception e) {
                    }
                }
                Dwelling dd = DwellingUtils.getFactory().createDwelling(id, zoneId, coordinate, hhId,
                        DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234, 50, 4, 600, 2000);   // this automatically puts it in id->dwelling map in Dwelling class
                dwellingData.addDwelling(dd);
                if (id == SiloUtil.trackDd) {
                    SiloUtil.trackWriter.println("Read dwelling with following attributes from " + path);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop dwelling file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " dwellings.");
    }

    public int readDataWithStateAndReassignIds(String path, int finalDdIdPreviousState, int finalHhIdPreviousState, boolean generate, String state) {
        DwellingFactory factory = DwellingUtils.getFactory();
        logger.info("Reading dwelling micro data from ascii file");
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId      = SiloUtil.findPositionInArray("id", header);
            int posZone    = SiloUtil.findPositionInArray("zone",header);
            int posHh      = SiloUtil.findPositionInArray("hhId",header);
            int posCoordX = -1;
            int posCoordY = -1;
            try {
                posCoordX = SiloUtil.findPositionInArray("coordX", header);
                posCoordY = SiloUtil.findPositionInArray("coordY", header);
            } catch (Exception e) {
                logger.warn("No coords given in dwelling input file. Models using microlocations will not work.");
            }

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                if (generate) {
                    String[] lineElements = recString.split(",");
                    int id = Integer.parseInt(lineElements[posId]);
                    int zoneId = Integer.parseInt(lineElements[posZone]);
                    int hhId = Integer.parseInt(lineElements[posHh]);
                    Coordinate coordinate = null;
                    if (posCoordX >= 0 && posCoordY >= 0) {
                        try {
                            coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                        } catch (Exception e) {
                        }
                    }
                    int correlativeId = id + finalDdIdPreviousState;
                    int correlativeIdHh = id + finalHhIdPreviousState;
                    Dwelling dd = DwellingUtils.getFactory().createDwelling(correlativeId, zoneId, coordinate, correlativeIdHh,
                            DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234, 50, 4, 600, 2000);   // this automatically puts it in id->dwelling map in Dwelling class
                    dwellingData.addDwelling(dd);
                    dd.setAttribute("originalId", id);
                    if (id == SiloUtil.trackDd) {
                        SiloUtil.trackWriter.println("Read dwelling with following attributes from " + path);
                    }
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop dwelling file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " dwellings.");
        return recCount;
    }

    public int readDataWithState(String path, boolean hasState) {
        DwellingFactory factory = DwellingUtils.getFactory();
        logger.info("Reading dwelling micro data from ascii file");
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId      = SiloUtil.findPositionInArray("id", header);
            int posZone    = SiloUtil.findPositionInArray("zone",header);
            int posHh      = SiloUtil.findPositionInArray("hhId",header);
            int posCoordX = -1;
            int posCoordY = -1;
            try {
                posCoordX = SiloUtil.findPositionInArray("coordX", header);
                posCoordY = SiloUtil.findPositionInArray("coordY", header);
            } catch (Exception e) {
                logger.warn("No coords given in dwelling input file. Models using microlocations will not work.");
            }
            int posState = 0;
            int posOriginalId = 0;
            if (hasState){
                posState = SiloUtil.findPositionInArray("state", header);
                posOriginalId = SiloUtil.findPositionInArray("originalId", header);
            }
            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                    String[] lineElements = recString.split(",");
                    int id = Integer.parseInt(lineElements[posId]);
                    int zoneId = Integer.parseInt(lineElements[posZone]);
                    int hhId = Integer.parseInt(lineElements[posHh]);
                    Coordinate coordinate = null;
                    if (posCoordX >= 0 && posCoordY >= 0) {
                        try {
                            coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                        } catch (Exception e) {
                        }
                    }

                    Dwelling dd = DwellingUtils.getFactory().createDwelling(id, zoneId, coordinate, hhId,
                            DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234, 50, 4, 600, 2000);   // this automatically puts it in id->dwelling map in Dwelling class
                    dwellingData.addDwelling(dd);
                if (hasState) {
                    int originalId = Integer.parseInt(lineElements[posOriginalId]);
                    String state = (lineElements[posState]);
                    dd.setAttribute("state", state);
                    dd.setAttribute("originalId", originalId);
                } else {
                    dd.setAttribute("state", "");
                    dd.setAttribute("originalId", id);
                }
                    if (id == SiloUtil.trackDd) {
                        SiloUtil.trackWriter.println("Read dwelling with following attributes from " + path);
                    }

            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop dwelling file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " dwellings.");
        return recCount;
    }


    public int readDataWithStateSave(String path, int finalDdIdPreviousState, int finalHhIdPreviousState, boolean save) {
        DwellingFactory factory = DwellingUtils.getFactory();
        logger.info("Reading dwelling micro data from ascii file");
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId      = SiloUtil.findPositionInArray("id", header);
            int posZone    = SiloUtil.findPositionInArray("zone",header);
            int posHh      = SiloUtil.findPositionInArray("hhId",header);
            int posCoordX = -1;
            int posCoordY = -1;
            try {
                posCoordX = SiloUtil.findPositionInArray("coordX", header);
                posCoordY = SiloUtil.findPositionInArray("coordY", header);
            } catch (Exception e) {
                logger.warn("No coords given in dwelling input file. Models using microlocations will not work.");
            }

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id        = Integer.parseInt(lineElements[posId]);
                int zoneId      = Integer.parseInt(lineElements[posZone]);
                int hhId      = Integer.parseInt(lineElements[posHh]);
                Coordinate coordinate = null;
                if (posCoordX >= 0 && posCoordY >= 0) {
                    try {
                        coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                    } catch (Exception e) {
                    }
                }
                int correlativeId = id + finalDdIdPreviousState;
                int correlativeIdHh = id + finalHhIdPreviousState;
                Dwelling dd = DwellingUtils.getFactory().createDwelling(correlativeId, zoneId, coordinate, correlativeIdHh,
                        DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234, 50, 4, 600, 2000);   // this automatically puts it in id->dwelling map in Dwelling class
                if (!save) {
                    dwellingData.addDwelling(dd);
                }
                dd.setAttribute("originalId", id);
                if (id == SiloUtil.trackDd) {
                    SiloUtil.trackWriter.println("Read dwelling with following attributes from " + path);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop dwelling file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " dwellings.");
        return recCount;
    }

    public void readDatawithStateChangeCoordinates(String path, String state) {
        DwellingFactory factory = DwellingUtils.getFactory();
        logger.info("Reading dwelling micro data from ascii file");
        String file = "C:/models/silo/germany/input/syntheticPopulation/" + state + "/zoneAttributeswithTAZCoord_v2.csv";
        TableDataSet cellsMicrolocations = SiloUtil.readCSVfile2(file);
        cellsMicrolocations.buildStringIndex(cellsMicrolocations.getColumnPosition("name"));
        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId      = SiloUtil.findPositionInArray("id", header);
            int posZone    = SiloUtil.findPositionInArray("zone",header);
            int posHh      = SiloUtil.findPositionInArray("hhId",header);
            int posCoordX = -1;
            int posCoordY = -1;

            try {
                posCoordX = SiloUtil.findPositionInArray("coordX", header);
                posCoordY = SiloUtil.findPositionInArray("coordY", header);
            } catch (Exception e) {
                logger.warn("No coords given in dwelling input file. Models using microlocations will not work.");
            }

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id        = Integer.parseInt(lineElements[posId]);
                int zoneId      = Integer.parseInt(lineElements[posZone]);
                int hhId      = Integer.parseInt(lineElements[posHh]);
                String cell100by100 = "100mN" + Math.round(Double.parseDouble(lineElements[posCoordY])) + "E" +  Math.round(Double.parseDouble(lineElements[posCoordX]));
                double coordX = cellsMicrolocations.getStringIndexedValueAt(cell100by100, "x_mp_31468");
                double coordY = cellsMicrolocations.getStringIndexedValueAt(cell100by100, "y_mp_31468");
                Coordinate coordinate = null;
                if (posCoordX >= 0 && posCoordY >= 0) {
                    try {
                        coordinate = new Coordinate(coordX,coordY);
                        //coordinate = new Coordinate(Double.parseDouble(lineElements[posCoordX]), Double.parseDouble(lineElements[posCoordY]));
                    } catch (Exception e) {
                    }
                }
                Dwelling dd = DwellingUtils.getFactory().createDwelling(id, zoneId, coordinate, hhId,
                        DefaultDwellingTypes.DefaultDwellingTypeImpl.MF234, 50, 4, 600, 2000);   // this automatically puts it in id->dwelling map in Dwelling class
                dwellingData.addDwelling(dd);
                if (id == SiloUtil.trackDd) {
                    SiloUtil.trackWriter.println("Read dwelling with following attributes from " + path);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop dwelling file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " dwellings.");
    }
}
