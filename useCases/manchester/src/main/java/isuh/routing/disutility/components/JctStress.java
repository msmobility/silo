package isuh.routing.disutility.components;

import isuh.data.Crossing;
import org.matsim.api.core.v01.network.Link;

import static isuh.data.Crossing.*;


public class JctStress {

    public static double getJunctionStress(Link link, String mode) {
        double stress = 0;

        if((boolean) link.getAttributes().getAttribute("crossVehicles")) {
            Double aadt = (Double) link.getAttributes().getAttribute("crossAadt") * 0.865;
            double lanes = (double) link.getAttributes().getAttribute("crossLanes");
            double crossingSpeed = (double) link.getAttributes().getAttribute("crossSpeedLimitMPH");
            double crossingSpeed85perc = (double) link.getAttributes().getAttribute("cross85PercSpeed") * 0.621371;
            if(aadt.isNaN()) aadt = 800.;

            Crossing crossingType = Crossing.getType(link,mode);

            if(crossingSpeed85perc >= crossingSpeed*1.1) {
                crossingSpeed = crossingSpeed85perc;
            }

            if(crossingType.equals(UNCONTROLLED)) {
                if(crossingSpeed < 60) {
                    stress = aadt/(300*crossingSpeed + 16500) + crossingSpeed/90 + lanes/3 - 0.5;
                } else {
                    stress = 1.;
                }
            } else if(crossingType.equals(PARALLEL)) {
                if(crossingSpeed <= 30) {
                    stress = aadt/24000 + lanes/3 - 2./3;
                } else {
                    stress = crossingSpeed/90 + 1./3;
                }
            } else if(crossingType.equals(SIGNAL)) {
                if(crossingSpeed < 60) {
                    stress = 0;
                } else {
                    stress = 1.;
                }
            }

            if(stress < 0.) {
                stress = 0;
            } else if (stress > 1.) {
                stress = 1;
            }
        }
        return stress;
    }
}