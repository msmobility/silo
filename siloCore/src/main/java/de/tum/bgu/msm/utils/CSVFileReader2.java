package de.tum.bgu.msm.utils;
import de.tum.bgu.msm.common.datafile.DataTypes;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CSVFileReader2 extends TableDataFileReader2 implements DataTypes {
    protected static transient Logger logger = Logger.getLogger("com.pb.common.datafile");
    private char delimiter = 44;
    private String pattern = ",(?=(?:[^\"]*\"[^\"]*\")*(?![^\"]*\"))";
    private Pattern regexPattern;
    private int columnCount;
    private int rowCount;
    private List columnData;
    private ArrayList columnLabels;
    private int[] columnType;

    public CSVFileReader2() {
        this.regexPattern = Pattern.compile(this.pattern);
    }


    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
        this.pattern = Character.toString(delimiter) + this.pattern.substring(1);
        this.regexPattern = Pattern.compile(this.pattern);
    }

    public char getDelimiter() {
        return this.delimiter;
    }

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
        this.regexPattern = Pattern.compile(pattern);
    }

    public TableDataSet readFile(File file) throws IOException {
        return this.readFile(file, true);
    }

    public TableDataSet readFile(String urlString) throws IOException {
        return this.readFile(urlString, true);
    }

    public TableDataSet readFile(File file, boolean columnLabelsPresent) throws IOException {
        return this.readFile((File)file, columnLabelsPresent, (String[])null);
    }

    public TableDataSet readFile(String urlString, boolean columnLabelsPresent) throws IOException {
        return this.readFile((String)urlString, columnLabelsPresent, (String[])null);
    }

    public TableDataSet readFile(File file, String[] columnsToRead) throws IOException {
        return this.readFile(file, true, columnsToRead);
    }

    public TableDataSet readFile(File file, boolean columnLabelsPresent, String[] columnsToRead) throws IOException {
        if(columnsToRead != null && !columnLabelsPresent) {
            throw new RuntimeException("Column lables provided as filter but there are no column labels in CSV file");
        } else {
            this.columnCount = 0;
            this.rowCount = 0;
            this.columnData = new ArrayList();
            this.columnLabels = new ArrayList();
            this.columnType = null;
            BufferedReader inStream = this.openFile(file);
            boolean[] readColumnFlag = null;
            if(columnLabelsPresent) {
                readColumnFlag = this.readColumnLabels(inStream, columnsToRead);
                boolean readAColumn = false;
                boolean[] var7 = readColumnFlag;
                int var8 = readColumnFlag.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    boolean b = var7[var9];
                    readAColumn = readAColumn || b;
                }

                if(!readAColumn) {
                    logger.fatal("No columns read when reading file " + file);
                    throw new RuntimeException("No columns read when reading file " + file);
                }
            }

            this.readData(file, inStream, columnLabelsPresent, readColumnFlag);
            TableDataSet tds = this.makeTableDataSet();
            tds.setName(file.toString());
            return tds;
        }
    }

    public TableDataSet readFile(String urlString, boolean columnLabelsPresent, String[] columnsToRead) throws IOException {
        if(columnsToRead != null && !columnLabelsPresent) {
            throw new RuntimeException("Column lables provided as filter but there are no column labels in CSV file");
        } else {
            this.columnCount = 0;
            this.rowCount = 0;
            this.columnData = new ArrayList();
            this.columnLabels = new ArrayList();
            this.columnType = null;
            URL url = new URL(urlString);
            URLConnection urlConn = url.openConnection();
            DataInputStream dis = new DataInputStream(urlConn.getInputStream());
            BufferedReader inStream = new BufferedReader(new InputStreamReader(dis));
            boolean[] readColumnFlag = null;
            if(columnLabelsPresent) {
                readColumnFlag = this.readColumnLabels(inStream, columnsToRead);
                boolean readAColumn = false;
                boolean[] var10 = readColumnFlag;
                int var11 = readColumnFlag.length;

                for(int var12 = 0; var12 < var11; ++var12) {
                    boolean b = var10[var12];
                    readAColumn = readAColumn || b;
                }

                if(!readAColumn) {
                    logger.fatal("No columns read when reading file " + urlString);
                    throw new RuntimeException("No columns read when reading file " + urlString);
                }
            }

            this.readData(urlString, inStream, columnLabelsPresent, readColumnFlag);
            TableDataSet tds = this.makeTableDataSet();
            tds.setName(urlString.substring(urlString.lastIndexOf("/") + 1, urlString.length()));
            System.out.println("Table Name is: " + tds.getName());
            return tds;
        }
    }

    public TableDataSet readFileWithFormats(File file, String[] columnFormats) throws IOException {
        boolean columnLabelsPresent = true;
        String[] columnsToRead = null;
        if(columnsToRead != null && !columnLabelsPresent) {
            throw new RuntimeException("Column lables provided as filter but there are no column labels in CSV file");
        } else {
            this.columnCount = 0;
            this.rowCount = 0;
            this.columnData = new ArrayList();
            this.columnLabels = new ArrayList();
            this.columnType = null;
            BufferedReader inStream = this.openFile(file);
            boolean[] readColumnFlag = null;
            if(columnLabelsPresent) {
                readColumnFlag = this.readColumnLabels(inStream, (String[])columnsToRead);
            }

            this.readData(file, inStream, columnLabelsPresent, readColumnFlag, columnFormats);
            TableDataSet tds = this.makeTableDataSet();
            tds.setName(file.toString());
            return tds;
        }
    }

    private BufferedReader openFile(File file) throws IOException {
        logger.debug("Opening file: " + file);
        BufferedReader inStream = null;

        try {
            inStream = new BufferedReader(new FileReader(file));
            return inStream;
        } catch (IOException var4) {
            throw var4;
        }
    }

    private boolean[] readColumnLabels(BufferedReader inStream, String[] columnsToRead) throws IOException {
        String line = inStream.readLine();
        if(line == null) {
            throw new IOException("Error: file looks like it's empty");
        } else {
            String[] tokens = this.parseTokens(line);
            int count = tokens.length;
            boolean[] readColumnFlag = new boolean[count];

            int c;
            for(c = 0; c < count; ++c) {
                readColumnFlag[c] = (columnsToRead == null);
            }

            c = 0;

            for(int i = 0; i < count; ++i) {
                String column_name = tokens[i];
                if(columnsToRead != null) {
                    for(int j = 0; j < columnsToRead.length; ++j) {
                        if(columnsToRead[j].equalsIgnoreCase(column_name)) {
                            readColumnFlag[c] = true;
                            this.columnLabels.add(column_name);
                            ++this.columnCount;
                            break;
                        }
                    }
                } else {
                    this.columnLabels.add(column_name);
                    ++this.columnCount;
                }

                ++c;
            }

            String msg = "column read flag = ";

            for(int i = 0; i < readColumnFlag.length; ++i) {
                if(readColumnFlag[i]) {
                    msg = msg + "true";
                } else {
                    msg = msg + "false";
                }

                if(i < readColumnFlag.length - 1) {
                    msg = msg + ", ";
                }
            }

            msg = msg + "\n";
            logger.debug(msg);
            return readColumnFlag;
        }
    }

    private void readData(File file, BufferedReader inStream, boolean columnLabelsPresent, boolean[] readColumnFlag) throws IOException {
        int rowNumber = 0;
        this.rowCount = this.findNumberOfLinesInFile(file);
        logger.debug("number of lines in file: " + this.rowCount);
        if(columnLabelsPresent) {
            --this.rowCount;
        }

        if(this.rowCount == 0) {
            this.columnType = new int[this.columnCount];
            readColumnFlag = new boolean[this.columnCount];

            for(int col = 0; col < readColumnFlag.length; ++col) {
                readColumnFlag[col] = true;
                this.columnType[col] = 2;
                this.columnData.add(new String[this.rowCount]);
            }
        }

        String line;
        while((line = inStream.readLine()) != null) {
            String[] tokens = this.parseTokens(line);
            int tokenCount = tokens.length;
            if(this.columnCount == 0) {
                this.columnCount = tokenCount;
            }

            if(tokenCount < this.columnCount) {
                throw new RuntimeException(tokenCount + " columns found on line " + rowNumber + ", should be at least " + this.columnCount + " in file " + file);
            }

            int c2;
            if(readColumnFlag == null) {
                readColumnFlag = new boolean[this.columnCount];

                for(c2 = 0; c2 < readColumnFlag.length; ++c2) {
                    readColumnFlag[c2] = true;
                }
            }

            //int c2;
            if(rowNumber == 0) {
                this.columnType = new int[this.columnCount];
                int[] types = this.determineColumnTypes(line);
                c2 = -1;

                for(int c = 0; c < tokenCount; ++c) {
                    if(readColumnFlag[c]) {
                        ++c2;
                        this.columnType[c2] = types[c];
                        if(this.columnType[c2] == 3) {
                            this.columnData.add(new float[this.rowCount]);
                        } else {
                            this.columnData.add(new String[this.rowCount]);
                        }
                    }
                }
            }

            c2 = -1;

            for(c2 = 0; c2 < tokenCount; ++c2) {
                String token = tokens[c2];
                if(readColumnFlag[c2]) {
                    //++c2;
                    switch(this.columnType[c2]) {
                        case 2:
                            if(token.startsWith("\"")) {
                                token = token.substring(1);
                            }

                            if(token.endsWith("\"")) {
                                token = token.substring(0, token.length());
                            }

                            String[] s = (String[])((String[])this.columnData.get(c2));
                            s[rowNumber] = token;
                            break;
                        case 3:
                            float[] f = (float[])((float[])this.columnData.get(c2));
                            f[rowNumber] = Float.parseFloat(token);
                            break;
                        default:
                            throw new RuntimeException("unknown column data type: " + this.columnType[c2] + " for row number " + rowNumber);
                    }
                }
            }

            ++rowNumber;
        }

        inStream.close();
    }

    private void readData(String urlString, BufferedReader inStream, boolean columnLabelsPresent, boolean[] readColumnFlag) throws IOException {
        int rowNumber = 0;
        this.rowCount = this.findNumberOfLinesInFile(urlString);
        logger.debug("number of lines in file: " + this.rowCount);
        if(columnLabelsPresent) {
            --this.rowCount;
        }

        if(this.rowCount == 0) {
            this.columnType = new int[this.columnCount];
            readColumnFlag = new boolean[this.columnCount];

            for(int col = 0; col < readColumnFlag.length; ++col) {
                readColumnFlag[col] = true;
                this.columnType[col] = 2;
                this.columnData.add(new String[this.rowCount]);
            }
        }

        String line;
        while((line = inStream.readLine()) != null) {
            String[] tokens = this.parseTokens(line);
            int tokenCount = tokens.length;
            if(this.columnCount == 0) {
                this.columnCount = tokenCount;
            }

            if(tokenCount < this.columnCount) {
                throw new RuntimeException(tokenCount + " columns found on line " + rowNumber + ", should be at least " + this.columnCount + " in file " + urlString);
            }

            int c2;
            if(readColumnFlag == null) {
                readColumnFlag = new boolean[this.columnCount];

                for(c2 = 0; c2 < readColumnFlag.length; ++c2) {
                    readColumnFlag[c2] = true;
                }
            }

            //int c2;
            if(rowNumber == 0) {
                this.columnType = new int[this.columnCount];
                int[] types = this.determineColumnTypes(line);
                c2 = -1;

                for(int c = 0; c < tokenCount; ++c) {
                    if(readColumnFlag[c]) {
                        ++c2;
                        this.columnType[c2] = types[c];
                        if(this.columnType[c2] == 3) {
                            this.columnData.add(new float[this.rowCount]);
                        } else {
                            this.columnData.add(new String[this.rowCount]);
                        }
                    }
                }
            }

            c2 = -1;

            for(c2 = 0; c2 < tokenCount; ++c2) {
                String token = tokens[c2];
                if(readColumnFlag[c2]) {
                    ++c2;
                    switch(this.columnType[c2]) {
                        case 2:
                            if(token.startsWith("\"")) {
                                token = token.substring(1);
                            }

                            if(token.endsWith("\"")) {
                                token = token.substring(0, token.length());
                            }

                            String[] s = (String[])((String[])this.columnData.get(c2));
                            s[rowNumber] = token;
                            break;
                        case 3:
                            float[] f = (float[])((float[])this.columnData.get(c2));
                            f[rowNumber] = Float.parseFloat(token);
                            break;
                        default:
                            throw new RuntimeException("unknown column data type: " + this.columnType[c2] + " for row number " + rowNumber);
                    }
                }
            }

            ++rowNumber;
        }

        inStream.close();
    }

    private void readData(File file, BufferedReader inStream, boolean columnLabelsPresent, boolean[] readColumnFlag, String[] columnFormats) throws IOException {
        int rowNumber = 0;
        this.rowCount = this.findNumberOfLinesInFile(file);
        logger.debug("number of lines in file: " + this.rowCount);
        if(columnLabelsPresent) {
            --this.rowCount;
        }

        int tokenCount;
        if(this.rowCount == 0) {
            this.columnType = new int[this.columnCount];
            int c2 = -1;

            for(tokenCount = 0; tokenCount < this.columnCount; ++tokenCount) {
                if(readColumnFlag[tokenCount]) {
                    ++c2;
                    if(columnFormats[tokenCount].equals("NUMBER")) {
                        this.columnType[c2] = 3;
                        this.columnData.add(new float[this.rowCount]);
                    } else {
                        this.columnType[c2] = 2;
                        this.columnData.add(new String[this.rowCount]);
                    }
                }
            }
        }

        String line;
        while((line = inStream.readLine()) != null) {
            String[] tokens = this.parseTokens(line);
            tokenCount = tokens.length;
            if(this.columnCount == 0) {
                this.columnCount = tokenCount;
            }

            if(tokenCount < this.columnCount) {
                throw new RuntimeException(tokenCount + " columns found on line " + rowNumber + ", should be at least " + this.columnCount);
            }

            int c2;
            if(readColumnFlag == null) {
                readColumnFlag = new boolean[this.columnCount];

                for(c2 = 0; c2 < readColumnFlag.length; ++c2) {
                    readColumnFlag[c2] = true;
                }
            }

            int c;
            if(rowNumber == 0) {
                this.columnType = new int[this.columnCount];
                c2 = -1;

                for(c = 0; c < this.columnCount; ++c) {
                    if(readColumnFlag[c]) {
                        ++c2;
                        if(columnFormats[c].equals("NUMBER")) {
                            this.columnType[c2] = 3;
                            this.columnData.add(new float[this.rowCount]);
                        } else {
                            this.columnType[c2] = 2;
                            this.columnData.add(new String[this.rowCount]);
                        }
                    }
                }
            }

            c2 = -1;

            for(c = 0; c < tokenCount; ++c) {
                String token = tokens[c];
                if(readColumnFlag[c]) {
                    ++c2;
                    switch(this.columnType[c2]) {
                        case 2:
                            if(token.startsWith("\"")) {
                                token = token.substring(1);
                            }

                            if(token.endsWith("\"")) {
                                token = token.substring(0, token.length());
                            }

                            String[] s = (String[])((String[])this.columnData.get(c2));
                            s[rowNumber] = token;
                            break;
                        case 3:
                            float[] f = (float[])((float[])this.columnData.get(c2));
                            f[rowNumber] = Float.parseFloat(token);
                            break;
                        default:
                            throw new RuntimeException("unknown column data type: " + this.columnType[c2] + " for row number " + rowNumber);
                    }
                }
            }

            ++rowNumber;
        }

        inStream.close();
    }

    private int findNumberOfLinesInFile(File file) throws IOException {
        int numberOfRows = 0;

        try {
            BufferedReader stream;
            for(stream = new BufferedReader(new FileReader(file)); stream.readLine() != null; ++numberOfRows) {
            }

            stream.close();
            return numberOfRows;
        } catch (IOException var4) {
            throw var4;
        }
    }

    private int findNumberOfLinesInFile(String urlString) throws IOException {
        int numberOfRows = 0;

        try {
            URL url = new URL(urlString);
            URLConnection urlConn = url.openConnection();
            DataInputStream dis = new DataInputStream(urlConn.getInputStream());

            BufferedReader stream;
            for(stream = new BufferedReader(new InputStreamReader(dis)); stream.readLine() != null; ++numberOfRows) {
            }

            stream.close();
            return numberOfRows;
        } catch (IOException var7) {
            throw var7;
        }
    }

    private TableDataSet makeTableDataSet() {
        TableDataSet table = new TableDataSet();
        int i;
        if(this.columnLabels.size() == 0) {
            for(i = 0; i < this.columnCount; ++i) {
                this.columnLabels.add("column_" + (i + 1));
            }
        }

        for(i = 0; i < this.columnCount; ++i) {
            table.appendColumn(this.columnData.get(i), (String)this.columnLabels.get(i));
        }

        return table;
    }

    public TableDataSet readTable(String tableName) throws IOException {
        File fileName = new File(this.getMyDirectory().getPath() + File.separator + tableName + ".csv");
        TableDataSet me = this.readFile(fileName);
        me.setName(tableName);
        return me;
    }

    public void close() {
    }

    private String[] parseTokens(String line) {
        //String[] tokens = this.regexPattern.split(line);
        String[] tokens = line.split(",",0);

        for(int i = 0; i < tokens.length; ++i) {
            if(tokens[i].startsWith("\"")) {
                tokens[i] = tokens[i].substring(1);
            }

            if(tokens[i].endsWith("\"")) {
                tokens[i] = tokens[i].substring(0, tokens[i].length() - 1);
            }

            tokens[i] = tokens[i].replaceAll("\"\"", "\"");
        }

        return tokens;
    }


    private int[] determineColumnTypes(String line) {
        String[] tokens = this.regexPattern.split(line);
        int[] columnTypes = new int[tokens.length];

        for(int i = 0; i < tokens.length; ++i) {
            if(tokens[i].startsWith("\"")) {
                columnTypes[i] = 2;
            } else {
                try {
                    Float.parseFloat(tokens[i]);
                    columnTypes[i] = 3;
                } catch (NumberFormatException var6) {
                    columnTypes[i] = 2;
                }
            }
        }

        return columnTypes;
    }
}
