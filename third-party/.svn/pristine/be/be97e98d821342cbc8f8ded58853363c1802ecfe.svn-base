package com.pb.sawdust.tabledata.sql;

import com.pb.sawdust.tabledata.TableDataException;

/**
 * The {@code SqlDataSetProvider} class is used to set a "default" sql data set for use with {@code SqlDataTable}s.
 * It is primarily used in {@code SqlDataTable} constructors to select a data set to hold the tables. Because all of the
 * methods (and data members} are static, this class can (and should) be used to set a JVM-wide default {@code SqlDataSet}
 * "policy".
 *
 * @author crf <br/>
 *         Started: Jul 8, 2008 12:00:23 PM
 */
public class SqlDataSetProvider {
    private static SqlDataSet sqlDataSet = null;

    //private constructor for non-instantiable class
    private SqlDataSetProvider(){ }

    /**
     * Set the sql data set to provide from this class.
     *
     * @param sqlDataSet
     *        The sql data set to provide from this class.
     *
     * @throws NullPointerException if {@code sqlDataSet} is {@code null}.
     */
    public static void setSqlDataSet(SqlDataSet sqlDataSet) {
        if (sqlDataSet == null)
            throw new NullPointerException("Data set for provider cannot be null.");
        SqlDataSetProvider.sqlDataSet = sqlDataSet;
    }

    /**
     * Get the sql data set provided by this class.
     *
     * @return the sql data set last set by {@code setSqlDataSet(SqlDataSet)}.
     *
     * @throws TableDataException if a sql data set has not been set yet using {@code setSqlDataSet(SqlDataSet)}.
     */
    public static SqlDataSet getSqlDataSet() {
        if (sqlDataSet == null)
            throw new TableDataException("No SqlDataSet set for provider.");
        return sqlDataSet;
    }


}
