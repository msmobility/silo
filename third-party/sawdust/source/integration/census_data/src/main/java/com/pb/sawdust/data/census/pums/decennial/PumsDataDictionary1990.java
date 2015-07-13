package com.pb.sawdust.data.census.pums.decennial;

import com.pb.sawdust.tabledata.metadata.DataType;

import java.util.Arrays;
import java.util.Collection;

import static com.pb.sawdust.tabledata.metadata.DataType.*;

/**
 * The {@code PumsDataDictionary1990} is a data dictionary for the 1990 decennial Census PUMS data.
 * 
 * @author crf
 *         Started 10/14/11 12:12 PM
 */
public class PumsDataDictionary1990 extends PumsDataDictionary<PumsDataDictionary1990.HouseholdField,PumsDataDictionary1990.PersonField> {
    
    private static final PumsDataDictionary1990 instance = new PumsDataDictionary1990();
              
    /**
     * Get an instance of the 1990 PUMS data dictionary.
     * 
     * @return a 1990 PUMS data dictionary.
     */
    public static PumsDataDictionary1990 getDictionary() {
        return instance;
    }

    /**
     * The {@code PumsDataReader1990} class provides a convenience reader class for 1990 decennial Census PUMS microdata 
     * sample data.
     */
    public static class PumsDataReader1990 extends PumsDataReader<PumsDataDictionary1990.HouseholdField,PumsDataDictionary1990.PersonField,PumsDataDictionary1990> {
        /**
         * Constructor specifying the data dictionary and the files the built table readers will read from.
         * 
         * @param files
         *        The PUMS data file paths.
         */
        public PumsDataReader1990(Collection<String> files) {
            super(files,instance);
        }

        /**
         * Constructor specifying the data dictionary and the files the built table readers will read from.
         * 
         * @param files
         *        The PUMS data file paths.
         */
        public PumsDataReader1990(String ... files) {
            super(Arrays.asList(files),instance);
        }
    }
    
    private PumsDataDictionary1990() {
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
        return HouseholdField.PUMA;
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
        return HouseholdField.HOUSWGT;
    }

    @Override
    public PersonField getPersonWeightField() {
        return PersonField.PWGT1;
    }

    @Override
    public HouseholdField getPersonsField() {
        return HouseholdField.PERSONS;
    }

    /**
     * The {@code HouseholdField} enum holds the household fields for the 1990 decennial Census PUMS data.
     */
    public enum HouseholdField implements PumsDataDictionary.PumsHouseholdField {
        RECTYPE(1,1,STRING,"Record Type"),
        SERIALNO(2,7,INT,""),
        SAMPLE(9,1,BYTE,"Sample Identifier"),
        DIVISION(10,1,BYTE,"Division code"),
        STATE(11,2,BYTE,"State Code"),
        PUMA(13,5,INT,"Public use microdata area (state dependent)"),
        AREATYPE(18,2,BYTE,"Area type revised for PUMS equivalency file"),
        MSAPMSA(20,4,SHORT,"MSA/PMSA"),
        PSA(24,3,STRING,"Planning service area (elderly sample only - state dependent)"),
        SUBSAMPL(27,2,BYTE,"Subsample number (Use to pull extracts - 1/1000/etc.)"),
        HOUSWGT(29,4,SHORT,"Housing Weight"),
        PERSONS(33,2,BYTE,"Number of person records following this housing record"),
        GQINST(35,1,BYTE,"Group quarters institution"),
        HFILLER(36,3,STRING,"Filler"),
        UNITS1(39,2,BYTE,"Units in structure"),
        HUSFLAG(41,1,BYTE,"All 100% housing unit data substituted"),
        PDSFLAG(42,1,BYTE,"All 100% person data substituted"),
        ROOMS(43,1,BYTE,"Rooms"),
        TENURE(44,1,BYTE,"Tenure"),
        ACRE10(45,1,BYTE,"On ten acres or more"),
        COMMUSE(46,1,BYTE,"Business or medical office on property"),
        VALUE(47,2,BYTE,"Property value"),
        RENT1(49,2,BYTE,"Monthly rent"),
        MEALS(51,1,BYTE,"Meals included in rent"),
        VACANCY1(52,1,BYTE,"Vacant usual home elsewhere (UHE)"),
        VACANCY2(53,1,BYTE,"Vacancy status"),
        VACANCY3(54,1,BYTE,"Boarded up status"),
        VACANCY4(55,1,BYTE,"Months vacant"),
        YRMOVED(56,1,BYTE,"When moved into this house or apartment"),
        BEDROOMS(57,1,BYTE,"Bedrooms"),
        PLUMBING(58,1,BYTE,"Complete plumbing facilities"),
        KITCHEN(59,1,BYTE,"Complete kitchen facilities"),
        TELEPHON(60,1,BYTE,"Telephone in Unit"),
        AUTOS(61,1,BYTE,"Vehicles (1 ton or less) available"),
        FUELHEAT(62,1,BYTE,"House heating fuel"),
        WATER(63,1,BYTE,"Source of water"),
        SEWAGE(64,1,BYTE,"Sewage disposal"),
        YRBUILT(65,1,BYTE,"When structure first built"),
        CONDO(66,1,BYTE,"House or apartment part of condominium"),
        ONEACRE(67,1,BYTE,"House on less than 1 acre"),
        AGSALES(68,1,BYTE,"1989 Sales of Agriculture Products"),
        ELECCOST(69,4,SHORT,"Electricity (yearly cost)*"),
        GASCOST(73,4,SHORT,"Gas (yearly cost)*"),
        WATRCOST(77,4,SHORT,"Water (yearly cost)"),
        FUELCOST(81,4,SHORT,"House heating fuel (yearly cost)"),
        RTAXAMT(85,2,BYTE,"Property taxes (yearly amount)"),
        HFILLER2(87,3,STRING,"Filler"),
        INSAMT(90,4,SHORT,"Fire/hazard/flood insurance (yearly amount)"),
        MORTGAG(94,1,BYTE,"Mortgage status"),
        MORTGAG3(95,5,INT,"Mortgage payment (monthly amount)"),
        TAXINCL(100,1,BYTE,"Payment include real estate taxes"),
        INSINCL(101,1,BYTE,"Payment include fire/hazard/flood insurance"),
        MORTGAG2(102,1,BYTE,"Second mortgage or home equity loan status"),
        MORTAMT2(103,5,INT,"Second mortgage payment (monthly amount)"),
        CONDOFEE(108,4,SHORT,"Condo fee (monthly amount)"),
        MOBLHOME(112,4,SHORT,"Mobile home costs (yearly amount)"),
        RFARM(116,1,BYTE,"Farm/nonfarm status"),
        RGRENT(117,4,SHORT,"Gross rent"),
        RGRAPI(121,2,BYTE,"Gross rent as a percentage of household income in 1989"),
        HFILLER3(123,1,STRING,"Filler"),
        ROWNRCST(124,5,SHORT,"Selected monthly owner costs"),
        RNSMOCPI(129,3,BYTE,"Selected monthly owner costs as a percentage of household income in 1989"),
        RRENTUNT(132,1,BYTE,"Specified rent unit"),
        RVALUNT(133,1,BYTE,"Specified value unit"),
        RFAMINC(134,7,INT,"Family income"),
        RHHINC(141,7,INT,"Household income"),
        RWRKR89(148,1,BYTE,"Workers in family in 1989"),
        RHHLANG(149,1,BYTE,"Household language"),
        RLINGISO(150,1,BYTE,"Linguistic isolation"),
        RHHFAMTP(151,2,BYTE,"Household/family type"),
        RNATADPT(153,2,BYTE,"Number of own natural born/adopted children in household (unweighted)"),
        RSTPCHLD(155,2,BYTE,"Number of own stepchildren in household (unweighted)"),
        RFAMPERS(157,2,BYTE,"Number of persons in family (unweighted)"),
        RNRLCHLD(159,2,BYTE,"Number of related children in household (unweighted)"),
        RNONREL(161,1,BYTE,"Presence of nonrelatives in household"),
        R18UNDR(162,1,BYTE,"Presence of person under 18 years in household"),
        R60OVER(163,1,BYTE,"Presence of persons 60 years and over in household"),
        R65OVER(164,1,BYTE,"Presence of person 65 years and over in household"),
        RSUBFAM(165,1,BYTE,"Presence of subfamilies in Household"),
        AUNITS1(166,1,BYTE,"Units in structure allocation"),
        AROOMS(167,1,BYTE,"Rooms allocation"),
        ATENURE(168,1,BYTE,"Tenure allocation"),
        AACRES10(169,1,BYTE,"On ten acres or more allocation"),
        ACOMMUSE(170,1,BYTE,"Business or medical office on property allocation"),
        AVALUE(171,1,BYTE,"Value allocation"),
        ARENT1(172,1,BYTE,"Monthly rent allocation"),
        AMEALS(173,1,BYTE,"Meals included in rent allocation"),
        AVACNCY2(174,1,BYTE,"Vacancy status allocation"),
        AVACNCY3(175,1,BYTE,"Boarded up status allocation"),
        AVACNCY4(176,1,BYTE,"Months vacant allocation"),
        AYRMOVED(177,1,BYTE,"When moved into this house or apartment allocation"),
        ABEDROOM(178,1,BYTE,"Number of bedrooms allocation"),
        APLUMBNG(179,1,BYTE,"Complete plumbing facilities allocation"),
        AKITCHEN(180,1,BYTE,"Complete kitchen facilities allocation"),
        APHONE(181,1,BYTE,"Telephones in house allocation"),
        AVEHICLE(182,1,BYTE,"Vehicles available by household allocation"),
        AFUEL(183,1,BYTE,"House heating fuel allocation"),
        AWATER(184,1,BYTE,"Source of water allocation"),
        ASEWER(185,1,BYTE,"Sewage disposal allocation"),
        AYRBUILT(186,1,BYTE,"When structure first built allocation"),
        ACONDO(187,1,BYTE,"House or apartment part of condominium allocation"),
        AONEACRE(188,1,BYTE,"House on less than 1 acre allocation"),
        AAGSALES(189,1,BYTE,"1989 Sales of Agricultural Products allocation"),
        AELECCST(190,1,BYTE,"Electricity (yearly cost) allocation"),
        AGASCST(191,1,BYTE,"Gas (yearly cost) allocation"),
        AWATRCST(192,1,BYTE,"Water (yearly cost) allocation"),
        AFUELCST(193,1,BYTE,"House heating fuel (yearly cost) allocation"),
        ATAXAMT(194,1,BYTE,"Taxes on property allocation"),
        AINSAMT(195,1,BYTE,"Fire, hazard, flood insurance allocation"),
        AMORTG(196,1,BYTE,"Mortgage status allocation"),
        AMORTG3(197,1,BYTE,"Regular mortgage payment allocation"),
        ATAXINCL(198,1,BYTE,"Payment include real estate taxes allocation"),
        AINSINCL(199,1,BYTE,"Payment include fire, hazard, flood insurance allocation"),
        AMORTG2(200,1,BYTE,"Second mortgage status allocation"),
        AMRTAMT2(201,1,BYTE,"Second mortgage payment allocation"),
        ACNDOFEE(202,1,BYTE,"Condominium fee allocation"),
        AMOBLHME(203,1,BYTE,"Mobile home costs allocation"),
        FILLER(204,28,STRING,"Filler");

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
     * The {@code PersonField} enum holds the person fields for the 1990 decennial Census PUMS data.
     */
    public enum PersonField implements PumsDataDictionary.PumsPersonField {
        RECTYPE(1,1,STRING,"Record Type"),
        SERIALNO(2,7,INT,""),
        RELAT1(9,2,BYTE,"Relationship"),
        SEX(11,1,BYTE,"Sex"),
        RACE(12,3,SHORT,"Recoded detailed race code (Appendix C)"),
        AGE(15,2,BYTE,"Age"),
        MARITAL(17,1,BYTE,"Marital status"),
        PWGT1(18,4,SHORT,"Person's weight"),
        PFILLER1(22,4,STRING,"Filler"),
        REMPLPAR(26,3,SHORT,"Employment status of parents"),
        RPOB(29,2,BYTE,"Place of birth (Recode)"),
        RSPOUSE(31,1,BYTE,"Married, spouse present/spouse absent"),
        ROWNCHLD(32,1,BYTE,"*Own child (see Appendix B, page 14)"),
        RAGECHLD(33,1,BYTE,"Presence and age of own children"),
        RRELCHLD(34,1,BYTE,"*Related child (see Appendix B, Page 14)"),
        RELAT2(35,1,BYTE,"Detailed relationship (other relative)"),
        SUBFAM2(36,1,BYTE,"Subfamily number"),
        SUBFAM1(37,1,BYTE,"Subfamily relationship"),
        HISPANIC(38,3,SHORT,"Detailed Hispanic origin code (See appendix I)"),
        POVERTY(41,3,SHORT,"Person poverty status recode (See appendix B)"),
        POB(44,3,SHORT,"Place of birth (Appendix I)"),
        CITIZEN(47,1,BYTE,"Citizenship"),
        IMMIGR(48,2,BYTE,"Year of entry"),
        SCHOOL(50,1,BYTE,"School enrollment"),
        YEARSCH(51,2,BYTE,"Educational attainment"),
        ANCSTRY1(53,3,SHORT,"Ancestry - first entry (See appendix I)"),
        ANCSTRY2(56,3,SHORT,"Ancestry - second entry (See appendix I)"),
        MOBILITY(59,1,BYTE,"Mobility status (lived here on April 1, 1985)"),
        MIGSTATE(60,2,BYTE,"Migration - State or foreign country code"),
        MIGPUMA(62,5,INT,"Migration PUMA (state dependent)"),
        LANG1(67,1,BYTE,"Language other than English at home"),
        LANG2(68,3,SHORT,"Language spoken at home (See appendix I)"),
        ENGLISH(71,1,BYTE,"Ability to speak English"),
        MILITARY(72,1,BYTE,"Military service"),
        RVETSERV(73,2,BYTE,"Veteran period of service"),
        SEPT80(75,1,BYTE,"Served September 1980 or later"),
        MAY75880(76,1,BYTE,"Served May 1975 to August 1980"),
        VIETNAM(77,1,BYTE,"Served Vietnam era (August 1964 - April 1975)"),
        FEB55(78,1,BYTE,"Served February 1955 - July 1964"),
        KOREAN(79,1,BYTE,"Served Korean conflict (June 1950 - January 1955)"),
        WWII(80,1,BYTE,"Served World War II (September 1940 - July 1947)"),
        PFILLER2(81,1,BYTE,"Filler"),
        OTHRSERV(82,1,BYTE,"Served any other time"),
        YRSSERV(83,2,BYTE,"Years of active duty military service"),
        DISABL1(85,1,BYTE,"Work limitation status"),
        DISABL2(86,1,BYTE,"Work prevented status"),
        MOBILLIM(87,1,BYTE,"Mobility limitation"),
        PERSCARE(88,1,BYTE,"Personal care limitation"),
        FERTIL(89,2,BYTE,"Number of children ever born"),
        RLABOR(91,1,BYTE,"Employment status recode"),
        WORKLWK(92,1,BYTE,"Worked last week"),
        HOURS(93,2,BYTE,"Hours worked last week"),
        POWSTATE(95,2,BYTE,"Place of work - state - (Appendix I)"),
        POWPUMA(97,5,INT,"Place of work PUMA (State dependent)"),
        MEANS(102,2,BYTE,"Means of transportation to work"),
        RIDERS(104,1,BYTE,"Vehicle occupancy"),
        DEPART(105,4,SHORT,"Time of departure for work - hour and minute"),
        TRAVTIME(109,2,BYTE,"Travel time to work"),
        TMPABSNT(111,1,BYTE,"Temporary absence from work"),
        LOOKING(112,1,BYTE,"Looking for work"),
        AVAIL(113,1,BYTE,"Available for work"),
        YEARWRK(114,1,BYTE,"Year last worked"),
        INDUSTRY(115,3,SHORT,"Industry"),
        OCCUP(118,3,SHORT,"Occupation"),
        CLASS(121,1,BYTE,"Class of worker"),
        WORK89(122,1,BYTE,"Worked last year (1989)"),
        WEEK89(123,2,BYTE,"Weeks worked last year (1989)"),
        HOUR89(125,2,BYTE,"Usual hours worked per week last year (1989)"),
        REARNING(127,6,INT,"Total person's earnings"),
        RPINCOME(133,6,INT,"Total person's income (signed)"),
        INCOME1(139,6,INT,"Wages or salary income in 1989"),
        INCOME2(145,6,INT,"Nonfarm self-employment income in 1989 (signed)"),
        INCOME3(151,6,INT,"Farm self-employment income in 1989 (signed)"),
        INCOME4(157,6,INT,"Interest, dividends, and net rental income in 1989 (signed)"),
        INCOME5(163,5,INT,"Social security income in 1989"),
        INCOME6(168,5,INT,"Public assistance income in 1989"),
        INCOME7(173,5,INT,"Retirement income in 1989"),
        INCOME8(178,5,INT,"All other income in 1989"),
        AAUGMENT(183,1,BYTE,"Augmented person (see text pp. C-5)"),
        ARELAT1(184,1,BYTE,"Relationship allocation flag"),
        ASEX(185,1,BYTE,"Sex allocation flag"),
        ARACE(186,1,BYTE,"Detailed race allocation flag"),
        AAGE(187,1,BYTE,"Age allocation flag"),
        AMARITAL(188,1,BYTE,"Marital status allocation flag"),
        AHISPAN(189,1,BYTE,"Detailed Hispanic origin allocation flag"),
        ABIRTHPL(190,1,BYTE,"Place of birth"),
        ACITIZEN(191,1,BYTE,"Citizenship allocation flag"),
        AIMMIGR(192,1,BYTE,"Year of entry allocation flag"),
        ASCHOOL(193,1,BYTE,"School enrollment allocation flag"),
        AYEARSCH(194,1,BYTE,"Highest education allocation flag"),
        AANCSTR1(195,1,BYTE,"First ancestry allocation flag"),
        AANCSTR2(196,1,BYTE,"Second ancestry allocation flag"),
        AMOBLTY(197,1,BYTE,"Mobility status allocation flag"),
        AMIGSTATE(198,1,BYTE,"Migration state allocation flag"),
        ALANG1(199,1,BYTE,"Language other than English allocation flag"),
        ALANG2(200,1,BYTE,"Language spoken at home allocation flag"),
        AENGLISH(201,1,BYTE,"Ability to speak English allocation flag"),
        AVETS1(202,1,BYTE,"Military service allocation flag"),
        ASERVPER(203,1,BYTE,"Military periods of service allocation flag"),
        AYRSSERV(204,1,BYTE,"Years of military service allocation flag"),
        ADISABL1(205,1,BYTE,"Work limitation status allocation flag"),
        ADISABL2(206,1,BYTE,"Work prevention status allocation flag"),
        AMOBLLIM(207,1,BYTE,"Mobility limitation status allocation flag"),
        APERCARE(208,1,BYTE,"Personal care limitation status allocation flag"),
        AFERTIL(209,1,BYTE,"Children ever born allocation flag"),
        ALABOR(210,1,BYTE,"Employment status recode allocation flag"),
        AHOURS(211,1,BYTE,"Hours worked last week allocation flag"),
        APOWST(212,1,BYTE,"Place of work state allocation flag"),
        AMEANS(213,1,BYTE,"Means of transportation to work allocation flag"),
        ARIDERS(214,1,BYTE,"Vehicle occupancy allocation flag"),
        ADEPART(215,1,BYTE,"Time of departure to work allocation flag"),
        ATRAVTME(216,1,BYTE,"Travel time to work allocation flag"),
        ALSTWRK(217,1,BYTE,"Year last worked allocation flag"),
        AINDUSTR(218,1,BYTE,"Industry allocation flag"),
        AOCCUP(219,1,BYTE,"Occupation allocation flag"),
        ACLASS(220,1,BYTE,"Class of worker allocation flag"),
        AWORK89(221,1,BYTE,"Worked last year allocation flag"),
        AWKS89(222,1,BYTE,"Weeks worked in 1989 allocation flag"),
        AHOUR89(223,1,BYTE,"Usual hours worked per week in 1989 allocation flag"),
        AINCOME1(224,1,BYTE,"Wages and salary income allocation flag"),
        AINCOME2(225,1,BYTE,"Nonfarm self-employment income allocation flag"),
        AINCOME3(226,1,BYTE,"Farm self-employment income allocation flag"),
        AINCOME4(227,1,BYTE,"Interest, dividend, and net rental income allocation flag"),
        AINCOME5(228,1,BYTE,"Social security income allocation flag"),
        AINCOME6(229,1,BYTE,"Public assistance allocation flag"),
        AINCOME7(230,1,BYTE,"Retirement income allocation flag"),
        AINCOME8(231,1,BYTE,"All other income allocation flag");

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


