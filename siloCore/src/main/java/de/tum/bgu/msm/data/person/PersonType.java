/*
 * Copyright  2005 PB Consult Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package de.tum.bgu.msm.data.person;

/**
 * @author Greg Erhardt
 * Created on Dec 2, 2009
 */
public enum PersonType {

    MEN_AGE_0,
    MEN_AGE_1_TO_4,
    MEN_AGE_5_TO_9,
    MEN_AGE_10_TO_14,
    MEN_AGE_15_TO_19,
    MEN_AGE_20_TO_24,
    MEN_AGE_25_TO_29,
    MEN_AGE_30_TO_34,
    MEN_AGE_35_TO_39,
    MEN_AGE_40_TO_44,
    MEN_AGE_45_TO_49,
    MEN_AGE_50_TO_54,
    MEN_AGE_55_TO_59,
    MEN_AGE_60_TO_64,
    MEN_AGE_65_TO_69,
    MEN_AGE_70_TO_74,
    MEN_AGE_75_TO_79,
    MEN_AGE_80_TO_84,
    MEN_AGE_85_TO_89,
    MEN_AGE_90_TO_94,
    MEN_AGE_95_TO_99,
    MEN_AGE_100_PLUS,
    WOMEN_AGE_0,
    WOMEN_AGE_1_TO_4,
    WOMEN_AGE_5_TO_9,
    WOMEN_AGE_10_TO_14,
    WOMEN_AGE_15_TO_19,
    WOMEN_AGE_20_TO_24,
    WOMEN_AGE_25_TO_29,
    WOMEN_AGE_30_TO_34,
    WOMEN_AGE_35_TO_39,
    WOMEN_AGE_40_TO_44,
    WOMEN_AGE_45_TO_49,
    WOMEN_AGE_50_TO_54,
    WOMEN_AGE_55_TO_59,
    WOMEN_AGE_60_TO_64,
    WOMEN_AGE_65_TO_69,
    WOMEN_AGE_70_TO_74,
    WOMEN_AGE_75_TO_79,
    WOMEN_AGE_80_TO_84,
    WOMEN_AGE_85_TO_89,
    WOMEN_AGE_90_TO_94,
    WOMEN_AGE_95_TO_99,
    WOMEN_AGE_100_PLUS;

    public static PersonType defineType(Person person) {
        final int age = person.getAge();
        if (person.getGender() == Gender.MALE) {
            if (age == 0) {
                return PersonType.MEN_AGE_0;
            } else if (age <= 4) {
                return PersonType.MEN_AGE_1_TO_4;
            } else if (age <= 9) {
                return PersonType.MEN_AGE_5_TO_9;
            } else if (age <= 14) {
                return PersonType.MEN_AGE_10_TO_14;
            } else if (age <= 19) {
                return PersonType.MEN_AGE_15_TO_19;
            } else if (age <= 24) {
                return PersonType.MEN_AGE_20_TO_24;
            } else if (age <= 29) {
                return PersonType.MEN_AGE_25_TO_29;
            } else if (age <= 34) {
                return PersonType.MEN_AGE_30_TO_34;
            } else if (age <= 39) {
                return PersonType.MEN_AGE_35_TO_39;
            } else if (age <= 44) {
                return PersonType.MEN_AGE_40_TO_44;
            } else if (age <= 49) {
                return PersonType.MEN_AGE_45_TO_49;
            } else if (age <= 54) {
                return PersonType.MEN_AGE_50_TO_54;
            } else if (age <= 59) {
                return PersonType.MEN_AGE_55_TO_59;
            } else if (age <= 64) {
                return PersonType.MEN_AGE_60_TO_64;
            } else if (age <= 69) {
                return PersonType.MEN_AGE_65_TO_69;
            } else if (age <= 74) {
                return PersonType.MEN_AGE_70_TO_74;
            } else if (age <= 79) {
                return PersonType.MEN_AGE_75_TO_79;
            } else if (age <= 84) {
                return PersonType.MEN_AGE_80_TO_84;
            } else if (age <= 89) {
                return PersonType.MEN_AGE_85_TO_89;
            } else if (age <= 94) {
                return PersonType.MEN_AGE_90_TO_94;
            } else if (age <= 99) {
                return PersonType.MEN_AGE_95_TO_99;
            } else {
                return PersonType.MEN_AGE_100_PLUS;
            }
        } else {
            if (age == 0) {
                return PersonType.WOMEN_AGE_0;
            } else if (age <= 4) {
                return PersonType.WOMEN_AGE_1_TO_4;
            } else if (age <= 9) {
                return PersonType.WOMEN_AGE_5_TO_9;
            } else if (age <= 14) {
                return PersonType.WOMEN_AGE_10_TO_14;
            } else if (age <= 19) {
                return PersonType.WOMEN_AGE_15_TO_19;
            } else if (age <= 24) {
                return PersonType.WOMEN_AGE_20_TO_24;
            } else if (age <= 29) {
                return PersonType.WOMEN_AGE_25_TO_29;
            } else if (age <= 34) {
                return PersonType.WOMEN_AGE_30_TO_34;
            } else if (age <= 39) {
                return PersonType.WOMEN_AGE_35_TO_39;
            } else if (age <= 44) {
                return PersonType.WOMEN_AGE_40_TO_44;
            } else if (age <= 49) {
                return PersonType.WOMEN_AGE_45_TO_49;
            } else if (age <= 54) {
                return PersonType.WOMEN_AGE_50_TO_54;
            } else if (age <= 59) {
                return PersonType.WOMEN_AGE_55_TO_59;
            } else if (age <= 64) {
                return PersonType.WOMEN_AGE_60_TO_64;
            } else if (age <= 69) {
                return PersonType.WOMEN_AGE_65_TO_69;
            } else if (age <= 74) {
                return PersonType.WOMEN_AGE_70_TO_74;
            } else if (age <= 79) {
                return PersonType.WOMEN_AGE_75_TO_79;
            } else if (age <= 84) {
                return PersonType.WOMEN_AGE_80_TO_84;
            } else if (age <= 89) {
                return PersonType.WOMEN_AGE_85_TO_89;
            } else if (age <= 94) {
                return PersonType.WOMEN_AGE_90_TO_94;
            } else if (age <= 99) {
                return PersonType.WOMEN_AGE_95_TO_99;
            } else {
                return PersonType.WOMEN_AGE_100_PLUS;
            }
        }
    }
}
