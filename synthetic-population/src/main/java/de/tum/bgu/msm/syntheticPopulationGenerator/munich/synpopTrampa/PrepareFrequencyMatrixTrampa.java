package de.tum.bgu.msm.syntheticPopulationGenerator.munich.synpopTrampa;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import de.tum.bgu.msm.common.datafile.TableDataSet;
import de.tum.bgu.msm.syntheticPopulationGenerator.DataSetSynPop;
import de.tum.bgu.msm.syntheticPopulationGenerator.properties.PropertiesSynPop;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.IntStream;

public class PrepareFrequencyMatrixTrampa {

    private Integer[] ageBrackets = {17, 25, 65, 99};
    private Integer[] genderOptions = {2, 1};
    private Integer[] occupationOptions = {1, 2,3};
    private Integer[] hhIncomeOptions = {0, 300, 500, 700, 900, 1100, 1500, 2000, 2600, 3200, 3600, 4000, 4500, 5500, 6000, 9999};
    private Integer[] multHhIncomeOptions = {500, 900, 1500, 2000, 2600, 3200, 4500, 5500, 6000, 9999};
    private Integer[] singleHhIncomeOptions = {300, 700, 900, 1500, 2000, 2600, 3200, 4500, 9999};
    private Integer[] occHhIncomeOptions = {500, 900, 1500, 2000, 2600, 3200, 4500, 9999};

    private static final Logger logger = Logger.getLogger(PrepareFrequencyMatrixTrampa.class);

    private DataSetSynPop dataSetSynPop;
    private TableDataSet frequencyMatrix;

    public PrepareFrequencyMatrixTrampa(DataSetSynPop dataSetSynPop) {
        this.dataSetSynPop = dataSetSynPop;
    }

    public void run() {
        //create the frequency matrix with all the attributes aggregated at the household level
        logger.info("   Starting to create the frequency matrix");

        final HashSet<Integer> ages = Sets.newHashSet(ageBrackets);
        final HashSet<Integer> genders = Sets.newHashSet(genderOptions);
        final HashSet<Integer> occupations = Sets.newHashSet(occupationOptions);

        final Set<List<Integer>> combinations = Sets.cartesianProduct(Lists.newArrayList(ages, genders, occupations));

        final List<Household> households = new ArrayList<>();
        int hhCounter = 1;
        for (Integer incomeOption : hhIncomeOptions) {
            for (int j = 1; j <= 5; j++) {
                households.addAll(getHouseholds(j, incomeOption));
                final int size = households.size();
                System.out.println(size);
                hhCounter = size;
            }

        }


        initializeAttributesMunicipality(hhCounter);

        logger.info("Number of combinations: " + hhCounter);

        hhCounter = 1;
        int ppCounter = 1;

        for (Household household : households) {


            final int occ = (int) household.persons.stream().filter(p -> p.occupation == Person.Occupation.occupied).count();

            final Iterator<Person> personIterator = household.persons.iterator();
            final Person next = personIterator.next();


            if (household.persons.size() == 1) {
                int hhIncome = getIncomeBracket(household.income, singleHhIncomeOptions);
//
                Person.Age age = next.age;
                switch (age) {
                    case SEVENTEEN:
                    case TWENTYFIVE:
                        frequencyMatrix.setValueAt(hhCounter, "age25_single_inc" + hhIncome, 1);
                        break;
                    case SIXTYFIVE:
                        frequencyMatrix.setValueAt(hhCounter, "age65_single_inc" + hhIncome, 1);
                        break;
                    case NINETYNINE:
                        frequencyMatrix.setValueAt(hhCounter, "age99_single_inc" + hhIncome, 1);
                        break;
                    default:
                        throw new RuntimeException("Error");
                }

                int temp2 = getIncomeBracket(household.income, occHhIncomeOptions);
                if (next.occupation == Person.Occupation.occupied) {
                    frequencyMatrix.setValueAt(hhCounter, "single_occ_" + temp2, 1);
                }

                if (next.age == Person.Age.NINETYNINE && next.occupation == Person.Occupation.unoccupied) {
                    frequencyMatrix.setValueAt(hhCounter, "single_retired_" + temp2, 1);
                }
            } else {
                int hhIncome = getIncomeBracket(household.income, multHhIncomeOptions);

                frequencyMatrix.setValueAt(hhCounter, "multiple_inc" + hhIncome, 1);

                if (occ < 2) {

//                    int hhIncome = getIncomeBracket(household.income, occHhIncomeOptions);

                    if (occ > 0) {
                        int temp2 = getIncomeBracket(household.income, occHhIncomeOptions);
//                            frequencyMatrix.setValueAt(hhCounter, "mult_occ_" + temp2, 1);
                    }
                    Person.Age age = next.age;

                    switch (age) {
                        case SEVENTEEN:
                        case TWENTYFIVE:
//                                frequencyMatrix.setValueAt(hhCounter, "age25_multiple_one_inc" + hhIncome, 1);
                            break;
                        case SIXTYFIVE:
//                                frequencyMatrix.setValueAt(hhCounter, "age65_multiple_one_inc" + hhIncome, 1);
                            break;
                        case NINETYNINE:
//                                frequencyMatrix.setValueAt(hhCounter, "age99_multiple_one_inc" + hhIncome, 1);
                            break;
                        default:
                            throw new RuntimeException("Error");
                    }
                } else {
                    if (household.income > 0) {
//                        int hhIncome = getIncomeBracket(household.income, multHhIncomeOptions);

                        Person.Age age = next.age;

//                        if (occ > 0) {
//                            int temp2 = getIncomeBracket(incomeOption, occHhIncomeOptions);
//                            frequencyMatrix.setValueAt(hhCounter, "mult_occ_" + temp2, 1);
//                        }
//                        System.out.println(hhCounter + ": age" + age + "_multiple_multiple_inc" + hhIncome);
                        switch (age) {
                            case SEVENTEEN:
                                break;
                            case TWENTYFIVE:
//                                frequencyMatrix.setValueAt(hhCounter, "age25_multiple_multiple_inc" + hhIncome, 1);
                                break;
                            case SIXTYFIVE:
//                                frequencyMatrix.setValueAt(hhCounter, "age65_multiple_multiple_inc" + hhIncome, 1);
                                break;
                            case NINETYNINE:
//                                frequencyMatrix.setValueAt(hhCounter, "age99_multiple_multiple_inc" + hhIncome, 1);
                                break;
                            default:
                                throw new RuntimeException("Error");
                        }
                    }
                }
            }


            frequencyMatrix.setValueAt(hhCounter, "hhTotal", 1);
            frequencyMatrix.setValueAt(hhCounter, "hhSize" + Math.min(5, household.persons.size()), 1);
            if (PropertiesSynPop.get().main.boroughIPU) {
                if (household.persons.size() == 1) {
                    frequencyMatrix.setValueAt(hhCounter, "MUChhSize1", 1);
                }
            }

            final Table<Integer, String, Integer> householdTable = dataSetSynPop.getHouseholdTable();
            householdTable.put(hhCounter, "hhSize", household.persons.size());
            householdTable.put(hhCounter, "personCount", ppCounter);

            int childrenCounter = 0;
            for (Person person : household.persons) {

                Person.Age age = person.age;
                Person.Gender gender = person.gender;
                Person.Occupation occupation = person.occupation;

//
//                        if (occ > 0) {
//                            if (gender == 1) {
//                                int value = 1 + (int) frequencyMatrix.getValueAt(hhCounter, "m_occ_inc" + income);
//                                frequencyMatrix.setValueAt(hhCounter, "m_occ_inc" + income, value);
//                            } else {
//                                int value = 1 + (int) frequencyMatrix.getValueAt(hhCounter, "w_occ_inc" + income);
//                                frequencyMatrix.setValueAt(hhCounter, "w_occ_inc" + income, value);
//                            }
//                        }


                if (gender == Person.Gender.MALE) {
                    int value = 1 + (int) frequencyMatrix.getValueAt(hhCounter, "male" + age.code());
                    frequencyMatrix.setValueAt(hhCounter, "male" + age.code(), value);
                } else {
                    int value = 1 + (int) frequencyMatrix.getValueAt(hhCounter, "female" + age.code());
                    frequencyMatrix.setValueAt(hhCounter, "female" + age.code(), value);
                }

                if (PropertiesSynPop.get().main.boroughIPU) {
                    if (age.code() < 18) {
                        frequencyMatrix.setValueAt(hhCounter, "MUChhWithChildren", 1);
                    }
                    if (gender == Person.Gender.FEMALE) {
                        int value1 = 1 + (int) frequencyMatrix.getValueAt(hhCounter, "MUCfemale");
                        frequencyMatrix.setValueAt(hhCounter, "MUCfemale", value1);
                    }
                    int value = 1 + (int) frequencyMatrix.getValueAt(hhCounter, "MUCage" + age.code());
                    frequencyMatrix.setValueAt(household.persons.size(), "MUCage" + age.code(), value);
                }

                if (occupation == Person.Occupation.occupied) {
                    if (gender == Person.Gender.MALE) {
                        int value = 1 + (int) frequencyMatrix.getValueAt(hhCounter, "maleWorkers");
                        frequencyMatrix.setValueAt(hhCounter, "maleWorkers", value);
                    } else {
                        int value = 1 + (int) frequencyMatrix.getValueAt(hhCounter, "femaleWorkers");
                        frequencyMatrix.setValueAt(hhCounter, "femaleWorkers", value);
                    }
                    if (PropertiesSynPop.get().main.boroughIPU) {
                        if (gender == Person.Gender.MALE) {
                            int value = 1 + (int) frequencyMatrix.getValueAt(hhCounter, "MUCmaleWorkers");
                            frequencyMatrix.setValueAt(hhCounter, "MUCmaleWorkers", value);
                        } else {
                            int value = 1 + (int) frequencyMatrix.getValueAt(hhCounter, "MUCfemaleWorkers");
                            frequencyMatrix.setValueAt(hhCounter, "MUCfemaleWorkers", value);
                        }
                    }
                }



                dataSetSynPop.getPersonTable().put(ppCounter, "age", age.code());
                dataSetSynPop.getPersonTable().put(ppCounter, "gender", gender.code());
                dataSetSynPop.getPersonTable().put(ppCounter, "occupation", occupation.code());
                ppCounter++;
            }

            dataSetSynPop.getHouseholdTable().put(hhCounter, "income", household.income);


            int temp2 = getIncomeBracket(household.income, occHhIncomeOptions);
            if (temp2 > 0) {
//                            if (childrenCounter == 1) {
//                                if (!frequencyMatrix.containsColumn("1child_" + temp2)) {
//                                    System.out.println("jo");
//                                }
//                                frequencyMatrix.setValueAt(hhCounter, "1child_" + temp2, 1);
//                            } else if (childrenCounter == 2) {
//                                frequencyMatrix.setValueAt(hhCounter, "2child_" + temp2, 1);
//                            } else if (childrenCounter > 2) {
//                                frequencyMatrix.setValueAt(hhCounter, "3child_" + temp2, 1);
//                            }
            }

            frequencyMatrix.setValueAt(hhCounter, "population", household.persons.size());
            if (PropertiesSynPop.get().main.boroughIPU) {
                frequencyMatrix.setValueAt(hhCounter, "MUChhTotal", 1);
                frequencyMatrix.setValueAt(hhCounter, "MUCpopulation", household.persons.size());
            }
            hhCounter++;
        }


        dataSetSynPop.setFrequencyMatrix(frequencyMatrix);
        logger.info("   Finished creating the frequency matrix");
    }

    private List<Household> getHouseholds(int hhSize, int income) {
        List<Person> persons = new ArrayList<>();

        for (Person.Occupation occupation : Person.Occupation.values()) {
            for (Person.Age age : Person.Age.values()) {
                for (Person.Gender gender : Person.Gender.values()) {
                    if(age == Person.Age.SEVENTEEN &&
                    occupation != Person.Occupation.student) {
                        continue;
                    }
                    if(!(age == Person.Age.SEVENTEEN || age == Person.Age.TWENTYFIVE)
                    && occupation == Person.Occupation.student) {
                        continue;
                    }
                    persons.add(new Person(occupation, age, gender));
                }
            }
        }

        List<Household> result = new ArrayList<>();

        for (Person person : persons) {
            int remaining = Math.max(hhSize - 1, 0);
            Set<Set<Person>> combinations = Sets.combinations(Sets.newHashSet(persons), remaining);

            for (Set<Person> remainingPerson : combinations) {
                List<Person> personList = new ArrayList<>();
                personList.add(person);
                personList.addAll(remainingPerson);
                result.add(new Household(personList, income));
            }
        }

        return result;
    }

    private static class Person {
        enum Occupation {
            occupied {
                @Override
                int code() {
                    return 1;
                }
            }, unoccupied {
                @Override
                int code() {
                    return 2;
                }
            }, student {
                @Override
                int code() {
                    return 3;
                }
            };

            abstract int code();

        }

        enum Age {
            SEVENTEEN {
                @Override
                int code() {
                    return 17;
                }
            }, TWENTYFIVE {
                @Override
                int code() {
                    return 25;
                }
            }, SIXTYFIVE {
                @Override
                int code() {
                    return 65;
                }
            }, NINETYNINE {
                @Override
                int code() {
                    return 99;
                }
            };

            abstract int code();
        }


        enum Gender {
            MALE {
                @Override
                int code() {
                    return 1;
                }
            }, FEMALE {
                @Override
                int code() {
                    return 2;
                }
            };

            abstract int code();

        }

        Occupation occupation;
        Age age;
        Gender gender;

        public Person(Occupation occupation, Age age, Gender gender) {
            this.occupation = occupation;
            this.age = age;
            this.gender = gender;
        }

        @Override
        public String toString() {
            return occupation + " | " + age + " | " + gender;
        }
    }

    private final static class Household {
        List<Person> persons;
        int income;

        public Household(List<Person> persons, int income) {
            this.persons = persons;
            this.income = income;
        }
    }

    public int getIncomeBracket(int hhIncome, Integer[] incomeOptions) {
        int temp = 0;
        for (Integer incomeOption : incomeOptions) {
            temp = incomeOption;
            if (incomeOption >= hhIncome) {
                break;
            }
        }
        hhIncome = temp;
        return hhIncome;
    }


    private void initializeAttributesMunicipality(int hhCount) {
        //Method to create the list of attributes given the generic names and the brackets

        frequencyMatrix = new TableDataSet();
        frequencyMatrix.appendColumn(IntStream.range(1, hhCount + 1).toArray(), "id");
        for (String attribute : PropertiesSynPop.get().main.attributesMunicipality) {
            SiloUtil.addIntegerColumnToTableDataSet(frequencyMatrix, attribute);
        }
        if (PropertiesSynPop.get().main.twoGeographicalAreasIPU) {
            for (String attribute : PropertiesSynPop.get().main.attributesCounty) {
                SiloUtil.addIntegerColumnToTableDataSet(frequencyMatrix, attribute);
            }
        }
        if (PropertiesSynPop.get().main.boroughIPU) {
            for (String attribute : PropertiesSynPop.get().main.attributesBorough) {
                SiloUtil.addIntegerColumnToTableDataSet(frequencyMatrix, attribute);
            }
        }

        Table<Integer, String, Integer> table = HashBasedTable.create();
        dataSetSynPop.setHouseholdTable(table);

        Table<Integer, String, Integer> table2 = HashBasedTable.create();
        dataSetSynPop.setPersonTable(table2);
    }
}
