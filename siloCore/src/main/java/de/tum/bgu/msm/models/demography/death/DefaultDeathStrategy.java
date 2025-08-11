package de.tum.bgu.msm.models.demography.death;

import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;

public class DefaultDeathStrategy implements DeathStrategy {

    public DefaultDeathStrategy() {
    }

    @Override
    public double calculateDeathProbability(Person person) {
        final int personAge = Math.min(person.getAge(), 100);
        Gender personSex = person.getGender();

        var alpha = 0.;

        if ("MALE".equals(personSex.name())) {
            if (personAge == 0){
                alpha = 0.0035171;
            } else if (personAge == 1){
                alpha = 0.00027502;
            } else if (personAge == 2){
                alpha = 0.00015091;
            } else if (personAge == 3){
                alpha = 0.00014069;
            } else if (personAge == 4){
                alpha = 0.00010809;
            } else if (personAge == 5){
                alpha = 0.00009131;
            } else if (personAge == 6){
                alpha = 0.00009202;
            } else if (personAge == 7){
                alpha = 0.00008643;
            } else if (personAge == 8){
                alpha = 0.00007416;
            } else if (personAge == 9){
                alpha = 0.00009636;
            } else if (personAge == 10){
                alpha = 0.00006965;
            } else if (personAge == 11){
                alpha = 0.00008112;
            } else if (personAge == 12){
                alpha = 0.00008548;
            } else if (personAge == 13){
                alpha = 0.00009094;
            } else if (personAge == 14){
                alpha = 0.0001113;
            } else if (personAge == 15){
                alpha = 0.00015519;
            } else if (personAge == 16){
                alpha = 0.0002484;
            } else if (personAge == 17){
                alpha = 0.00028661;
            } else if (personAge == 18){
                alpha = 0.00039548;
            } else if (personAge == 19){
                alpha = 0.00043643;
            } else if (personAge == 20){
                alpha = 0.00044619;
            } else if (personAge == 21){
                alpha = 0.00046719;
            } else if (personAge == 22){
                alpha = 0.00042421;
            } else if (personAge == 23){
                alpha = 0.00047249;
            } else if (personAge == 24){
                alpha = 0.00047635;
            } else if (personAge == 25){
                alpha = 0.00050404;
            } else if (personAge == 26){
                alpha = 0.00050092;
            } else if (personAge == 27){
                alpha = 0.0005178;
            } else if (personAge == 28){
                alpha = 0.00054553;
            } else if (personAge == 29){
                alpha = 0.00058138;
            } else if (personAge == 30){
                alpha = 0.00061109;
            } else if (personAge == 31){
                alpha = 0.00068366;
            } else if (personAge == 32){
                alpha = 0.00069727;
            } else if (personAge == 33){
                alpha = 0.00072194;
            } else if (personAge == 34){
                alpha = 0.00079542;
            } else if (personAge == 35){
                alpha = 0.00088198;
            } else if (personAge == 36){
                alpha = 0.00087339;
            } else if (personAge == 37){
                alpha = 0.00090841;
            } else if (personAge == 38){
                alpha = 0.00103223;
            } else if (personAge == 39){
                alpha = 0.00111491;
            } else if (personAge == 40){
                alpha = 0.00121116;
            } else if (personAge == 41){
                alpha = 0.00132725;
            } else if (personAge == 42){
                alpha = 0.00147968;
            } else if (personAge == 43){
                alpha = 0.00167684;
            } else if (personAge == 44){
                alpha = 0.00177439;
            } else if (personAge == 45){
                alpha = 0.00208116;
            } else if (personAge == 46){
                alpha = 0.00225744;
            } else if (personAge == 47){
                alpha = 0.00252991;
            } else if (personAge == 48){
                alpha = 0.00284653;
            } else if (personAge == 49){
                alpha = 0.00322336;
            } else if (personAge == 50){
                alpha = 0.00358139;
            } else if (personAge == 51){
                alpha = 0.00401607;
            } else if (personAge == 52){
                alpha = 0.00458896;
            } else if (personAge == 53){
                alpha = 0.00515636;
            } else if (personAge == 54){
                alpha = 0.00573763;
            } else if (personAge == 55){
                alpha = 0.0063011;
            } else if (personAge == 56){
                alpha = 0.00692544;
            } else if (personAge == 57){
                alpha = 0.00770856;
            } else if (personAge == 58){
                alpha = 0.0085539;
            } else if (personAge == 59){
                alpha = 0.0093556;
            } else if (personAge == 60){
                alpha = 0.01019652;
            } else if (personAge == 61){
                alpha = 0.01112414;
            } else if (personAge == 62){
                alpha = 0.01209984;
            } else if (personAge == 63){
                alpha = 0.01304853;
            } else if (personAge == 64){
                alpha = 0.01418555;
            } else if (personAge == 65){
                alpha = 0.01517552;
            } else if (personAge == 66){
                alpha = 0.01653495;
            } else if (personAge == 67){
                alpha = 0.01763878;
            } else if (personAge == 68){
                alpha = 0.0191729;
            } else if (personAge == 69){
                alpha = 0.0203515;
            } else if (personAge == 70){
                alpha = 0.0225967;
            } else if (personAge == 71){
                alpha = 0.02443693;
            } else if (personAge == 72){
                alpha = 0.02602427;
            } else if (personAge == 73){
                alpha = 0.02879558;
            } else if (personAge == 74){
                alpha = 0.03114641;
            } else if (personAge == 75){
                alpha = 0.03455648;
            } else if (personAge == 76){
                alpha = 0.03832184;
            } else if (personAge == 77){
                alpha = 0.04207809;
            } else if (personAge == 78){
                alpha = 0.04778889     ;
            } else if (personAge == 79){
                alpha = 0.05345579;
            } else if (personAge == 80){
                alpha = 0.06034223;
            } else if (personAge == 81){
                alpha = 0.06781989;
            } else if (personAge == 82){
                alpha = 0.07752174;
            } else if (personAge == 83){
                alpha = 0.08744924;
            } else if (personAge == 84){
                alpha = 0.09782105;
            } else if (personAge == 85){
                alpha = 0.10956122;
            } else if (personAge == 86){
                alpha = 0.12161384;
            } else if (personAge == 87){
                alpha = 0.13417285;
            } else if (personAge == 88){
                alpha = 0.14930005;
            } else if (personAge == 89){
                alpha = 0.16468568;
            } else if (personAge == 90){
                alpha = 0.18283321;
            } else if (personAge == 91){
                alpha = 0.20731584;
            } else if (personAge == 92){
                alpha = 0.22726903;
            } else if (personAge == 93){
                alpha = 0.25312663;
            } else if (personAge == 94){
                alpha = 0.26403672;
            } else if (personAge == 95){
                alpha = 0.28962417;
            } else if (personAge == 96){
                alpha = 0.29922388;
            } else if (personAge == 97){
                alpha = 0.3221297;
            } else if (personAge == 98){
                alpha = 0.36464002;
            } else if (personAge == 99){
                alpha = 0.38783601;
            } else if (personAge >= 100){
                alpha = 0.410106;
            }
        } else if (personSex.name().equals("FEMALE")) {
            if (personAge == 0){
                alpha = 0.00300213;
            } else if (personAge == 1){
                alpha = 0.00025727;
            } else if (personAge == 2){
                alpha = 0.00011624;
            } else if (personAge == 3){
                alpha = 0.00011728;
            } else if (personAge == 4){
                alpha = 0.00009002;
            } else if (personAge == 5){
                alpha = 0.00007165;
            } else if (personAge == 6){
                alpha = 0.00006568;
            } else if (personAge == 7){
                alpha = 0.00005983;
            } else if (personAge == 8){
                alpha = 0.00006457;
            } else if (personAge == 9){
                alpha = 0.00005999;
            } else if (personAge == 10){
                alpha = 0.00005817;
            } else if (personAge == 11){
                alpha = 0.00006773;
            } else if (personAge == 12){
                alpha = 0.0000829;
            } else if (personAge == 13){
                alpha = 0.00007989;
            } else if (personAge == 14){
                alpha = 0.0001079;
            } else if (personAge == 15){
                alpha = 0.00012969;
            } else if (personAge == 16){
                alpha = 0.00015128;
            } else if (personAge == 17){
                alpha = 0.00014984;
            } else if (personAge == 18){
                alpha = 0.00019652;
            } else if (personAge == 19){
                alpha = 0.00019595;
            } else if (personAge == 20){
                alpha = 0.00019742;
            } else if (personAge == 21){
                alpha = 0.00017717;
            } else if (personAge == 22){
                alpha = 0.00016509;
            } else if (personAge == 23){
                alpha = 0.00023189;
            } else if (personAge == 24){
                alpha = 0.00019089;
            } else if (personAge == 25){
                alpha = 0.00020002;
            } else if (personAge == 26){
                alpha = 0.00020167;
            } else if (personAge == 27){
                alpha = 0.00021866;
            } else if (personAge == 28){
                alpha = 0.0002504;
            } else if (personAge == 29){
                alpha = 0.00027532;
            } else if (personAge == 30){
                alpha = 0.00028406;
            } else if (personAge == 31){
                alpha = 0.00032232;
            } else if (personAge == 32){
                alpha = 0.00033938;
            } else if (personAge == 33){
                alpha = 0.00037054;
            } else if (personAge == 34){
                alpha = 0.00039259;
            } else if (personAge == 35){
                alpha = 0.00042245;
            } else if (personAge == 36){
                alpha = 0.00046718;
            } else if (personAge == 37){
                alpha = 0.00047345;
            } else if (personAge == 38){
                alpha = 0.00057037;
            } else if (personAge == 39){
                alpha = 0.00063424;
            } else if (personAge == 40){
                alpha = 0.00067117;
            } else if (personAge == 41){
                alpha = 0.00074618;
            } else if (personAge == 42){
                alpha = 0.00084868;
            } else if (personAge == 43){
                alpha = 0.00092229;
            } else if (personAge == 44){
                alpha = 0.00104142;
            } else if (personAge == 45){
                alpha = 0.00111667;
            } else if (personAge == 46){
                alpha = 0.00131667;
            } else if (personAge == 47){
                alpha = 0.00146544;
            } else if (personAge == 48){
                alpha = 0.00158135;
            } else if (personAge == 49){
                alpha = 0.00182147;
            } else if (personAge == 50){
                alpha = 0.00200394;
            } else if (personAge == 51){
                alpha = 0.00230274;
            } else if (personAge == 52){
                alpha = 0.00251072;
            } else if (personAge == 53){
                alpha = 0.00285827;
            } else if (personAge == 54){
                alpha = 0.00307015;
            } else if (personAge == 55){
                alpha = 0.00336385;
            } else if (personAge == 56){
                alpha = 0.00369889;
            } else if (personAge == 57){
                alpha = 0.00397935;
            } else if (personAge == 58){
                alpha = 0.00437658;
            } else if (personAge == 59){
                alpha = 0.0048601;
            } else if (personAge == 60){
                alpha = 0.00528491;
            } else if (personAge == 61){
                alpha = 0.00565687;
            } else if (personAge == 62){
                alpha = 0.00607161;
            } else if (personAge == 63){
                alpha = 0.00665744;
            } else if (personAge == 64){
                alpha = 0.00739817;
            } else if (personAge == 65){
                alpha = 0.00800149;
            } else if (personAge == 66){
                alpha = 0.00894147;
            } else if (personAge == 67){
                alpha = 0.00945906;
            } else if (personAge == 68){
                alpha = 0.01036727;
            } else if (personAge == 69){
                alpha = 0.01103579;
            } else if (personAge == 70){
                alpha = 0.0121908;
            } else if (personAge == 71){
                alpha = 0.01321404;
            } else if (personAge == 72){
                alpha = 0.01404266;
            } else if (personAge == 73){
                alpha = 0.01565656;
            } else if (personAge == 74){
                alpha = 0.01706556;
            } else if (personAge == 75){
                alpha = 0.01949607;
            } else if (personAge == 76){
                alpha = 0.02229552;
            } else if (personAge == 77){
                alpha = 0.02521566;
            } else if (personAge == 78){
                alpha = 0.0293381;
            } else if (personAge == 79){
                alpha = 0.03394772;
            } else if (personAge == 80){
                alpha = 0.03932066;
            } else if (personAge == 81){
                alpha = 0.04476366;
            } else if (personAge == 82){
                alpha = 0.05229102;
            } else if (personAge == 83){
                alpha = 0.0598285;
            } else if (personAge == 84){
                alpha = 0.06915059;
            } else if (personAge == 85){
                alpha = 0.07888544;
            } else if (personAge == 86){
                alpha = 0.09096688;
            } else if (personAge == 87){
                alpha = 0.1036328;
            } else if (personAge == 88){
                alpha = 0.11780892;
            } else if (personAge == 89){
                alpha = 0.13386588;
            } else if (personAge == 90){
                alpha = 0.15159724;
            } else if (personAge == 91){
                alpha = 0.17091808;
            } else if (personAge == 92){
                alpha = 0.19095998;
            } else if (personAge == 93){
                alpha = 0.21782221;
            } else if (personAge == 94){
                alpha = 0.2312695;
            } else if (personAge == 95){
                alpha = 0.25224087;
            } else if (personAge == 96){
                alpha = 0.26084948;
            } else if (personAge == 97){
                alpha = 0.29312636;
            } else if (personAge == 98){
                alpha = 0.32154264;
            } else if (personAge == 99){
                alpha = 0.35354396;
            } else if (personAge >= 100){
                alpha = 0.3780599;
            }
        }

        if (personAge < 0){
            throw new RuntimeException("Undefined negative person age!"+personAge);
        }

        return alpha;
    }
}