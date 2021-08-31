//package de.tum.bgu.msm.matsim.noise;
//
//import de.tum.bgu.msm.matsim.MatsimScenarioAssembler;
//import org.matsim.api.core.v01.Scenario;
//import org.matsim.contrib.noise.NoiseConfigGroup;
//import org.matsim.contrib.noise.NoiseReceiverPoints;
//import org.matsim.core.config.Config;
//import org.matsim.core.config.ConfigUtils;
//import org.matsim.core.config.groups.ControlerConfigGroup;
//
//public final class NoiseScenarioAssembler implements MatsimScenarioAssembler {
//
//    private final MatsimScenarioAssembler delegate;
//    private final NoiseDataManager noiseDataManager;
//
//    public NoiseScenarioAssembler(MatsimScenarioAssembler delegate, NoiseDataManager noiseDataManager) {
//        this.delegate = delegate;
//        this.noiseDataManager = noiseDataManager;
//    }
//
//    @Override
//    public Scenario assembleScenario(Config initialMatsimConfig, int year) {
//        Scenario scenario = delegate.assembleScenario(initialMatsimConfig, year);
//        final NoiseReceiverPoints noiseReceiverPoints = noiseDataManager.getNoiseReceiverPoints();
//        scenario.getConfig().controler().setRoutingAlgorithmType(ControlerConfigGroup.RoutingAlgorithmType.FastDijkstra);
//        scenario.addScenarioElement(NoiseReceiverPoints.NOISE_RECEIVER_POINTS, noiseReceiverPoints);
//        NoiseConfigGroup noiseParameters = ConfigUtils.addOrGetModule(scenario.getConfig(), NoiseConfigGroup.class);
//        noiseParameters.setInternalizeNoiseDamages(false);
//        noiseParameters.setComputeCausingAgents(false);
//        noiseParameters.setComputeNoiseDamages(false);
//        noiseParameters.setComputePopulationUnits(false);
//        noiseParameters.setComputeAvgNoiseCostPerLinkAndTime(false);
//        noiseParameters.setThrowNoiseEventsCaused(false);
//        noiseParameters.setThrowNoiseEventsAffected(false);
//        noiseParameters.setWriteOutputIteration(0);
//        scenario.getConfig().qsim().setEndTime(24*60*60);
//
//        return scenario;
//    }
//}
