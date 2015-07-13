package com.pb.sawdust.data.census.pums.spec;

import com.pb.sawdust.data.census.pums.PumaTables;

/**
 * The {@code PumaTablesReader} ...
 *
 * @author crf
 *         Started 1/31/12 9:56 AM
 */
public interface PumaTablesReader {
    PumaTables getPumaTables(String name);
}
