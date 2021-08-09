package sdg.data;

import cern.colt.map.tint.OpenIntIntHashMap;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;

public class MyLink {
    Link matsimLink;
    Id<Link> id;
    OpenIntIntHashMap volumeHourly = new OpenIntIntHashMap();
    int commuteTrip=0;

    public int getCommuteTrip() {
        return commuteTrip;
    }

    public int getDailyVolume() {
        return dailyVolume;
    }

    int dailyVolume = 0;

    public void addDailyVolume(){
        dailyVolume++;
    }

    public MyLink(Id<Link> id, Link link) {
        this.matsimLink = link;
        this.id = id;
        for(int i=0;i<25;i++) {
            volumeHourly.put(i, 0);
        }
    }

    public Id<Link> getId() {
        return id;
    }

    public void setId(Id<Link> id) {
        this.id = id;
    }

    public OpenIntIntHashMap getVolumeHourly() {
        return volumeHourly;
    }

    public void setVolumeHourly(OpenIntIntHashMap volumeHourly) {
        this.volumeHourly = volumeHourly;
    }

    public Link getMatsimLink() {
        return matsimLink;
    }
}
