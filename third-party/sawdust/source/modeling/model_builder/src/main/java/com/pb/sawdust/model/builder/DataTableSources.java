package com.pb.sawdust.model.builder;

import com.pb.sawdust.excel.tabledata.read.ExcelTableReader;
import com.pb.sawdust.model.integration.transcad.TranscadBinaryTableReader;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.DataTableDataProvider;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.basic.RowDataTable;
import com.pb.sawdust.tabledata.read.CsvTableReader;
import com.pb.sawdust.tabledata.read.DbfTableReader;
import com.pb.sawdust.tabledata.read.TableReader;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.exceptions.RuntimeWrappingException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;

/**
 * The {@code DataTableSources} ...
 *
 * @author crf
 *         Started 5/29/12 11:10 AM
 */
public class DataTableSources {

    public static interface DataTableBuilder {
        DataTable buildTable(TableReader reader);
    }

    private static abstract class CachedDataTableSource implements DataTableSource {
        private volatile DataTable table = null;

        protected abstract DataTable getDataTableInstance();

        public synchronized DataTable getDataTable() {
            if (table == null)
                table = getDataTableInstance();
            return table;
        }
    }

    private static abstract class AbstractDataTableSource extends CachedDataTableSource {
        private final String tableName;

        protected AbstractDataTableSource(String tableName) {
            this.tableName = tableName;
        }

        protected String getTableName() {
            return tableName;
        }
    }

    private static abstract class FileDataTableSource extends AbstractDataTableSource {
        private final Path tableFile;
        private final DataTableBuilder builder;

        protected FileDataTableSource(Path tableFile, String tableName, DataTableBuilder builder) {
            super(tableName);
            this.tableFile = tableFile;
            this.builder = builder;
        }

        protected Path getTableFile() {
            return tableFile;
        }

        protected abstract TableReader getTableReader();

        @Override
        public DataTable getDataTableInstance() {
            return builder.buildTable(getTableReader());
        }
    }

    public static class CsvDataTableSource extends FileDataTableSource {

        public CsvDataTableSource(Path tableFile, String tableName, DataTableBuilder builder) {
            super(tableFile,tableName,builder);
        }

        @Override
        public TableReader getTableReader() {
            return new CsvTableReader(getTableFile().toString(),getTableName());
        }
    }

    public static class DbfDataTableSource extends FileDataTableSource {

        public DbfDataTableSource(Path tableFile, String tableName, DataTableBuilder builder) {
            super(tableFile,tableName,builder);
        }

        @Override
        public TableReader getTableReader() {
            return new DbfTableReader(getTableFile().toString(),getTableName());
        }
    }

    public static class ExcelDataTableSource extends FileDataTableSource {

        public ExcelDataTableSource(Path tableFile, String tableName, DataTableBuilder builder) {
            super(tableFile,tableName,builder);
        }

        @Override
        public TableReader getTableReader() {
            return ExcelTableReader.excelTableReader(getTableFile().toString(),getTableName());
        }
    }

    public static class TranscadTableSource extends FileDataTableSource {

        public TranscadTableSource(Path tableFile, String tableName, DataTableBuilder builder) {
            super(tableFile,tableName,builder);
        }

        @Override
        public TableReader getTableReader() {
            return new TranscadBinaryTableReader(TranscadBinaryTableReader.formFileBase(getTableFile().toString()),getTableName());
        }
    }

    public static class JavaStaticDataTableSource extends AbstractDataTableSource {
        private final String className;
        private final String methodName;

        public JavaStaticDataTableSource(String tableName, String className, String methodName) {
            super(tableName);
            this.className = className;
            this.methodName = methodName;
        }

        @Override
        public DataTable getDataTableInstance() {
            try {
                Class<?> sourceClass = Class.forName(className);
                Method method = sourceClass.getMethod(methodName);
                if (!Modifier.isStatic(method.getModifiers()))
                    throw new IllegalStateException("Method " + methodName + " is not static on " + className);
                if (!DataTable.class.isAssignableFrom(method.getReturnType()))
                    throw new IllegalStateException("Static method " + className + "." + methodName + " does not return a DataTable");
                return (DataTable) method.invoke(null);
            } catch (NoSuchMethodException e) {
                throw new RuntimeWrappingException("Method not found, make sure it is declared public: " + className + "." + methodName,e);
            } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeWrappingException(e);
            }
        }
    }
}
