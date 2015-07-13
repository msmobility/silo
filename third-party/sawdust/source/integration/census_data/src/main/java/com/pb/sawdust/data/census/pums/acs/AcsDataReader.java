package com.pb.sawdust.data.census.pums.acs;

import com.pb.sawdust.data.census.pums.AbstractPumaDataReader;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.metadata.Typer;
import com.pb.sawdust.tabledata.read.CsvTableReader;
import com.pb.sawdust.tabledata.read.TableReader;

import java.util.*;

/**
 * The {@code AcsDataReader} is used to read American Community Survey (ACS) PUMS data. ACS PUMS data
 * is held in CSV files, with a separate file for person and household records.
 *       
 * @param <H>
 *        The type of the household field this reader reads, as specified by the data dictionary type. This field must be 
 *        an {@code enum}.  
 *        
 * @param <P>
 *        The type of the person field this reader reads, as specified by the data dictionary type. This field must be an 
 *        {@code enum}.
 *        
 * @param <D>
 *        The type of the data dictionary which defines the metadata about the files this reader reads.
 *
 * @author crf
 *         Started 10/13/11 11:22 PM
 */
public class AcsDataReader<H extends Enum<H> & AcsDataDictionary.AcsHouseholdField ,P extends Enum<P> & AcsDataDictionary.AcsPersonField,D extends AcsDataDictionary<H,P>> extends AbstractPumaDataReader<String[],H,P,D> {
    private final List<String> personFiles;
    private final List<String> hhFiles;

    /**
     * Constructor specifying the data dictionary and the files the built table readers will read from.
     * 
     * @param files
     *        A mapping holding the pairs of household and person record file paths. The key entry should be the path to
     *        the household file, and the value will be the path to the person file.
     * 
     * @param dataDictionary
     *        The data dictionary used by this reader.
     */
    public AcsDataReader(Map<String,String> files,D dataDictionary) {
        super(dataDictionary);
        personFiles = new LinkedList<>();
        hhFiles = new LinkedList<>();
        for (String hhFile : files.keySet()) {
            hhFiles.add(hhFile);
            personFiles.add(files.get(hhFile));
        }
    }

    @Override
    protected TableReader[] getActualTableReaders() {
        List<TableReader> householdReaders = new LinkedList<>();
        List<TableReader> personReaders = new LinkedList<>();
        Set<H> hhFields = getHouseholdFields();
        Set<P> personFields = getPersonFields();
        DataType[] hhTypes = new DataType[hhFields.size()];
        String[] hhNames = new String[hhFields.size()];
        int counter = 0;
        for (H householdField : getDataDictionary().getAllHouseholdFields()) {
            if (hhFields.contains(householdField)) {
                hhTypes[counter] = householdField.getColumnType();
                hhNames[counter] = householdField.getColumnName();
                counter++;
            }
        }

        DataType[] personTypes = new DataType[personFields.size()];
        String[] personNames = new String[personFields.size()];
        counter = 0;
        for (P personField : getDataDictionary().getAllPersonFields()) {
            if (personFields.contains(personField)) {
                personTypes[counter] = personField.getColumnType();
                personNames[counter] = personField.getColumnName();
                counter++;
            }
        }

        for (int i = 0; i < hhFiles.size(); i++) {
            CsvTableReader reader = new CsvTableReader(hhFiles.get(i),HOUSEHOLD_TABLE_NAME);
            reader.setTyper(acsTyper);
            reader.setColumnsToKeep(hhNames);
            reader.setColumnTypes(hhTypes);
            reader.addLineFilter(getHouseholdLineFilter());
            reader.setRowFilter(getHouseholdDataFilter());
            householdReaders.add(reader);

            reader = new CsvTableReader(personFiles.get(i),PERSON_TABLE_NAME);
            reader.setTyper(acsTyper);
            reader.setColumnsToKeep(personNames);
            reader.setColumnTypes(personTypes);
            reader.addLineFilter(getPersonLineFilter());
            reader.setRowFilter(getPersonDataFilter());
            personReaders.add(reader);
        }
        return new TableReader[] {new MultiFilePumsReader(householdReaders),new MultiFilePumsReader(personReaders)};
    }

    private final Typer acsTyper = new Typer() {
        public Object coerceToType(String value, DataType type) {
            if(type.getJavaType().isNumeric() && value.trim().length() == 0)
                value = "0"; //cast to zero
            return super.coerceToType(value,type);
        }
    };
}
