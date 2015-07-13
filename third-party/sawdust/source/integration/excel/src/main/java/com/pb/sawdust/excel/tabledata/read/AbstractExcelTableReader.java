package com.pb.sawdust.excel.tabledata.read;

import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.read.FileTableReader;

/**
 * The {@code AbstractExcelTableReader} ...
 *
 * @author crf
 *         Started 1/19/13 5:15 PM
 */
public abstract class AbstractExcelTableReader extends FileTableReader {
    protected AbstractExcelTableReader(String file, String tableName) {
        super(file,tableName);
    }
    abstract public void setTypeDepth(int typeDepth);
    abstract public void setNullDataType(DataType nullDataType);
}
