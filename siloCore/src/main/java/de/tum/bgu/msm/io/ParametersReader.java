package de.tum.bgu.msm.io;

import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParametersReader {

    private final static Logger logger = Logger.getLogger(DefaultPersonReader.class);
    private final Map<Integer, Map<String, Double >> parametersMap = new HashMap<>();

    public ParametersReader() {

    }

    public Map<Integer, Map<String, Double >> readData(String path) {
        logger.info("Reading combinations of parameters from demographic events");

        PersonFactory ppFactory = PersonUtils.getFactory();
        String recString = "";
        int recCount = 0;

        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posRandomSeed = SiloUtil.findPositionInArray("RandomSeed", header);
            int posBirthFirstChildMean = SiloUtil.findPositionInArray("BirthFirstChildMean", header);
            int posBirthFirstChildDeviation = SiloUtil.findPositionInArray("BirthFirstChildDeviation", header);
            int posBirthFirstChildScale = SiloUtil.findPositionInArray("BirthFirstChildScale", header);
            int posBirthSecondChildMean = SiloUtil.findPositionInArray("BirthSecondChildMean", header);
            int posBirthSecondChildDeviation = SiloUtil.findPositionInArray("BirthSecondChildDeviation", header);
            int posBirthSecondChildScale = SiloUtil.findPositionInArray("BirthSecondChildScale", header);
            int posBirthThirdChildMean = SiloUtil.findPositionInArray("BirthThirdChildMean", header);
            int posBirthThirdChildDeviation = SiloUtil.findPositionInArray("BirthThirdChildDeviation", header);
            int posBirthThirdChildScale = SiloUtil.findPositionInArray("BirthThirdChildScale", header);
            int posBirthFourthChildMean = SiloUtil.findPositionInArray("BirthFourthChildMean", header);
            int posBirthFourthChildDeviation = SiloUtil.findPositionInArray("BirthFourthChildDeviation", header);
            int posBirthFourthChildScale = SiloUtil.findPositionInArray("BirthFourthChildScale", header);
            int posBirthLocalScaler = SiloUtil.findPositionInArray("BirthLocalScaler", header);
            int posBirthSingleScaler = SiloUtil.findPositionInArray("BirthSingleScaler", header);
            int posBirthProportionMarried = SiloUtil.findPositionInArray("BirthProportionMarried", header);
            int posDeathFemaleAlpha = SiloUtil.findPositionInArray("DeathFemaleAlpha", header);
            int posDeathFemaleScale = SiloUtil.findPositionInArray("DeathFemaleScale", header);
            int posDeathMaleAlpha = SiloUtil.findPositionInArray("DeathMaleAlpha", header);
            int posDeathMaleScale = SiloUtil.findPositionInArray("DeathMaleScale", header);
            int posDivorceFemaleLogMean = SiloUtil.findPositionInArray("DivorceFemaleLogMean", header);
            int posDivorceFemaleLogShape = SiloUtil.findPositionInArray("DivorceFemaleLogShape", header);
            int posDivorceFemaleLogScale = SiloUtil.findPositionInArray("DivorceFemaleLogScale", header);
            int posDivorceFemaleGammaMean = SiloUtil.findPositionInArray("DivorceFemaleGammaMean", header);
            int posDivorceFemaleGammaShape = SiloUtil.findPositionInArray("DivorceFemaleGammaShape", header);
            int posDivorceFemaleGammaScale = SiloUtil.findPositionInArray("DivorceFemaleGammaScale", header);
            int posDivorceMaleLogMean = SiloUtil.findPositionInArray("DivorceMaleLogMean", header);
            int posDivorceMaleLogShape = SiloUtil.findPositionInArray("DivorceMaleLogShape", header);
            int posDivorceMaleLogScale = SiloUtil.findPositionInArray("DivorceMaleLogScale", header);
            int posDivorceMaleGammaMean = SiloUtil.findPositionInArray("DivorceMaleGammaMean", header);
            int posDivorceMaleGammaShape = SiloUtil.findPositionInArray("DivorceMaleGammaShape", header);
            int posDivorceMaleGammaScale = SiloUtil.findPositionInArray("DivorceMaleGammaScale", header);
            int posLeaveHhFemaleMean = SiloUtil.findPositionInArray("LeaveHhFemaleMean", header);
            int posLeaveHhFemaleShape = SiloUtil.findPositionInArray("LeaveHhFemaleShape", header);
            int posLeaveHhFemaleScale = SiloUtil.findPositionInArray("LeaveHhFemaleScale", header);
            int posLeaveHhMaleMean = SiloUtil.findPositionInArray("LeaveHhMaleMean", header);
            int posLeaveHhMaleShape = SiloUtil.findPositionInArray("LeaveHhMaleShape", header);
            int posLeaveHhMaleScale = SiloUtil.findPositionInArray("LeaveHhMaleScale", header);
            int posMarriageFemaleNormMean = SiloUtil.findPositionInArray("MarriageFemaleNormMean", header);
            int posMarriageFemaleNormDev = SiloUtil.findPositionInArray("MarriageFemaleNormDev", header);
            int posMarriageFemaleGammaMean = SiloUtil.findPositionInArray("MarriageFemaleGammaMean", header);
            int posMarriageFemaleGammaShape = SiloUtil.findPositionInArray("MarriageFemaleGammaShape", header);
            int posMarriageFemaleScale = SiloUtil.findPositionInArray("MarriageFemaleScale", header);
            int posMarriageMaleNormMean = SiloUtil.findPositionInArray("MarriageMaleNormMean", header);
            int posMarriageMaleNormDev = SiloUtil.findPositionInArray("MarriageMaleNormDev", header);
            int posMarriageMaleGammaMean = SiloUtil.findPositionInArray("MarriageMaleGammaMean", header);
            int posMarriageMaleGammaShape = SiloUtil.findPositionInArray("MarriageMaleGammaShape", header);
            int posMarriageMaleScale = SiloUtil.findPositionInArray("MarriageMaleScale", header);
            int posMarriageInterRacialShare = SiloUtil.findPositionInArray("MarriageInterRacialShare", header);
            int posMarriageSingleHhScale = SiloUtil.findPositionInArray("MarriageSingleHhScale", header);
            int posMarriageLocalScale = SiloUtil.findPositionInArray("MarriageLocalScaler", header);
            int posDivorceLocalScale = SiloUtil.findPositionInArray("DivorceLocalScaler", header);
            int posCohabitationScale = SiloUtil.findPositionInArray("MarriageDivorceCohabitationScale", header);


            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                Map<String, Double> parametersCombination = new LinkedHashMap<>();
                String[] lineElements = recString.split(",");
                parametersCombination.put("RandomSeed",Double.parseDouble(lineElements[posRandomSeed]));
                parametersCombination.put("BirthFirstChildMean", Double.parseDouble(lineElements[posBirthFirstChildMean]));
                parametersCombination.put("BirthFirstChildDeviation", Double.parseDouble(lineElements[posBirthFirstChildDeviation]));
                parametersCombination.put("BirthFirstChildScale", Double.parseDouble(lineElements[posBirthFirstChildScale]));
                parametersCombination.put("BirthSecondChildMean", Double.parseDouble(lineElements[posBirthSecondChildMean]));
                parametersCombination.put("BirthSecondChildDeviation", Double.parseDouble(lineElements[posBirthSecondChildDeviation]));
                parametersCombination.put("BirthSecondChildScale", Double.parseDouble(lineElements[posBirthSecondChildScale]));
                parametersCombination.put("BirthThirdChildMean", Double.parseDouble(lineElements[posBirthThirdChildMean]));
                parametersCombination.put("BirthThirdChildDeviation", Double.parseDouble(lineElements[posBirthThirdChildDeviation]));
                parametersCombination.put("BirthThirdChildScale", Double.parseDouble(lineElements[posBirthThirdChildScale]));
                parametersCombination.put("BirthFourthChildMean", Double.parseDouble(lineElements[posBirthFourthChildMean]));
                parametersCombination.put("BirthFourthChildDeviation", Double.parseDouble(lineElements[posBirthFourthChildDeviation]));
                parametersCombination.put("BirthFourthChildScale", Double.parseDouble(lineElements[posBirthFourthChildScale]));
                parametersCombination.put("BirthLocalScaler", Double.parseDouble(lineElements[posBirthLocalScaler]));
                parametersCombination.put("BirthSingleScaler", Double.parseDouble(lineElements[posBirthSingleScaler]));
                parametersCombination.put("BirthProportionMarried", Double.parseDouble(lineElements[posBirthProportionMarried]));
                parametersCombination.put("DeathFemaleAlpha", Double.parseDouble(lineElements[posDeathFemaleAlpha]));
                parametersCombination.put("DeathFemaleScale", Double.parseDouble(lineElements[posDeathFemaleScale]));
                parametersCombination.put("DeathMaleAlpha", Double.parseDouble(lineElements[posDeathMaleAlpha]));
                parametersCombination.put("DeathMaleScale", Double.parseDouble(lineElements[posDeathMaleScale]));
                parametersCombination.put("DivorceFemaleLogMean", Double.parseDouble(lineElements[posDivorceFemaleLogMean]));
                parametersCombination.put("DivorceFemaleLogShape", Double.parseDouble(lineElements[posDivorceFemaleLogShape]));
                parametersCombination.put("DivorceFemaleLogScale", Double.parseDouble(lineElements[posDivorceFemaleLogScale]));
                parametersCombination.put("DivorceFemaleGammaMean", Double.parseDouble(lineElements[posDivorceFemaleGammaMean]));
                parametersCombination.put("DivorceFemaleGammaShape", Double.parseDouble(lineElements[posDivorceFemaleGammaShape]));
                parametersCombination.put("DivorceFemaleGammaScale", Double.parseDouble(lineElements[posDivorceFemaleGammaScale]));
                parametersCombination.put("DivorceMaleLogMean", Double.parseDouble(lineElements[posDivorceMaleLogMean]));
                parametersCombination.put("DivorceMaleLogShape", Double.parseDouble(lineElements[posDivorceMaleLogShape]));
                parametersCombination.put("DivorceMaleLogScale", Double.parseDouble(lineElements[posDivorceMaleLogScale]));
                parametersCombination.put("DivorceMaleGammaMean", Double.parseDouble(lineElements[posDivorceMaleGammaMean]));
                parametersCombination.put("DivorceMaleGammaShape", Double.parseDouble(lineElements[posDivorceMaleGammaShape]));
                parametersCombination.put("DivorceMaleGammaScale", Double.parseDouble(lineElements[posDivorceMaleGammaScale]));
                parametersCombination.put("LeaveHhFemaleMean", Double.parseDouble(lineElements[posLeaveHhFemaleMean]));
                parametersCombination.put("LeaveHhFemaleShape", Double.parseDouble(lineElements[posLeaveHhFemaleShape]));
                parametersCombination.put("LeaveHhFemaleScale", Double.parseDouble(lineElements[posLeaveHhFemaleScale]));
                parametersCombination.put("LeaveHhMaleMean", Double.parseDouble(lineElements[posLeaveHhMaleMean]));
                parametersCombination.put("LeaveHhMaleShape", Double.parseDouble(lineElements[posLeaveHhMaleShape]));
                parametersCombination.put("LeaveHhMaleScale", Double.parseDouble(lineElements[posLeaveHhMaleScale]));
                parametersCombination.put("MarriageFemaleNormMean", Double.parseDouble(lineElements[posMarriageFemaleNormMean]));
                parametersCombination.put("MarriageFemaleNormDev", Double.parseDouble(lineElements[posMarriageFemaleNormDev]));
                parametersCombination.put("MarriageFemaleScale", Double.parseDouble(lineElements[posMarriageFemaleScale]));
                parametersCombination.put("MarriageFemaleGammaMean", Double.parseDouble(lineElements[posMarriageFemaleGammaMean]));
                parametersCombination.put("MarriageFemaleGammaShape", Double.parseDouble(lineElements[posMarriageFemaleGammaShape]));
                parametersCombination.put("MarriageMaleNormMean", Double.parseDouble(lineElements[posMarriageMaleNormMean]));
                parametersCombination.put("MarriageMaleNormDev", Double.parseDouble(lineElements[posMarriageMaleNormDev]));
                parametersCombination.put("MarriageMaleScale", Double.parseDouble(lineElements[posMarriageMaleScale]));
                parametersCombination.put("MarriageMaleGammaMean", Double.parseDouble(lineElements[posMarriageMaleGammaMean]));
                parametersCombination.put("MarriageMaleGammaShape", Double.parseDouble(lineElements[posMarriageMaleGammaShape]));
                parametersCombination.put("MarriageInterRacialShare", Double.parseDouble(lineElements[posMarriageInterRacialShare]));
                parametersCombination.put("MarriageSingleHhScale", Double.parseDouble(lineElements[posMarriageSingleHhScale]));
                parametersCombination.put("MarriageLocalScale", Double.parseDouble(lineElements[posMarriageLocalScale]));
                parametersCombination.put("DivorceLocalScale", Double.parseDouble(lineElements[posDivorceLocalScale]));
                parametersCombination.put("MarriageDivorceCohabitationScale", Double.parseDouble(lineElements[posCohabitationScale]));
/*                parametersCombination.put("DeathFemaleAlpha", 0.13000619);
                parametersCombination.put("DeathFemaleScale", 0.00000125);
                parametersCombination.put("DeathMaleAlpha", 0.107127957);
                parametersCombination.put("DeathMaleScale", 0.0000119);
                parametersCombination.put("DivorceFemaleLogMean", 3.739433);
                parametersCombination.put("DivorceFemaleLogShape", 0.25);
                parametersCombination.put("DivorceFemaleLogScale", 0.444589481);
                parametersCombination.put("DivorceFemaleGammaMean", 27.213);
                parametersCombination.put("DivorceFemaleGammaShape", 0.903879);
                parametersCombination.put("DivorceFemaleGammaScale", 0.2364);
                parametersCombination.put("DivorceMaleLogMean", 3.7451);
                parametersCombination.put("DivorceMaleLogShape", 0.2459);
                parametersCombination.put("DivorceMaleLogScale", 0.4357);
                parametersCombination.put("DivorceMaleGammaMean", 25.4355);
                parametersCombination.put("DivorceMaleGammaShape", 0.9712);
                parametersCombination.put("DivorceMaleGammaScale", 0.2476);
                parametersCombination.put("LeaveHhFemaleMean", 3.127531651);
                parametersCombination.put("LeaveHhFemaleShape", 0.185993742);
                parametersCombination.put("LeaveHhFemaleScale", 0.455093463);
                parametersCombination.put("LeaveHhMaleMean", 3.163319934);
                parametersCombination.put("LeaveHhMaleShape", 0.187543581);
                parametersCombination.put("LeaveHhMaleScale", 0.43319649);
                parametersCombination.put("MarriageFemaleNormMean", 44.2565465);
                parametersCombination.put("MarriageFemaleNormDev", 12.6221495);
                parametersCombination.put("MarriageFemaleScale", 0.78372893);
                parametersCombination.put("MarriageFemaleGammaMean", 36.5023455);
                parametersCombination.put("MarriageFemaleGammaShape", 0.85680438);
                parametersCombination.put("MarriageMaleNormMean", 52.1206204);
                parametersCombination.put("MarriageMaleNormDev", 13.0923808);
                parametersCombination.put("MarriageMaleScale", 0.75316272);
                parametersCombination.put("MarriageMaleGammaMean", 33.1783079);
                parametersCombination.put("MarriageMaleGammaShape", 1.01027592);
                parametersCombination.put("MarriageInterRacialShare", 0.02);
                parametersCombination.put("MarriageSingleHhScale", 2.0);
                parametersCombination.put("MarriageCohabitationScale", 1.1);*/
                parametersMap.put(recCount, parametersCombination);

            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + path);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " combinations.");

        return parametersMap;
    }
}
