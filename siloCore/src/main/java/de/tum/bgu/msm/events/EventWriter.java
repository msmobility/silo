package de.tum.bgu.msm.events;

import java.util.List;

public class EventWriter {

    //not readily implemented yet. keep for later use for now
    public static void writeEvents(List<MicroEvent> results, int year) {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.enable(SerializationFeature.INDENT_OUTPUT);
//            JsonFactory f = mapper.getFactory();
//            File jsonFile = new File("C:/Users/Nico/Desktop/events_" + year + ".json.gz");
//            FileOutputStream fos = new FileOutputStream(jsonFile);
//            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
//            JsonGenerator g = f.createGenerator(gzipOS, JsonEncoding.UTF8);
//            for (Event result : results) {
//                g.writeStartObject();
//                g.writeObject(result);
//                g.writeEndObject();
//            }
//            g.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
