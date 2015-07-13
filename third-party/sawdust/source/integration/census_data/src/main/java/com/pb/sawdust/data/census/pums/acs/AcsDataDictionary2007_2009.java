package com.pb.sawdust.data.census.pums.acs;

import com.pb.sawdust.tabledata.metadata.DataType;

import java.util.Map;

import static com.pb.sawdust.tabledata.metadata.DataType.*;

/**
 * The {@code AcsDataDictionary2007_2009} is a data dictionary for 2007-2009 (2009 three-year) American Community Survey 
 * (ACS) PUMS data.
 *
 * @author crf
 *         Started 10/15/11 7:26 AM
 */
public class AcsDataDictionary2007_2009 extends AcsDataDictionary<AcsDataDictionary2007_2009.HouseholdField,AcsDataDictionary2007_2009.PersonField> {

    private static final AcsDataDictionary2007_2009 instance = new AcsDataDictionary2007_2009();
              
    /**
     * Get an instance of the 2007-2009 ACS data dictionary.
     * 
     * @return a 2007-2009 ACS data dictionary.
     */
    public static AcsDataDictionary2007_2009 getDictionary() {
        return instance;
    }

    /**
     * The {@code AcsDataReader2007_2009} class provides a convenience reader class for 2007-2009 (2009 three-year) American 
     * Community Survey (ACS) PUMS data. 
     */
    public static class AcsDataReader2007_2009 extends AcsDataReader<AcsDataDictionary2007_2009.HouseholdField,AcsDataDictionary2007_2009.PersonField,AcsDataDictionary2007_2009> {
        /**
         * Constructor specifying the files the built table readers will read from.
         * 
         * @param files
         *        A mapping holding the pairs of household and person record file paths. The key entry should be the path to
         *        the household file, and the value will be the path to the person file.
         */
        public AcsDataReader2007_2009(Map<String,String> files) {
            super(files,instance);
        }
    }

    private AcsDataDictionary2007_2009() {
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
        return HouseholdField.ST;
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
        return HouseholdField.WGTP;
    }

    @Override
    public PersonField getPersonWeightField() {
        return PersonField.PWGTP;
    }

    @Override
    public HouseholdField getPersonsField() {
        return HouseholdField.NP;
    }

    /**
     * The {@code HouseholdField} enum holds the household fields for the 2007-2009 (2009 three-year) American Community 
     * Survey (ACS) PUMS data.
     */
    public enum HouseholdField implements AcsDataDictionary.AcsHouseholdField {
        RT(STRING,"Record Type"),
        SERIALNO(LONG,"Housing unit/GQ person serial number"),
        DIVISION(INT,"Division code"),
        PUMA(STRING,"Public use microdata area code (PUMA)"),
        REGION(INT,"Region code"),
        ST(INT,"State Code"),
        ADJHSG(FLOAT,"Adjustment factor for housing dollar amounts (6 implied decimal places)"),
        ADJINC(FLOAT,"Adjustment factor for income and earnings dollar amounts (6 implied decimal places)"),
        WGTP(INT,"Housing Weight"),
        NP(INT,"Number of person records following this housing record"),
        TYPE(INT,"Type of unit"),
        ACR(INT,"Lot size"),
        AGS(INT,"Sales of Agriculture Products (Yearly sales)"),
        BDS(INT,"Bedrooms"),
        BLD(INT,"Units in structure"),
        BUS(INT,"Business or medical office on property"),
        CONP(INT,"Condo fee (monthly amount)"),
        ELEP(INT,"Electricity (monthly cost)"),
        FS(INT,"Yearly food stamp recipiency"),
        FULP(INT,"Fuel cost(yearly cost for fuels other than gas and electricity)"),
        GASP(INT,"Gas (monthly cost)"),
        HFL(INT,"House heating fuel"),
        INSP(INT,"Fire/hazard/flood insurance (yearly amount)"),
        MHP(INT,"Mobile home costs (yearly amount)"),
        MRGI(INT,"First mortgage payment includes fire/hazard/flood insurance"),
        MRGP(INT,"First mortgage payment (monthly amount)"),
        MRGT(INT,"First mortgage payment includes real estate taxes"),
        MRGX(INT,"First mortgage status"),
        RMS(INT,"Rooms"),
        RNTM(INT,"Meals included in rent"),
        RNTP(INT,"Monthly rent"),
        SMP(INT,"Total payment on all second and junior mortgages and home equity loans (monthly amount)"),
        TEL(INT,"Telephone in Unit"),
        TEN(INT,"Tenure"),
        VACS(INT,"Vacancy status"),
        VAL(INT,"Property value"),
        VEH(INT,"Vehicles (1 ton or less) available"),
        WATP(INT,"Water (yearly cost)"),
        YBL(INT,"When structure first built"),
        FES(INT,"Family type and employment status"),
        FINCP(INT,"Family income (past 12 months)"),
        FPARC(INT,"Family presence and age of related children"),
        GRNTP(INT,"Gross rent (monthly amount)"),
        GRPIP(INT,"Gross rent as a percentage of household income past 12 months"),
        HHL(INT,"Household language"),
        HHT(INT,"Household/family type"),
        HINCP(INT,"Household income (past 12 months)"),
        HUGCL(INT,"Flag to indicate grandchild living in housing unit"),
        HUPAC(INT,"HH presence and age of children"),
        HUPAOC(INT,"HH presence and age of own children"),
        HUPARC(INT,"HH presence and age of related children"),
        KIT(INT,"Complete kitchen facilities"),
        LNGI(INT,"Linguistic isolation"),
        MV(INT,"When moved into this house or apartment"),
        NOC(INT,"Number of own children in household (unweighted)"),
        NPF(INT,"Number of persons in family (unweighted)"),
        NPP(INT,"Grandparent headed household with no parent present"),
        NR(INT,"Presence of nonrelative in household"),
        NRC(INT,"Number of related children in household (unweighted)"),
        OCPIP(INT,"Selected monthly owner costs as a percentage of household"),
        PARTNER(INT,"Unmarried partner household"),
        PLM(INT,"Complete plumbing facilities"),
        PSF(INT,"Presence of subfamilies in Household"),
        R18(INT,"Presence of persons under 18 years in household (unweighted)"),
        R60(INT,"Presence of persons 60 years and over in household (unweighted)"),
        R65(INT,"Presence of persons 65 years and over in household (unweighted)"),
        RESMODE(INT,"Response mode"),
        SMOCP(INT,"Selected monthly owner costs"),
        SMX(INT,"Second or junior mortgage or home equity loan status"),
        SRNT(INT,"Specified rent unit"),
        SVAL(INT,"Specified value owner unit"),
        TAXP(INT,"Property taxes (yearly amount)"),
        WIF(INT,"Workers in family during the past 12 months"),
        WKEXREL(INT,"Work experience of householder and spouse"),
        WORKSTAT(INT,"Work status of householder or spouse in family households"),
        FACRP(INT,"Lot size allocation flag"),
        FAGSP(INT,"Sales of Agricultural Products allocation flag"),
        FBDSP(INT,"Number of bedrooms allocation flag"),
        FBLDP(INT,"Units in structure allocation flag"),
        FBUSP(INT,"Business or medical office on property allocation flag"),
        FCONP(INT,"Condominium fee allocation flag"),
        FELEP(INT,"Electricity (monthly cost) allocation flag"),
        FFSP(INT,"Yearly food stamp recipiency allocation flag"),
        FFULP(INT,"Fuel cost (yearly cost for fuels other than gas and electricity)allocation"),
        FGASP(INT,"Gas (monthly cost) allocation flag"),
        FHFLP(INT,"House heating fuel allocation flag "),
        FINSP(INT,"Fire, hazard, flood insurance (yearly amount) allocation flag"),
        FKITP(INT,"Complete kitchen facilities allocation flag"),
        FMHP(INT,"Mobile home costs (yearly amount) allocation flag"),
        FMRGIP(INT,"First mortgage payment includes fire, hazard, flood insurance allocation flag"),
        FMRGP(INT,"First mortgage payment (monthly amount) allocation flag"),
        FMRGTP(INT,"First mortgage payment includes real estate taxes allocation flag"),
        FMRGXP(INT,"First mortgage status allocation flag"),
        FMVP(INT,"When moved into this house or apartment allocation flag"),
        FPLMP(INT,"Complete plumbing facilities allocation flag"),
        FRMSP(INT,"Rooms allocation flag"),
        FRNTMP(INT,"Meals included in rent allocation flag"),
        FRNTP(INT,"Monthly rent allocation flag"),
        FSMP(INT,"Total payment on second and junior mortgages and home equity loans (monthly amount) allocation flag"),
        FSMXHP(INT,"Home equity loan status allocation flag"),
        FSMXSP(INT,"Second mortgage status allocation flag"),
        FTAXP(INT,"Taxes on property allocation flag"),
        FTELP(INT,"Telephones in house allocation flag"),
        FTENP(INT,"Tenure allocation flag"),
        FVACSP(INT,"Vacancy status allocation flag"),
        FVALP(INT,"Property value allocation flag"),
        FVEHP(INT,"Vehicles available allocation flag"),
        FWATP(INT,"Water (yearly cost) allocation flag"),
        FYBLP(INT,"When structure first built allocation flag "),
        WGTP1(INT,"Housing Weight replicate 1"),
        WGTP2(INT,"Housing Weight replicate 2"),
        WGTP3(INT,"Housing Weight replicate 3"),
        WGTP4(INT,"Housing Weight replicate 4"),
        WGTP5(INT,"Housing Weight replicate 5"),
        WGTP6(INT,"Housing Weight replicate 6"),
        WGTP7(INT,"Housing Weight replicate 7"),
        WGTP8(INT,"Housing Weight replicate 8"),
        WGTP9(INT,"Housing Weight replicate 9"),
        WGTP10(INT,"Housing Weight replicate 10"),
        WGTP11(INT,"Housing Weight replicate 11"),
        WGTP12(INT,"Housing Weight replicate 12"),
        WGTP13(INT,"Housing Weight replicate 13"),
        WGTP14(INT,"Housing Weight replicate 14"),
        WGTP15(INT,"Housing Weight replicate 15"),
        WGTP16(INT,"Housing Weight replicate 16"),
        WGTP17(INT,"Housing Weight replicate 17"),
        WGTP18(INT,"Housing Weight replicate 18"),
        WGTP19(INT,"Housing Weight replicate 19"),
        WGTP20(INT,"Housing Weight replicate 20"),
        WGTP21(INT,"Housing Weight replicate 21"),
        WGTP22(INT,"Housing Weight replicate 22"),
        WGTP23(INT,"Housing Weight replicate 23"),
        WGTP24(INT,"Housing Weight replicate 24"),
        WGTP25(INT,"Housing Weight replicate 25"),
        WGTP26(INT,"Housing Weight replicate 26"),
        WGTP27(INT,"Housing Weight replicate 27"),
        WGTP28(INT,"Housing Weight replicate 28"),
        WGTP29(INT,"Housing Weight replicate 29"),
        WGTP30(INT,"Housing Weight replicate 30"),
        WGTP31(INT,"Housing Weight replicate 31"),
        WGTP32(INT,"Housing Weight replicate 32"),
        WGTP33(INT,"Housing Weight replicate 33"),
        WGTP34(INT,"Housing Weight replicate 34"),
        WGTP35(INT,"Housing Weight replicate 35"),
        WGTP36(INT,"Housing Weight replicate 36"),
        WGTP37(INT,"Housing Weight replicate 37"),
        WGTP38(INT,"Housing Weight replicate 38"),
        WGTP39(INT,"Housing Weight replicate 39"),
        WGTP40(INT,"Housing Weight replicate 40"),
        WGTP41(INT,"Housing Weight replicate 41"),
        WGTP42(INT,"Housing Weight replicate 42"),
        WGTP43(INT,"Housing Weight replicate 43"),
        WGTP44(INT,"Housing Weight replicate 44"),
        WGTP45(INT,"Housing Weight replicate 45"),
        WGTP46(INT,"Housing Weight replicate 46"),
        WGTP47(INT,"Housing Weight replicate 47"),
        WGTP48(INT,"Housing Weight replicate 48"),
        WGTP49(INT,"Housing Weight replicate 49"),
        WGTP50(INT,"Housing Weight replicate 50"),
        WGTP51(INT,"Housing Weight replicate 51"),
        WGTP52(INT,"Housing Weight replicate 52"),
        WGTP53(INT,"Housing Weight replicate 53"),
        WGTP54(INT,"Housing Weight replicate 54"),
        WGTP55(INT,"Housing Weight replicate 55"),
        WGTP56(INT,"Housing Weight replicate 56"),
        WGTP57(INT,"Housing Weight replicate 57"),
        WGTP58(INT,"Housing Weight replicate 58"),
        WGTP59(INT,"Housing Weight replicate 59"),
        WGTP60(INT,"Housing Weight replicate 60"),
        WGTP61(INT,"Housing Weight replicate 61"),
        WGTP62(INT,"Housing Weight replicate 62"),
        WGTP63(INT,"Housing Weight replicate 63"),
        WGTP64(INT,"Housing Weight replicate 64"),
        WGTP65(INT,"Housing Weight replicate 65"),
        WGTP66(INT,"Housing Weight replicate 66"),
        WGTP67(INT,"Housing Weight replicate 67"),
        WGTP68(INT,"Housing Weight replicate 68"),
        WGTP69(INT,"Housing Weight replicate 69"),
        WGTP70(INT,"Housing Weight replicate 70"),
        WGTP71(INT,"Housing Weight replicate 71"),
        WGTP72(INT,"Housing Weight replicate 72"),
        WGTP73(INT,"Housing Weight replicate 73"),
        WGTP74(INT,"Housing Weight replicate 74"),
        WGTP75(INT,"Housing Weight replicate 75"),
        WGTP76(INT,"Housing Weight replicate 76"),
        WGTP77(INT,"Housing Weight replicate 77"),
        WGTP78(INT,"Housing Weight replicate 78"),
        WGTP79(INT,"Housing Weight replicate 79"),
        WGTP80(INT,"Housing Weight replicate 80");

        private final DataType columnType;
        private final String columnDescription;

        private HouseholdField(DataType columnType, String columnDescription) {
            this.columnType = columnType;
            this.columnDescription = columnDescription;
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
     * The {@code PersonField} enum holds the person fields for the 2007-2009 (2009 three-year) American Community 
     * Survey (ACS) PUMS data.
     */
    public enum PersonField implements AcsDataDictionary.AcsPersonField {
        RT(STRING,"Record Type"),
        SERIALNO(LONG,"Housing unit/GQ person serial number"),
        SPORDER(INT,"Person number"),
        PUMA(STRING,"Public use microdata area code (PUMA)"),
        ST(INT,"State Code"),
        ADJINC(INT,"Adjustment factor for income and earnings dollar amounts (6 implied decimal places)"),
        PWGTP(INT,"Person's weight"),
        AGEP(INT,"Age"),
        CIT(INT,"Citizenship status"),
        COW(INT,"Class of worker"),
        ENG(INT,"Ability to speak English"),
        FER(INT,"Child born within the past 12 months"),
        GCL(INT,"Grandchildren living in this house "),
        GCM(INT,"Months responsible for grandchildren"),
        GCR(INT,"Responsible for grandchildren"),
        INTP(INT,"Interest, dividends, and net rental income past 12 months (signed)"),
        JWMNP(INT,"Travel time to work"),
        JWRIP(INT,"Vehicle occupancy"),
        JWTR(INT,"Means of transportation to work"),
        LANX(INT,"Language other than English spoken at home"),
        MAR(INT,"Marital status"),
        MIG(INT,"Mobility status (lived here 1 year ago)"),
        MIL(INT,"Military service"),
        MLPA(INT,"Served September 2001 or later"),
        MLPB(INT,"Served August 1990 - August 2001 (including Persian Gulf War)"),
        MLPC(INT,"Served September 1980 - July 1990"),
        MLPD(INT,"Served May 1975 - August 1980"),
        MLPE(INT,"Served Vietnam era (August 1964 - April 1975)"),
        MLPF(INT,"Served March 1961 - July 1964"),
        MLPG(INT,"Served February 1955 - February 1961 "),
        MLPH(INT,"Served Korean War (July 1950 - January 1955)"),
        MLPI(INT,"Served January 1947 - June 1950"),
        MLPJ(INT,"Served World War II (December 1941 - December 1946)"),
        MLPK(INT,"Served November 1941 or earlier"),
        NWAB(INT,"Temporary absence from work (UNEDITED–See \"Employment Status Recode\" (ESR))"),
        NWAV(INT,"Available for work (UNEDITED–See \"Employment Status Recode\" (ESR))"),
        NWLA(INT,"On layoff from work (UNEDITED–See \"Employment Status Recode\" (ESR))"),
        NWLK(INT,"Looking for work (UNEDITED–See \"Employment Status Recode\" (ESR))"),
        NWRE(INT,"Informed of recall (UNEDITED–See \"Employment Status Recode\" (ESR))"),
        OIP(INT,"All other income past 12 months"),
        PAP(INT,"Public assistance income past 12 months"),
        REL(INT,"Relationship"),
        RETP(INT,"Retirement income past 12 months"),
        SCH(INT,"School enrollment"),
        SCHG(INT,"Grade level attending"),
        SCHL(INT,"Educational attainment"),
        SEMP(INT,"Self-employment income past 12 months (signed)"),
        SEX(INT,"Sex"),
        SSIP(INT,"Supplementary Security Income past 12 months"),
        SSP(INT,"Social Security income past 12 months"),
        WAGP(INT,"Wages or salary income past 12 months"),
        WKHP(INT,"Usual hours worked per week past 12 months"),
        WKL(INT,"When last worked"),
        WKW(INT,"Weeks worked during past 12 months"),
        YOEP(INT,"Year of entry"),
        ANC(INT,"Ancestry recode"),
        ANC1P(INT,"Year of entry Recoded Detailed Ancestry - first entry"),
        ANC2P(INT,"Recoded Detailed Ancestry - second entry"),
        DECADE(INT,"Decade of entry"),
        DRIVESP(INT,"Number of vehicles calculated from JWRI"),
        ESP(INT,"Employment status of parents"),
        ESR(INT,"Employment status recode"),
        HISP(INT,"Recoded detailed Hispanic origin"),
        INDP(INT,"Industry recode"),
        JWAP(INT,"Time of arrival at work - hour and minute"),
        JWDP(INT,"Time of departure for work - hour and minute"),
        LANP(INT,"Language spoken at home"),
        MIGPUMA(INT,"Migration PUMA"),
        MIGSP(INT,"Migration recode - State or foreign country code"),
        MSP(INT,"Married, spouse present/spouse absent"),
        NAICSP(INT,"NAICS Industry code"),
        NATIVITY(INT,"Nativity"),
        NOP(INT,"Nativity of parent"),
        OC(INT,"Own child"),
        OCCP(INT,"Occupation recode"),
        PAOC(INT,"Presence and age of own children"),
        PERNP(INT,"Total person's earnings"),
        PINCP(INT,"Total person's income (signed)"),
        POBP(INT,"Place of birth (Recode)"),
        POVPIP(INT,"Person poverty status recode"),
        POWPUMA(INT,"Place of work PUMA"),
        POWSP(INT,"Place of work - State or foreign country recode"),
        QTRBIR(INT,"Quarter of birth"),
        RAC1P(INT,"Recoded detailed race code"),
        RAC2P(INT,"Recoded detailed race code"),
        RAC3P(INT,"Recoded detailed race code"),
        RACAIAN(INT,"American Indian and Alaska Native recode (American Indian and Alaska Native alone or in combination with one or more other races)"),
        RACASN(INT,"Asian recode (Asian alone or in combination with one or more other races)"),
        RACBLK(INT,"Black or African American recode (Black alone or in combination with one or more other races)"),
        RACNHPI(INT,"Native Hawaiian and Other Pacific Islander recode (Native Hawaiian and Other Pacific Islander alone or in combination with one or more other races)"),
        RACNUM(INT,"Number of major race groups represented"),
        RACSOR(INT,"Some other race recode (Some other race alone or in combination with one or more other races)"),
        RACWHT(INT,"White recode (White alone or in combination with one or more other races)"),
        RC(INT,"Related child"),
        SFN(INT,"Subfamily number"),
        SFR(INT,"Subfamily relationship"),
        SOCP(INT,"SOC Occupation code"),
        VPS(INT,"Veteran period of service"),
        WAOB(INT,"World area of birth"),
        FAGEP(INT,"Age allocation flag"),
        FANCP(INT,"Ancestry allocation flag"),
        FCITP(INT,"Citizenship allocation flag"),
        FCOWP(INT,"Class of worker allocation flag"),
        FENGP(INT,"Ability to speak English allocation flag"),
        FESRP(INT,"Employment status recode allocation flag"),
        FFERP(INT,"Children born within the past 12 months allocation flag"),
        FGCLP(INT,"Grandchildren living in house allocation flag"),
        FGCMP(INT,"Months responsible for grandchildren allocation flag"),
        FGCRP(INT,"Responsible for grandchildren allocation flag"),
        FHISP(INT,"Detailed Hispanic origin allocation flag"),
        FINDP(INT,"Industry allocation flag"),
        FINTP(INT,"Interest, dividend, and net rental income allocation"),
        FJWDP(INT,"Time of departure to work allocation flag"),
        FJWMNP(INT,"Travel time to work allocation flag"),
        FJWRIP(INT,"Vehicle occupancy allocation flag"),
        FJWTRP(INT,"Means of transportation to work allocation flag"),
        FLANP(INT,"Language spoken at home allocation flag"),
        FLANXP(INT,"Language other than English allocation flag"),
        FMARP(INT,"Marital status allocation flag"),
        FMIGP(INT,"Mobility status allocation flag"),
        FMIGSP(INT,"Migration state allocation flag"),
        FMILPP(INT,"Military periods of service allocation flag"),
        FMILSP(INT,"Military service allocation flag"),
        FOCCP(INT,"Occupation allocation flag"),
        FOIP(INT,"All other income allocation flag"),
        FPAP(INT,"Public assistance income allocation flag"),
        FPOBP(INT,"Place of birth "),
        FPOWSP(INT,"Place of work state allocation flag"),
        FRACP(INT,"Detailed race allocation flag "),
        FRELP(INT,"Relationship allocation flag"),
        FRETP(INT,"Retirement income allocation flag"),
        FSCHGP(INT,"Grade attending allocation flag"),
        FSCHLP(INT,"Highest education allocation flag"),
        FSCHP(INT,"School enrollment allocation flag"),
        FSEMP(INT,"Self-employment income allocation flag"),
        FSEXP(INT,"Sex allocation flag"),
        FSSIP(INT,"Supplementary Security Income allocation flag"),
        FSSP(INT,"Social Security income allocation flag"),
        FWAGP(INT,"Wages and salary income allocation flag"),
        FWKHP(INT,"Usual hours worked per week past 12 months allocation flag"),
        FWKLP(INT,"Last worked allocation flag"),
        FWKWP(INT,"Weeks worked past 12 months allocation flag"),
        FYOEP(INT,"Year of entry allocation flag"),
        PWGTP1(INT,"Person's Weight replicate 1"),
        PWGTP2(INT,"Person's Weight replicate 2"),
        PWGTP3(INT,"Person's Weight replicate 3"),
        PWGTP4(INT,"Person's Weight replicate 4"),
        PWGTP5(INT,"Person's Weight replicate 5"),
        PWGTP6(INT,"Person's Weight replicate 6"),
        PWGTP7(INT,"Person's Weight replicate 7"),
        PWGTP8(INT,"Person's Weight replicate 8"),
        PWGTP9(INT,"Person's Weight replicate 9"),
        PWGTP10(INT,"Person's Weight replicate 10"),
        PWGTP11(INT,"Person's Weight replicate 11"),
        PWGTP12(INT,"Person's Weight replicate 12"),
        PWGTP13(INT,"Person's Weight replicate 13"),
        PWGTP14(INT,"Person's Weight replicate 14"),
        PWGTP15(INT,"Person's Weight replicate 15"),
        PWGTP16(INT,"Person's Weight replicate 16"),
        PWGTP17(INT,"Person's Weight replicate 17"),
        PWGTP18(INT,"Person's Weight replicate 18"),
        PWGTP19(INT,"Person's Weight replicate 19"),
        PWGTP20(INT,"Person's Weight replicate 20"),
        PWGTP21(INT,"Person's Weight replicate 21"),
        PWGTP22(INT,"Person's Weight replicate 22"),
        PWGTP23(INT,"Person's Weight replicate 23"),
        PWGTP24(INT,"Person's Weight replicate 24"),
        PWGTP25(INT,"Person's Weight replicate 25"),
        PWGTP26(INT,"Person's Weight replicate 26"),
        PWGTP27(INT,"Person's Weight replicate 27"),
        PWGTP28(INT,"Person's Weight replicate 28"),
        PWGTP29(INT,"Person's Weight replicate 29"),
        PWGTP30(INT,"Person's Weight replicate 30"),
        PWGTP31(INT,"Person's Weight replicate 31"),
        PWGTP32(INT,"Person's Weight replicate 32"),
        PWGTP33(INT,"Person's Weight replicate 33"),
        PWGTP34(INT,"Person's Weight replicate 34"),
        PWGTP35(INT,"Person's Weight replicate 35"),
        PWGTP36(INT,"Person's Weight replicate 36"),
        PWGTP37(INT,"Person's Weight replicate 37"),
        PWGTP38(INT,"Person's Weight replicate 38"),
        PWGTP39(INT,"Person's Weight replicate 39"),
        PWGTP40(INT,"Person's Weight replicate 40"),
        PWGTP41(INT,"Person's Weight replicate 41"),
        PWGTP42(INT,"Person's Weight replicate 42"),
        PWGTP43(INT,"Person's Weight replicate 43"),
        PWGTP44(INT,"Person's Weight replicate 44"),
        PWGTP45(INT,"Person's Weight replicate 45"),
        PWGTP46(INT,"Person's Weight replicate 46"),
        PWGTP47(INT,"Person's Weight replicate 47"),
        PWGTP48(INT,"Person's Weight replicate 48"),
        PWGTP49(INT,"Person's Weight replicate 49"),
        PWGTP50(INT,"Person's Weight replicate 50"),
        PWGTP51(INT,"Person's Weight replicate 51"),
        PWGTP52(INT,"Person's Weight replicate 52"),
        PWGTP53(INT,"Person's Weight replicate 53"),
        PWGTP54(INT,"Person's Weight replicate 54"),
        PWGTP55(INT,"Person's Weight replicate 55"),
        PWGTP56(INT,"Person's Weight replicate 56"),
        PWGTP57(INT,"Person's Weight replicate 57"),
        PWGTP58(INT,"Person's Weight replicate 58"),
        PWGTP59(INT,"Person's Weight replicate 59"),
        PWGTP60(INT,"Person's Weight replicate 60"),
        PWGTP61(INT,"Person's Weight replicate 61"),
        PWGTP62(INT,"Person's Weight replicate 62"),
        PWGTP63(INT,"Person's Weight replicate 63"),
        PWGTP64(INT,"Person's Weight replicate 64"),
        PWGTP65(INT,"Person's Weight replicate 65"),
        PWGTP66(INT,"Person's Weight replicate 66"),
        PWGTP67(INT,"Person's Weight replicate 67"),
        PWGTP68(INT,"Person's Weight replicate 68"),
        PWGTP69(INT,"Person's Weight replicate 69"),
        PWGTP70(INT,"Person's Weight replicate 70"),
        PWGTP71(INT,"Person's Weight replicate 71"),
        PWGTP72(INT,"Person's Weight replicate 72"),
        PWGTP73(INT,"Person's Weight replicate 73"),
        PWGTP74(INT,"Person's Weight replicate 74"),
        PWGTP75(INT,"Person's Weight replicate 75"),
        PWGTP76(INT,"Person's Weight replicate 76"),
        PWGTP77(INT,"Person's Weight replicate 77"),
        PWGTP78(INT,"Person's Weight replicate 78"),
        PWGTP79(INT,"Person's Weight replicate 79"),
        PWGTP80(INT,"Person's Weight replicate 80");

        private final DataType columnType;
        private final String columnDescription;

        private PersonField(DataType columnType, String columnDescription) {
            this.columnType = columnType;
            this.columnDescription = columnDescription;
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
