package com.pb.sawdust.geography;

import com.pb.sawdust.geography.tensor.GeographicDoubleMatrix;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;

import java.util.*;

/**
 * The {@code GeographicReaders} class provides methods for reading {@code Geography}s (and related classes) from external
 * sources.
 *
 * @author crf
 *         Started 10/31/11 4:03 PM
 */
public class GeographicReaders {
    private GeographicReaders() {}  //no need to instantiate

    // ***********Geographies*******************/

    public static <T,G extends GeographyElement<T>> Geography<T,G> readGeography(DataTable geographyTable, String idColumn, String sizeColumn, String descriptionColumn, GeographyElementTableReader<T,G> elementReader) {
        Set<G> elements = new LinkedHashSet<>();
        for (DataRow row : geographyTable)
            elements.add(elementReader.readElement(row,idColumn,sizeColumn,descriptionColumn));
        return new Geography<>(elements);
    }

    public static <T,G extends GeographyElement<T>> Geography<T,G> readGeography(DataTable geographyTable, String idColumn, String sizeColumn, GeographyElementTableReader<T,G> elementReader) {
        return readGeography(geographyTable,idColumn,sizeColumn,null,elementReader);
    }

    private static <T,G extends GeographyElement<T>> Geography<T,G> readGeography(DataTable geographyTable, String idColumn, String sizeColumn, String descriptionColumn, boolean noReader) {
        return readGeography(geographyTable,idColumn,sizeColumn,descriptionColumn,GeographicReaders.<T,G>getDefaultReader(geographyTable,idColumn));
    }

    @SuppressWarnings("unchecked") //ok here
    private static <T,G extends GeographyElement<T>> GeographyElementTableReader<T,G> getDefaultReader(DataTable table, String idColumn) {
        switch (table.getColumnDataType(idColumn)) {
            case BYTE :
            case SHORT :
            case INT : return (GeographyElementTableReader<T,G>) new IdGeographyElementTableReader();
            default : return (GeographyElementTableReader<T,G>) new NamedGeographyElementTableReader();
        }
    }

    public static <T,G extends GeographyElement<T>> Geography<T,G> readGeography(DataTable geographyTable, String idColumn, String sizeColumn, String descriptionColumn) {
        return readGeography(geographyTable,idColumn,sizeColumn,descriptionColumn,true);
    }

    public static <T,G extends GeographyElement<T>> Geography<T,G> readGeography(DataTable geographyTable, String idColumn, String sizeColumn) {
        return readGeography(geographyTable,idColumn,sizeColumn,null,true);
    }

    public static interface GeographyElementIdTableReader<T> {
        T readId(DataRow row, String idColumn);
    }

    public static interface GeographyElementTableReader<T,G extends GeographyElement<T>> extends GeographyElementReader<T,G>,GeographyElementIdTableReader<T> {
        G readElement(DataRow row, String idColumn, String sizeColumn, String descriptionColumn);
    }

    public static class IdGeographyElementTableReader extends IdGeographyElementReader implements GeographyElementTableReader<Integer,IdGeographyElement> {
        public IdGeographyElement readElement(DataRow row, String idColumn, String sizeColumn, String descriptionColumn) {
            Integer id = readId(row,idColumn);
            return descriptionColumn == null ? readElement(id,row.getCellAsDouble(sizeColumn),null) :
                                               readElement(id,row.getCellAsDouble(sizeColumn),row.getCellAsString(descriptionColumn));
        }

        public Integer readId(DataRow row, String idColumn) {
            return row.getCellAsInt(idColumn);
        }
    }

    public static class NamedGeographyElementTableReader extends NamedGeographyElementReader implements GeographyElementTableReader<String,NamedGeographyElement> {
        public NamedGeographyElement readElement(DataRow row, String idColumn, String sizeColumn, String descriptionColumn) {
            String id = readId(row,idColumn);
            return descriptionColumn == null ? readElement(id,row.getCellAsDouble(sizeColumn),null) :
                                               readElement(id,row.getCellAsDouble(sizeColumn),row.getCellAsString(descriptionColumn));
        }

        public String readId(DataRow row, String idColumn) {
            return row.getCellAsString(idColumn);
        }
    }

    public static interface GeographyElementReader<T,G extends GeographyElement<T>> {
        G readElement(T id, double size, String description);
    }

    public static class IdGeographyElementReader implements GeographyElementReader<Integer,IdGeographyElement> {
        public IdGeographyElement readElement(Integer id, double size, String description) {
            return description == null ? new IdGeographyElement(id,size) : new IdGeographyElement(id,size,description);
        }
    }

    public static class NamedGeographyElementReader implements GeographyElementReader<String,NamedGeographyElement> {
        public NamedGeographyElement readElement(String id, double size, String description) {
            return description == null ? new NamedGeographyElement(id,size) : new NamedGeographyElement(id,size,description);
        }
    }


    //****************Mappings************************/


    public static <F extends GeographyElement<?>,T extends GeographyElement<?>> GeographicMapping<F,T> readMapping(Geography<?,F> fromGeography, Geography<?,T> toGeography, DoubleMatrix overlay) {
        return new FullGeographicMapping<>(new GeographicDoubleMatrix<F,T>(fromGeography,toGeography,overlay));
    }

    public static <FT,F extends GeographyElement<FT>,TT,T extends GeographyElement<TT>> GeographicMapping<F,T> readFunctionalMapping(Geography<FT,F> fromGeography, Geography<TT,T> toGeography,
                                                                                                                             DataTable mappingTable, String fromIdColumn, String toIdColumn,
                                                                                                                             GeographyElementIdTableReader<FT> fromElementReader, GeographyElementIdTableReader<TT> toElementReader) {
        Map<F,T> mapping = new HashMap<>();
        for (DataRow row : mappingTable) {
            F from = fromGeography.getElement(fromElementReader.readId(row,fromIdColumn));
            T to = toGeography.getElement(toElementReader.readId(row,toIdColumn));
            mapping.put(from,to);
        }
        return new FunctionalGeographicMapping<>(fromGeography,toGeography,mapping);
    }

    public static <FT,F extends GeographyElement<FT>,TT,T extends GeographyElement<TT>> GeographicMapping<F,T> readFunctionalMapping(DataTable mappingTable, String fromIdColumn, GeographyElementTableReader<FT,F> fromElementReader,
                                                                                                                                     String fromSizeColumn, String fromDescriptionColumn,
                                                                                                                                     String toIdColumn, GeographyElementIdTableReader<TT> toElementIdReader, GeographyElementReader<TT,T> toElementReader) {
        Map<F,T> mapping = new LinkedHashMap<>();
        Map<F,TT> idMapping = new HashMap<>();
        Map<TT,Double> toElements = new HashMap<>();
        for (DataRow row : mappingTable) {
            F from = fromElementReader.readElement(row,fromIdColumn,fromSizeColumn,fromDescriptionColumn);
            TT toId = toElementIdReader.readId(row,toIdColumn);
            idMapping.put(from,toId);
            if (!toElements.containsKey(toId))
                toElements.put(toId,from.getSize());
            else
                toElements.put(toId,toElements.get(toId)+from.getSize());
        }
        Set<T> tos = new HashSet<>();
        for (TT toId : toElements.keySet())
            tos.add(toElementReader.readElement(toId,toElements.get(toId),null));
        Geography<TT,T> toGeography = new Geography<>(tos);
        for (F from : idMapping.keySet())
            mapping.put(from,toGeography.getElement(idMapping.get(from)));
        return new FunctionalGeographicMapping<>(new Geography<FT,F>(mapping.keySet()),toGeography,mapping);
    }

    public static <FT,F extends GeographyElement<FT>,TT,T extends GeographyElement<TT>> GeographicMapping<F,T> readFunctionalMapping(DataTable mappingTable, String fromIdColumn, GeographyElementTableReader<FT,F> fromElementReader,
                                                                                                                                     String fromSizeColumn,
                                                                                                                                     String toIdColumn, GeographyElementIdTableReader<TT> toElementIdReader, GeographyElementReader<TT,T> toElementReader) {
        return readFunctionalMapping(mappingTable,fromIdColumn,fromElementReader,fromSizeColumn,null,toIdColumn,toElementIdReader,toElementReader);
    }

    public static <FT,F extends GeographyElement<FT>,TT,T extends GeographyElement<TT>> GeographicMapping<F,T> readFunctionalMapping(DataTable mappingTable, String fromIdColumn, String fromSizeColumn, String fromDescriptionColumn,String toIdColumn) {
        GeographyElementTableReader<TT,T> toElementReader = GeographicReaders.getDefaultReader(mappingTable,toIdColumn);
        return readFunctionalMapping(mappingTable,fromIdColumn,GeographicReaders.<FT,F>getDefaultReader(mappingTable,fromIdColumn),
                                     fromSizeColumn,fromDescriptionColumn,
                                     toIdColumn,toElementReader,toElementReader);


    }

    public static <FT,F extends GeographyElement<FT>,TT,T extends GeographyElement<TT>> GeographicMapping<F,T> readFunctionalMapping(DataTable mappingTable, String fromIdColumn, String fromSizeColumn, String toIdColumn) {
        return GeographicReaders.<FT,F,TT,T>readFunctionalMapping(mappingTable,fromIdColumn,fromSizeColumn,null,toIdColumn);
    }
}
