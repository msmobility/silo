package sdg.reader;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.VehicleLeavesTrafficEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleLeavesTrafficEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import sdg.data.AnalyzedPerson;
import sdg.data.MyLink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolumeEventHandler implements LinkEnterEventHandler {

    private final Network network;

    public Map<Id<Link>, MyLink> getMyLinkList() {
        return myLinkList;
    }

    private Map<Id<Link>, MyLink> myLinkList = new HashMap<>();

    public VolumeEventHandler(Network network) {
        this.network = network;
    }

    @Override
    public void handleEvent(LinkEnterEvent event) {
        Id<Link> linkId = event.getLinkId();
        MyLink link = myLinkList.get(linkId);
        if(link==null){
            link = new MyLink(linkId, network.getLinks().get(linkId));
            myLinkList.put(linkId,link);
        }
        link.addDailyVolume();
    }

    @Override
    public void reset(int iteration) {
    }

}
