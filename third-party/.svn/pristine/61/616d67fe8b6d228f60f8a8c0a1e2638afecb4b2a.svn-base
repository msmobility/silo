package com.pb.sawdust.model.integration.transcad;

import com.pb.sawdust.io.IterableFileReader;
import com.pb.sawdust.io.TextFile;
import com.pb.sawdust.tabledata.metadata.ColumnSchema;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.util.format.DelimitedDataFormat;
import com.pb.sawdust.util.parsing.DelimitedDataParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The {@code TransCADDictionaryFileReader} ...
 *
 * @author crf <br/>
 *         Started Aug 22, 2010 5:58:29 AM
 */
public class TranscadTableDictionary {
    private TableSchema schema;
    private int lineWidth;
    private boolean hasHeader;
    private List<ColumnExtraInformation> columnExtraInformation;
    private TableType type;

    public TranscadTableDictionary(TableSchema schema, TableType type) {
        this(schema,type,false);
    }

    public TranscadTableDictionary(TableSchema schema, TableType type, boolean hasHeader) {
        this.schema = schema;
        columnExtraInformation = new ArrayList<ColumnExtraInformation>();
        this.hasHeader = hasHeader;
        this.type = type;

        lineWidth = 0;
        int start = 1;
        for (DataType t : schema.getColumnTypes()) {
            ColumnExtraInformation cei = new ColumnExtraInformation(t,start);
            columnExtraInformation.add(cei);
            start += cei.getWidth();
            lineWidth += cei.getWidth();
        }
    }

    public TranscadTableDictionary(String dictionaryFile) {
        this(new File(dictionaryFile));
    }

    public TranscadTableDictionary(String dictionaryFile, TableType type) {
        this(new File(dictionaryFile),type);
    }

    public TranscadTableDictionary(File dictionaryFile) {
        this(dictionaryFile,TableType.getTableType(dictionaryFile.getName()));
    }

    public TranscadTableDictionary(File dictionaryFile, TableType type) {
        this.type = type;
        DelimitedDataParser parser = new DelimitedDataParser(',');
        int counter = 0;
        columnExtraInformation = new ArrayList<ColumnExtraInformation>();
        for (String line : IterableFileReader.getLineIterableFile(dictionaryFile)) {
            switch(counter) {
                case 0 : schema = new TableSchema(line); break;
                case 1 : {
                    hasHeader = line.trim().equalsIgnoreCase("0 header");
                    lineWidth = hasHeader ? 0 : Integer.parseInt(line);
                    break;
                }
                default : {
                    String[] lineData = parser.parse(line.replace("\\\"","\"\""));
                    Object defaultData = null;
                    DataType baseColumnType = null;
                    int width;
                    DataType sourceType;
                    switch (lineData[1].charAt(0)) {
                        case 'I' :
                        case 'S' :
                            width = Integer.parseInt(lineData[3]);
                            sourceType = width == 4 ? DataType.INT : DataType.SHORT;
                            schema.addColumn(lineData[0],sourceType);
                            baseColumnType = width == 1 ? DataType.BYTE : sourceType;
                            if (lineData[10].trim().length() > 0)
                                defaultData = Integer.parseInt(lineData[10]);
                            break;
                        case 'F' :
                        case 'R' :
                            width = Integer.parseInt(lineData[3]);
                            sourceType = width == 4 ? DataType.FLOAT : DataType.DOUBLE;
                            schema.addColumn(lineData[0],sourceType);
                            baseColumnType = sourceType;
                            if (lineData[10].trim().length() > 0)
                                defaultData = width == 4 ? Float.parseFloat(lineData[10]) : Double.parseDouble(lineData[10]);
                            break;
                        case 'C' :
                            schema.addColumn(lineData[0],DataType.STRING);
                            baseColumnType = DataType.STRING;
                            if (lineData[10].length() > 0)
                                defaultData = lineData[10];
                            break;
//                        case 'F' :
//                            schema.addColumn(lineData[0],DataType.FLOAT);
//                            baseColumnType = DataType.FLOAT;
//                            if (lineData[10].trim().length() > 0)
//                                defaultData = Float.parseFloat(lineData[10]);
//                            break;
                        default : throw new IllegalArgumentException("Unknown data type (" + lineData[1] + ") for TransCad dictionay file: " + dictionaryFile);
                    }
                    columnExtraInformation.add(new ColumnExtraInformation(Integer.parseInt(lineData[2]),
                                                                          Integer.parseInt(lineData[3]),
                                                                          Integer.parseInt(lineData[4]),
                                                                          Integer.parseInt(lineData[5]),
                                                                          Integer.parseInt(lineData[6]),
                                                                          lineData[7],
                                                                          lineData[8],
                                                                          lineData[9],
                                                                          defaultData,
                                                                          lineData[11],
                                                                          lineData[12],
                                                                          baseColumnType));
                }
            }
            counter++;
        }
    }

    public TableSchema getSchema() {
        return schema;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public boolean hasHeader() {
        return hasHeader;
    }

    public List<ColumnExtraInformation> getColumnExtraInformation() {
        return Collections.unmodifiableList(columnExtraInformation);
    }

    public static enum TableType {
        CSV("dcc"),
        FFB("dcb"),
        FFA("dct");

        private final String defaultExtension;

        private TableType(String defaultExtension) {
            this.defaultExtension = defaultExtension;
        }

        public String getDefaultExtension() {
            return defaultExtension;
        }

        public static TableType getTableType(String fileName) {
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex < 0 || dotIndex == fileName.length()-1)
                return null;
            String extension = fileName.substring(dotIndex+1);
            for (TableType type : TableType.values())
                if (extension.equalsIgnoreCase(type.defaultExtension))
                    return type;
            return null;
        }
    }

    public void writeDictionary(String fileBase) {
        TextFile tf = new TextFile(fileBase + "." + type.getDefaultExtension(),true);
        tf.openForWriting();
        tf.writeLine(schema.getTableLabel());
        if (hasHeader())
            tf.writeLine("0 header");
        else
            tf.writeLine("" + lineWidth);
        int counter = 0;
        DelimitedDataFormat formatter = new DelimitedDataFormat(',');
        for (ColumnSchema col : schema) {
            ColumnExtraInformation cei = columnExtraInformation.get(counter++);
            String format;
            switch (cei.getBaseColumnType()) {
                case BYTE :
                case SHORT : format = "S"; break;
                case INT : format = "I"; break;
                case FLOAT : format = "F"; break;
                case DOUBLE : format = "R"; break;
                case STRING : format = "C"; break;
                default : throw new IllegalStateException("Column data type " + col.getType() + " not supported for TransCad dictionary file.");
            }
            Object[] data = {col.getColumnLabel(),format,cei.getStart(),cei.getWidth(),cei.getDecimals(),cei.getDisplayWidth(),
                             cei.getDisplayDecimals(),cei.getFormat(),cei.getAggregationMethod(),cei.getDescription(),
                             cei.getDefaultValue() == null ? "" : cei.getDefaultValue(),cei.getSplitJoinMethod(),cei.getDisplayName()};
            //need quotes around strings
            int oc = 0;
            for (Object o : data) {
                if (o instanceof String)
                    data[oc] = "\"" + o + "\"";
                oc++;
            }
//            data[1] = format; //no quotes here
            tf.writeLine(formatter.format(data).replace("\"\"\"","\""));
        }
        tf.close();
    }

    public class ColumnExtraInformation {
        private int start;
        private int width;
        private int decimals;
        private int displayWidth;
        private int displayDecimals;
        private String format;
        private String aggregationMethod;
        private String description;
        private Object defaultValue;
        private String splitJoinMethod;
        private String displayName;
        private final DataType baseColumnType;

        private ColumnExtraInformation(int start, int width, int decimals, int displayWidth, int displayDecimals, String format,
                                       String aggregationMethod, String description, Object defaultValue, String splitJoinMethod,
                                       String displayName, DataType baseColumnType) {
            this.start = start;
            this.width = width;
            this.decimals = decimals;
            this.displayWidth = displayWidth;
            this.displayDecimals = displayDecimals;
            this.format = format;
            this.aggregationMethod = aggregationMethod;
            this.description = description;
            this.defaultValue = defaultValue;
            this.splitJoinMethod = splitJoinMethod;
            this.displayName = displayName;
            this.baseColumnType = baseColumnType;
        }

        private ColumnExtraInformation(DataType type, int start) {
            //creates default column extra information
            this.start = start;
            decimals = 0;
            format = "";
            aggregationMethod = "";
            description = "";
            splitJoinMethod = "Blank";
            displayName = "";
            baseColumnType = type;
            switch (type) {
                case BYTE : {
                    width = 1;
                    displayWidth = 4;
                    displayDecimals = 0;
                    defaultValue = 0;
                    break;
                }
                case SHORT : {
                    width = 2;
                    displayWidth = 6;
                    displayDecimals = 0;
                    defaultValue = 0;
                    break;
                }
                case INT : {
                    width = 4;
                    displayWidth = 8;
                    displayDecimals = 0;
                    defaultValue = 0;
                    break;
                }
                case FLOAT : {
                    width = 4;
                    displayWidth = 10;
                    displayDecimals = 2;
                    defaultValue = 0.0;
                    break;
                }
                case DOUBLE : {
                    width = 8;
                    displayWidth = 10;
                    displayDecimals = 2;
                    defaultValue = 0.0;
                    break;
                }
                case STRING : {
                    width = 16;
                    displayWidth = 16; 
                    displayDecimals = 0;
                    defaultValue = "";
                    break;
                }
                default : throw new IllegalArgumentException("Invalid data type for Transcad table: " + type);
            }
        }

        public int getStart() {
            return start;
        }

        public int getWidth() {
            return width;
        }

        public int getDecimals() {
            return decimals;
        }

        public int getDisplayWidth() {
            return displayWidth;
        }

        public int getDisplayDecimals() {
            return displayDecimals;
        }

        public String getFormat() {
            return format;
        }

        public String getAggregationMethod() {
            return aggregationMethod;
        }

        public String getDescription() {
            return description;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public String getSplitJoinMethod() {
            return splitJoinMethod;
        }

        public String getDisplayName() {
            return displayName;
        }

        public DataType getBaseColumnType() {
            return baseColumnType;
        }

        public void setWidth(int width) {
            int change = width - this.width;
            this.width = width;
            boolean past = false;
            for (ColumnExtraInformation cei : columnExtraInformation) {
                if (cei == this)
                    past = true;
                else if (past)
                    cei.start += change;
            }
            lineWidth += change;
        }

        public void setDecimals(int decimals) {
            this.decimals = decimals;
        }

        public void setDisplayWidth(int displayWidth) {
            this.displayWidth = displayWidth;
        }

        public void setDisplayDecimals(int displayDecimals) {
            this.displayDecimals = displayDecimals;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public void setAggregationMethod(String aggregationMethod) {
            this.aggregationMethod = aggregationMethod;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public void setSplitJoinMethod(String splitJoinMethod) {
            this.splitJoinMethod = splitJoinMethod;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }
}
