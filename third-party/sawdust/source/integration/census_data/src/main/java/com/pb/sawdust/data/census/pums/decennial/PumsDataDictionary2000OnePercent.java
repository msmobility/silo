package com.pb.sawdust.data.census.pums.decennial;

import com.pb.sawdust.tabledata.metadata.DataType;

import java.util.Arrays;
import java.util.Collection;

import static com.pb.sawdust.tabledata.metadata.DataType.*;

/**
 * The {@code PumsDataDictionary2000OnePercent} is a data dictionary for the 2000 decennial Census 1% PUMS data.
 *
 * @author crf
 *         Started 10/14/11 12:09 PM
 */
public class PumsDataDictionary2000OnePercent extends PumsDataDictionary<PumsDataDictionary2000OnePercent.HouseholdField,PumsDataDictionary2000OnePercent.PersonField> {
    
    private static final PumsDataDictionary2000OnePercent instance = new PumsDataDictionary2000OnePercent();
                
    /**
     * Get an instance of the 2000 1% PUMS data dictionary.
     * 
     * @return a 2000 1% PUMS data dictionary.
     */
    public static PumsDataDictionary2000OnePercent getDictionary() {
        return instance;
    }

    /**
     * The {@code PumsDataReader2000FivePercent} class provides a convenience reader class for 2000 1% decennial Census 
     * PUMS data.
     */
    public static class PumsDataReader2000OnePercent extends PumsDataReader<PumsDataDictionary2000OnePercent.HouseholdField,PumsDataDictionary2000OnePercent.PersonField,PumsDataDictionary2000OnePercent> {
        /**
         * Constructor specifying the data dictionary and the files the built table readers will read from.
         * 
         * @param files
         *        The PUMS data file paths.
         */
        public PumsDataReader2000OnePercent(Collection<String> files) {
            super(files,instance);
        }
        
        /**
         * Constructor specifying the data dictionary and the files the built table readers will read from.
         * 
         * @param files
         *        The PUMS data file paths.
         */
        public PumsDataReader2000OnePercent(String ... files) {
            super(Arrays.asList(files),instance);
        }
    }
    
    private PumsDataDictionary2000OnePercent() {
        super(HouseholdField.class,PersonField.class);
    }
    
    @Override
    public HouseholdField[] getAllHouseholdFields() {
        return HouseholdField.values();
    }

    @Override
    public PersonField[] getAllPersonFields() {
        return PersonField.values();
    }

    @Override
    public HouseholdField getStateFipsField() {
        return HouseholdField.STATE;
    }

    @Override
    public HouseholdField getPumaField() {
        return HouseholdField.PUMA5;
    }

    @Override
    public HouseholdField getHouseholdSerialIdField() {
        return HouseholdField.SERIALNO;
    }

    @Override
    public PersonField getPersonSerialIdField() {
        return PersonField.SERIALNO;
    }

    @Override
    public HouseholdField getHouseholdWeightField() {
        return HouseholdField.HWEIGHT;
    }

    @Override
    public PersonField getPersonWeightField() {
        return PersonField.PWEIGHT;
    }

    @Override
    public HouseholdField getPersonsField() {
        return HouseholdField.PERSONS;
    }

    /**
     * The {@code HouseholdField} enum holds the household fields for the 2000 1% decennial Census PUMS data.
     */
    public enum HouseholdField implements PumsDataDictionary.PumsHouseholdField {
        RECTYPE(0,1,STRING,"Record Type"),
        SERIALNO(1,7,LONG,"Housing/Group Quarters (GQ) Unit Serial Number"),
        SAMPLE(8,1,INT,"Sample Identifier"),
        STATE(9,2,INT,"State Code"),
        REGION(11,1,INT,"Region  Code"),
        DIVISION(12,1,INT,"Division Code"),
        PUMA5(13,5,STRING,"Public Use Microdata Area Code (PUMA)"),
        PUMA1(18,5,INT,"Super Public Use Microdata Area Code (SuperPUMA)"),
        MSACMSA5(23,4,STRING,"Metropolitan Area (MA): MSA/CMSA for PUMA"),
        MSAPMSA5(27,4,STRING,"Metropolitan Area: MSA/PMSA for PUMA"),
        MSACMSA1(31,4,INT,"Metropolitan Area: MSA/CMSA for SuperPUMA"),
        MSAPMSA1(35,4,INT,"Metropolitan Area: MSA/PMSA for SuperPUMA"),
        AREATYP5(39,2,STRING,"Metropolitan Area: PUMA Relationship to MA"),
        AREATYP1(41,2,INT,"Metropolitan Area: SuperPUMA Relationship to MA"),
        TOTPUMA5(43,14,STRING,"Total Area of PUMA"),
        LNDPUMA5(57,14,STRING,"Land Area of PUMA"),
        TOTPUMA1(71,14,DOUBLE,"Total Area of SuperPUMA"),
        LNDPUMA1(85,14,DOUBLE,"Land Area of SuperPUMA"),
        SUBSAMPL(99,2,INT,"Subsample number"),
        HWEIGHT(101,4,INT,"Housing unit weight"),
        PERSONS(105,2,INT,"Number of person records following this housing record"),
        UNITTYPE(107,1,INT,"Type of unit"),
        HSUB(108,1,INT,"Substitution Flag"),
        HAUG(109,1,INT,"Augmentation Flag"),
        VACSTAT(110,1,INT,"Vacancy Status"),
        VACSTATA(111,1,INT,"Vacancy Status Allocation Flag"),
        TENURE(112,1,INT,"Home Ownership"),
        TENUREA(113,1,INT,"Home Ownership Allocation Flag"),
        BLDGSZ(114,2,STRING,"Size of Building"),
        BLDGSZA(116,1,INT,"Size of Building Allocation Flag"),
        YRBUILT(117,1,STRING,"Year Building Built"),
        YRBUILTA(118,1,INT,"Year Building Built Allocation Flag"),
        YRMOVED(119,1,STRING,"Year Moved In"),
        YRMOVEDA(120,1,INT,"Year Moved In Allocation Flag"),
        ROOMS(121,1,STRING,"Number of Rooms"),
        ROOMSA(122,1,INT,"Number of Rooms Allocation Flag"),
        BEDRMS(123,1,STRING,"Number of Bedrooms"),
        BEDRMSA(124,1,INT,"Number of Bedrooms Allocation Flag"),
        CPLUMB(125,1,STRING,"Complete Plumbing Facilities"),
        CPLUMBA(126,1,INT,"Complete Plumbing Facilities Allocation Flag"),
        CKITCH(127,1,STRING,"Complete Kitchen Facilities"),
        CKITCHA(128,1,INT,"Complete Kitchen Facilities Allocation Flag"),
        PHONE(129,1,STRING,"Telephone Availability"),
        PHONEA(130,1,INT,"Telephone Availability Allocation Flag"),
        FUEL(131,1,STRING,"Heating Fuel"),
        FUELA(132,1,INT,"Heating Fuel Allocation Flag"),
        VEHICL(133,1,STRING,"Number of Vehicles Available"),
        VEHICLA(134,1,INT,"Number of Vehicles Available Allocation Flag"),
        BUSINES(135,1,STRING,"Commercial Business on Property"),
        BUSINESA(136,1,INT,"Commercial Business on Property Allocation Flag"),
        ACRES(137,1,STRING,"Acreage"),
        ACRESA(138,1,INT,"Acreage Allocation Flag"),
        AGSALES(139,1,STRING,"Sales of Agricultural Products in 1999"),
        AGSALESA(140,1,INT,"Sales of Agricultural Products in 1999 Allocation Flag"),
        ELEC(141,4,STRING,"Cost of Electricity (annual)"),
        ELECA(145,1,INT,"Cost of Electricity (annual) Allocation Flag"),
        GAS (146,4,STRING,"Cost of Gas (annual)"),
        GASA(150,1,INT,"Cost of Gas (annual) Allocation Flag"),
        WATER (151,4,STRING,"Cost of Water and Sewer (annual)"),
        WATERA(155,1,INT,"Cost of Water and Sewer (annual) Allocation Flag"),
        OIL (156,4,STRING,"Cost of Oil, Kerosene, or Wood (annual)"),
        OILA(160,1,INT,"Cost of Oil, Kerosene, or Wood (annual) Allocation Flag"),
        RENT (161,4,STRING,"Monthly Rent"),
        RENTA(165,1,INT,"Monthly Rent Allocation Flag"),
        MEALS(166,1,STRING,"Meals Included in Rent"),
        MEALSA(167,1,INT,"Meals Included in Rent Allocation Flag"),
        MORTG1(168,1,STRING,"Mortgage Status"),
        MORTG1A(169,1,INT,"Mortgage Status Allocation Flag"),
        MRT1AMT(170,5,STRING,"Mortgage Payment (monthly amount)"),
        MRT1AMTA(175,1,INT,"Mortgage Payment (monthly amount) Allocation Flag"),
        MORTG2(176,1,STRING,"Second Mortgage Status"),
        MORTG2A(177,1,INT,"Second Mortgage Status Allocation Flag"),
        MRT2AMT(178,5,STRING,"Second Mortgage Payment (monthly amount)"),
        MRT2AMTA(183,1,INT,"Second Mortgage Payment (monthly amount) Allocation Flag"),
        TAXINCL(184,1,STRING,"Property Tax Status"),
        TAXINCLA(185,1,INT,"Property Tax Status Allocation Flag"),
        TAXAMT(186,2,INT,"Property Tax Amount (annual)"),
        TAXAMTA(188,1,INT,"Property Tax Amount (annual) Allocation Flag"),
        INSINCL(189,1,STRING,"Property Insurance Status"),
        INSINCLA(190,1,INT,"Property Insurance Status Allocation Flag"),
        INSAMT(191,4,STRING,"Property Insurance Amount (annual)"),
        INSAMTA(195,1,INT,"Property Insurance Amount (annual) Allocation Flag"),
        CONDFEE(196,4,STRING,"Condominium Fee (monthly)"),
        CONDFEEA(200,1,INT,"Condominium Fee (monthly) Allocation Flag"),
        VALUE(201,2,STRING,"Property Value"),
        VALUEA(203,1,INT,"Property Value Allocation Flag"),
        MHLOAN(204,1,STRING,"Mobile Home Loan Status"),
        MHLOANA(205,1,INT,"Mobile Home Loan Status Allocation Flag"),
        MHCOST(206,5,STRING,"Mobile Home Costs"),
        MHCOSTA(211,1,INT,"Mobile Home Costs Allocation Flag"),
        HHT(212,1,INT,"Household/Family Type"),
        P65(213,2,INT,"Number of people 65 years and over in household"),
        P18(215,2,INT,"Number of people under 18 years in household"),
        NPF(217,2,INT,"Number of people in family"),
        NOC(219,2,INT,"Number of own children under 18 years in household"),
        NRC(221,2,INT,"Number of related children under 18 years in household"),
        PSF(223,1,INT,"Presence of Subfamily in Household"),
        PAOC(224,1,INT,"Presence and Age of Own Children under 18 years"),
        PARC(225,1,INT,"Presence and Age of Related Children under 18 years"),
        SVAL(226,1,INT,"Specified Value Indicator"),
        SMOC (227,5,INT,"Selected Monthly Owner Costs"),
        SMOCAPI(232,3,INT,"Selected Monthly Owner Costs as a Percentage of Household Income"),
        SRNT(235,1,INT,"Specified Rent Indicator"),
        GRENT(236,4,INT,"Gross Rent"),
        GRAPI(240,3,INT,"Gross Rent as a Percentage of Household Income"),
        FNF(243,1,INT,"Farm/Nonfarm Recode"),
        HHL(244,1,INT,"Household Language"),
        LNGI(245,1,INT,"Linguistic Isolation"),
        WIF(246,1,INT,"Number of workers in family"),
        EMPSTAT(247,1,INT,"Family Type and Employment Status"),
        WORKEXP(248,2,INT,"Family Type and Work Experience of Householder"),
        HINC (250,8,INT,"Household Total Income in 1999"),
        FINC (258,8,INT,"Family Total Income in 1999");

        private final int start;
        private final int width;
        private final DataType columnType;
        private final String columnDescription;

        private HouseholdField(int start, int width, DataType columnType, String columnDescription) {
            this.start = start;
            this.width = width;
            this.columnType = columnType;
            this.columnDescription = columnDescription;
        }

        public int getStart() {
            return start;
        }

        public int getWidth() {
            return width;
        }

        public String getColumnName() {
            return name();
        }

        public DataType getColumnType() {
            return columnType;
        }

        public String getColumnDescription() {
            return columnDescription;
        }

        public Enum getSelf() {
            return this;
        }
        
        public int getColumnOrdinal() {
            return ordinal();
        }
    }

    /**
     * The {@code PersonField} enum holds the person fields for the 2000 1% decennial Census PUMS data.
     */
    public enum PersonField implements PumsDataDictionary.PumsPersonField {
        RECTYPE(0,1,STRING,"Record Type"),
        SERIALNO(1,7,LONG,"Housing/Group Quarters (GQ) Unit Serial Number"),
        PNUM(8,2,INT,"Person Sequence Number"),
        PAUG(10,1,INT,"Augmented Person Flag"),
        DDP(11,1,INT,"Data-defined Person Flag"),
        PWEIGHT(12,4,INT,"Person Weight"),
        RELATE(16,2,INT,"Relationship"),
        RELATEA(18,1,INT,"Relationship Allocation Flag"),
        OC (19,1,INT,"Own Child Indicator"),
        RC(20,1,INT,"Related Child Indicator"),
        PAOCF(21,1,INT,"Presence and Age of Own Children, Females"),
        SEX(22,1,INT,"Sex"),
        SEXA(23,1,INT,"Sex Allocation Flag"),
        AGE (24,2,INT,"Age"),
        AGEA(26,1,INT,"Age Allocation Flag"),
        HISPAN(27,2,INT,"Hispanic or Latino Origin"),
        HISPANA(29,1,INT,"Hispanic or Latino Origin Allocation Flag"),
        NUMRACE(30,1,INT,"Number of Major Race Groups Marked"),
        WHITE(31,1,INT,"White recode"),
        BLACK(32,1,INT,"Black or African American recode"),
        AIAN(33,1,INT,"American Indian and Alaska Native recode"),
        ASIAN(34,1,INT,"Asian recode"),
        NHPI(35,1,INT,"Native and Other Pacific Islander recode"),
        OTHER(36,1,INT,"Some other race recode"),
        RACE1(37,1,INT,"Race Recode 1"),
        RACE2(38,2,INT,"Race Recode 2"),
        RACE3(40,2,INT,"Race Recode 3"),
        RACEA(42,1,INT,"Race Allocation Flag"),
        MARSTAT(43,1,INT,"Marital Status"),
        MARSTATA(44,1,INT,"Marital Status Allocation Flag"),
        MSP(45,1,INT,"Married, Spouse Present Recode"),
        SFN(46,1,INT,"Subfamily Number for this person"),
        SFREL(47,1,INT,"Subfamily Relationship"),
        ENROLL(48,1,INT,"School Enrollment; Attended since February 1, 2000"),
        ENROLLA(49,1,INT,"School Enrollment: Attended since February 1, 2000 Allocation Flag"),
        GRADE(50,1,INT,"School Enrollment: Grade Level Attending"),
        GRADEA(51,1,INT,"School Enrollment: Grade Level Attending Allocation Flag"),
        EDUC(52,2,INT,"Educational Attainment"),
        EDUCA(54,1,INT,"Educational Attainment Allocation Flag"),
        ANCFRST1(55,3,INT,"Ancestry Code 1 for 1% file"),
        ANCSCND1(58,3,INT,"Ancestry Code 2 for 1% file"),
        ANCA(61,1,INT,"Ancestry Allocation Flag"),
        ANCR(62,1,INT,"Ancestry Recode"),
        SPEAK(63,1,STRING,"Non-English Language"),
        SPEAKA(64,1,INT,"Non-English Language Allocation Flag"),
        LANG1(65,3,INT,"Language Spoken for 1% file"),
        LANGA(68,1,INT,"Language Spoken Allocation Flag"),
        ENGABIL(69,1,STRING,"English Ability "),
        ENGABILA(70,1,INT,"English Ability Allocation Flag"),
        POB1(71,3,INT,"Place of Birth for 1% file"),
        POBA(74,1,INT,"Place of Birth Allocation Flag"),
        CITIZEN(75,1,INT,"Citizenship Status "),
        CITIZENA(76,1,INT,"Citizenship Status Allocation Flag"),
        YR2US(77,4,INT,"Year of Entry to United States"),
        YR2USA(81,1,INT,"Year of Entry to United States Allocation Flag"),
        MOB(82,1,INT,"Residence 5 Years Ago"),
        MOBA(83,1,INT,"Residence 5 Years Ago Allocation Flag"),
        MIGST1(84,3,INT,"Migration State or Foreign Country Code for 1% file"),
        MIGSTA(87,1,INT,"Migration State or Foreign County Code Allocation Flag"),
        MIGPUMA5(88,5,STRING,"Migration PUMA"),
        MIGPUMA1(93,5,INT,"Migration SuperPUMA"),
        MIGAREA5(98,2,STRING,"Migration PUMA Relationship to MA"),
        MIGAREA1(100,2,INT,"Migration SuperPUMA Relationship to MA"),
        MIGCMA5(102,4,STRING,"Migration MA: MSA/CMSA for Migration PUMA"),
        MIGCMA1(106,4,INT,"Migration MA: MSA/CMSA for Migration SuperPUMA"),
        MIGPMA5(110,4,STRING,"Migration MA: MSA/PMSA for Migration PUMA"),
        MIGPMA1(114,4,INT,"Migration MA: MSA/PMSA for Migration SuperPUMA"),
        SENSORY(118,1,STRING,"Sensory Disability"),
        SENSORYA(119,1,INT,"Sensory Disability Allocation Flag"),
        PHYSCL(120,1,STRING,"Physical Disability"),
        PHYSCLA(121,1,INT,"Physical Disability Allocation Flag"),
        MENTAL(122,1,STRING,"Mental Disability"),
        MENTALA(123,1,INT,"Mental Disability Allocation Flag"),
        SLFCARE(124,1,STRING,"Self-Care Disability"),
        SLFCAREA(125,1,INT,"Self-Care Disability Allocation Flag"),
        ABGO(126,1,STRING,"Able to Go Out Disability"),
        ABGOA(127,1,INT,"Able to Go Out Disability Allocation Flag"),
        ABWORK(128,1,STRING,"Employment Disability"),
        ABWORKA(129,1,INT,"Employment Disability Allocation Flag"),
        DISABLE(130,1,INT,"Disability Recode"),
        GRANDC(131,1,INT,"Presence of Grandchildren under 18 years"),
        GRANDCA(132,1,INT,"Presence of Grandchildren under 18 years Allocation Flag"),
        RSPNSBL(133,1,INT,"Responsible for Grandchildren"),
        RSPNSBLA(134,1,INT,"Responsible for Grandchildren Allocation Flag"),
        HOWLONG(135,1,INT,"Length of Responsibility for Grandchildren"),
        HOWLONGA(136,1,INT,"Length of Responsibility for Grandchildren Allocation Flag"),
        MILTARY(137,1,INT,"Military Service "),
        MILTARYA(138,1,INT,"Military Service Allocation Flag"),
        VPS1(139,1,INT,"Veteran's Period of Service 1:  On active duty April 1995 or later"),
        VPS2(140,1,INT,"Veteran's Period of Service 2:  On active duty August 1990 to March 1995 (including Persian Gulf War)"),
        VPS3(141,1,INT,"Veteran's Period of Service 3:  On active duty September 1980 to July 1990"),
        VPS4(142,1,INT,"Veteran's Period of Service 4:  On active duty May 1975 to August 1980"),
        VPS5(143,1,INT,"Veteran's Period of Service 5: On active duty during the Vietnam Era (August 1964 to April 1975)"),
        VPS6(144,1,INT,"Veteran's Period of Service 6:  On active duty February 1955 to July 1964"),
        VPS7(145,1,INT,"Veteran's Period of Service 7:  On active duty during the Korean War (June 1950 to January 1955)"),
        VPS8(146,1,INT,"Veteran's Period of Service 8:  On active duty during World War II (September 1940 to July 1947)"),
        VPS9(147,1,INT,"Veteran's Period of Service 9: On active duty any other time"),
        VPSA(148,1,INT,"Veteran's Period of Service Allocation Flag"),
        MILYRS(149,1,INT,"Years of Military Service"),
        MILYRSA(150,1,INT,"Years of Military Service Allocation Flag"),
        VPSR(151,2,INT,"Veteran's Period of Service Recode"),
        ESR(153,1,INT,"Employment Status Recode"),
        ESRA(154,1,INT,"Employment Status Allocation Flag"),
        ESP(155,1,INT,"Employment Status of Parent(s)"),
        POWST1(156,3,INT,"Place of Work State or Foreign Country Code for 1% file"),
        POWSTA(159,1,INT,"Place of Work State or Foreign Country Code Allocation Flag"),
        POWPUMA5(160,5,STRING,"Place of Work PUMA"),
        POWPUMA1(165,5,INT,"Place of Work SuperPUMA"),
        POWAREA5(170,2,STRING,"Place of Work PUMA Relationship to MA"),
        POWAREA1(172,2,INT,"Place of Work SuperPUMA Relationship to MA"),
        POWCMA5(174,4,STRING,"Place of Work MA: MSA/CMSA for Place of Work PUMA"),
        POWCMA1(178,4,INT,"Place of Work MA: MSA/CMSA for Place of Work SuperPUMA"),
        POWPMA5(182,4,STRING,"Place of Work MA: MSA/PMSA for Place of Work PUMA"),
        POWPMA1(186,4,INT,"Place of Work MA: MSA/PMSA for Place of Work SuperPUMA"),
        TRVMNS(190,2,INT,"Means of Transportation to Work"),
        TRVMNSA(192,1,INT,"Means of Transportation to Work Allocation Flag"),
        CARPOOL(193,1,INT,"Vehicle Occupancy"),
        CARPOOLA(194,1,INT,"Vehicle Occupancy Allocation Flag"),
        LVTIME(195,3,INT,"Time Leaving for Work"),
        LVTIMEA(198,1,INT,"Time Leaving for Work Allocation Flag"),
        TRVTIME(199,3,INT,"Travel Time to Work"),
        TRVTIMEA(202,1,INT,"Travel Time to Work Allocation Flag"),
        LAYOFF(203,1,INT,"Layoff from Job"),
        ABSENT(204,1,INT,"Absent from Work"),
        RECALL(205,1,INT,"Return-to-Work Recall"),
        LOOKWRK(206,1,INT,"Looking for Work"),
        BACKWRK(207,1,INT,"Back to Work"),
        LASTWRK(208,1,INT,"Year Last Worked"),
        LASTWRKA(209,1,INT,"Year Last Worked Allocation Flag"),
        INDCEN(210,3,INT,"Industry (Census)"),
        INDCENA(213,1,INT,"Industry (Census) Allocation Flag"),
        INDNAICS(214,8,STRING,"Industry (NAICS)"),
        OCCCEN5(222,3,INT,"Occupation (Census) for 5% file"),
        OCCCENA(225,1,INT,"Occupation (Census) Allocation Flag"),
        OCCSOC5(226,7,STRING,"Occupation (SOC) for 5% file"),
        CLWKR(233,1,INT,"Class of Worker"),
        CLWKRA(234,1,INT,"Class of Worker Allocation Flag"),
        WRKLYR(235,1,INT,"Worked in 1999"),
        WRKLYRA(236,1,INT,"Worked in 1999 Allocation Flag"),
        WEEKS(237,2,INT,"Weeks Worked in 1999"),
        WEEKSA(239,1,INT,"Weeks Worked in 1999 Allocation Flag"),
        HOURS(240,2,INT,"Hours per Week in 1999"),
        HOURSA(242,1,INT,"Hours per Week in 1999 Allocation Flag"),
        INCWS(243,6,STRING,"Wage/Salary Income in 1999"),
        INCWSA(249,1,INT,"Wage/Salary Income in 1999 Allocation Flag"),
        INCSE(250,6,STRING,"Self-Employment Income in 1999"),
        INCSEA(256,1,INT,"Self-Employment Income in 1999 Allocation Flag"),
        INCINT(257,6,STRING,"Interest Income in 1999"),
        INCINTA(263,1,INT,"Interest Income in 1999 Allocation Flag"),
        INCSS(264,5,STRING,"Social Security Income in 1999"),
        INCSSA(269,1,INT,"Social Security Income in 1999 Allocation Flag"),
        INCSSI(270,5,STRING,"Supplemental Security Income in 1999"),
        INCSSIA(275,1,INT,"Supplemental Security Income in 1999 Allocation Flag"),
        INCPA(276,5,STRING,"Public Assistance Income in 1999"),
        INCPAA(281,1,INT,"Public Assistance Income in 1999 Allocation Flag"),
        INCRET(282,6,STRING,"Retirement Income in 1999"),
        INCRETA(288,1,INT,"Retirement Income in 1999 Allocation Flag"),
        INCOTH(289,6,STRING,"Other Income in 1999"),
        INCOTHA(295,1,INT,"Other Income in 1999 Allocation Flag"),
        INCTOT(296,7,STRING,"Person's Total Income in 1999"),
        INCTOTA(303,1,INT,"Person's Total Income in 1999 Allocation Flag"),
        EARNS(304,7,INT,"Person's Total Earnings in 1999"),
        POVERTY(311,3,INT,"Person's Poverty Status");
        //FILLER(314,2,STRING,""); //eliminated, so just ignore

        private final int start;
        private final int width;
        private final DataType columnType;
        private final String columnDescription;

        private PersonField(int start, int width, DataType columnType, String columnDescription) {
            this.start = start;
            this.width = width;
            this.columnType = columnType;
            this.columnDescription = columnDescription;
        }

        public int getStart() {
            return start;
        }

        public int getWidth() {
            return width;
        }

        public String getColumnName() {
            return name();
        }

        public DataType getColumnType() {
            return columnType;
        }

        public String getColumnDescription() {
            return columnDescription;
        }

        public Enum getSelf() {
            return this;
        }               
        
        public int getColumnOrdinal() {
            return ordinal();
        }
    }
}

