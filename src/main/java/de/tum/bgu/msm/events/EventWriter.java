package de.tum.bgu.msm.events;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class EventWriter {

    public static void writeEvents(List<EventResult> results, int year) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            JsonFactory f = mapper.getFactory();
            File jsonFile = new File("C:/Users/nkueh/Desktop/json_" + year + ".json");
            FileOutputStream fos = new FileOutputStream(jsonFile);
            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
            JsonGenerator g = f.createGenerator(gzipOS, JsonEncoding.UTF8);
            for (EventResult result : results) {
                g.writeObject(result);
            }
            g.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
