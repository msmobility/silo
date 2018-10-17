package de.tum.bgu.msm.data.household;

import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.data.HouseholdDataManager;
import de.tum.bgu.msm.data.person.*;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

import static de.tum.bgu.msm.data.household.HouseholdType.*;
import static de.tum.bgu.msm.data.household.IncomeCategory.*;

public class HouseholdUtil {

    private final static Logger logger = Logger.getLogger(HouseholdUtil.class);

    private final static HouseholdFactory factory = new HouseholdFactoryImpl();

    private HouseholdUtil() {
    }

    public static HouseholdFactory getFactory() {
        return factory;
    }

    public static int getNumberOfWorkers(Household household) {
        return (int) household.getPersons().values().stream().filter(p -> p.getOccupation() == Occupation.EMPLOYED).count();
    }

    public static int getHHLicenseHolders(Household household) {
        return (int) household.getPersons().values().stream().filter(Person::hasDriverLicense).count();
    }

    public static int getHhIncome(Household household) {
        int hhInc = 0;
        for (Person i : household.getPersons().values()) {
            hhInc += i.getIncome();
        }
        return hhInc;
    }

    public static boolean checkIfOnlyChildrenRemaining(Household household) {
        if (household.getPersons().isEmpty()) {
            return false;
        }
        for (Person pp : household.getPersons().values()) {
            if (pp.getAge() >= 16) {
                return false;
            }
        }
        return true;
    }

    public static HouseholdType defineHouseholdType(Household household) {
        // define household type based on size and income

        int hhSize = household.getHhSize();
        IncomeCategory incomeCategory = HouseholdDataManager.getIncomeCategoryForIncome(HouseholdUtil.getHhIncome(household));

        HouseholdType ht = null;
        if (hhSize == 1) {
            if (incomeCategory == LOW) ht = SIZE_1_INC_LOW;
            else if (incomeCategory == MEDIUM) ht = SIZE_1_INC_MEDIUM;
            else if (incomeCategory == HIGH) ht = SIZE_1_INC_HIGH;
            else ht = SIZE_1_INC_VERY_HIGH;
        } else if (hhSize == 2) {
            if (incomeCategory == LOW) ht = SIZE_2_INC_LOW;
            else if (incomeCategory == MEDIUM) ht = SIZE_2_INC_MEDIUM;
            else if (incomeCategory == HIGH) ht = SIZE_2_INC_HIGH;
            else ht = SIZE_2_INC_VERY_HIGH;
        } else if (hhSize == 3) {
            if (incomeCategory == LOW) ht = SIZE_3_INC_LOW;
            else if (incomeCategory == MEDIUM) ht = SIZE_3_INC_MEDIUM;
            else if (incomeCategory == HIGH) ht = SIZE_3_INC_HIGH;
            else ht = SIZE_3_INC_VERY_HIGH;
        } else if (hhSize > 3) {
            if (incomeCategory == LOW) ht = SIZE_4_INC_LOW;
            else if (incomeCategory == MEDIUM) ht = SIZE_4_INC_MEDIUM;
            else if (incomeCategory == HIGH) ht = SIZE_4_INC_HIGH;
            else ht = SIZE_4_INC_VERY_HIGH;
        }
        return ht;
    }

    public static Race defineHouseholdRace(Household household) {
        Race householdRace = null;
        for (Person pp : household.getPersons().values()) {
            if (householdRace == null) {
                householdRace = pp.getRace();
            } else if (pp.getRace() != householdRace) {
                return Race.other;
            }
        }
        return householdRace;
    }

    public static Person findMostLikelyUnmarriedPartner (Person per, Household hh) {
        // when assigning roles to persons, look for likely partner in household that is not married yet

        Person selectedPartner = null;
        double highestUtil = Double.NEGATIVE_INFINITY;
        double tempUtil;
        for (Person partner: hh.getPersons().values()) {
            if (partner.getGender() != per.getGender() && partner.getRole() != PersonRole.MARRIED) {
                int ageDiff = Math.abs(per.getAge() - partner.getAge());
                if (ageDiff == 0) {
                    tempUtil = 2;
                } else {
                    tempUtil = 1f / (float) ageDiff;
                }
                if (tempUtil > highestUtil) {
                    selectedPartner = partner;     // find most likely partner
                }
            }
        }
        return selectedPartner;
    }

    public static Person findMostLikelyPartner(Person per, Household hh) {
        // find married partner that fits best for person per
        double highestUtil = Double.NEGATIVE_INFINITY;
        double tempUtil;
        Person selectedPartner = null;
        for(Person partner: hh.getPersons().values()) {
            if (!partner.equals(per) && partner.getGender() != per.getGender() && partner.getRole() == PersonRole.MARRIED) {
                final int ageDiff = Math.abs(per.getAge() - partner.getAge());
                if (ageDiff == 0) {
                    tempUtil = 2.;
                } else  {
                    tempUtil = 1. / ageDiff;
                }
                if (tempUtil > highestUtil) {
                    highestUtil = tempUtil;
                    selectedPartner = partner;     // find most likely partner
                }
            }
        }
        if (selectedPartner == null) {
            logger.error("Could not find spouse of person " + per.getId() + " in household " + hh.getId());
            for (Person person: hh.getPersons().values()) {
                logger.error("Houshold member " + person.getId() + " (gender: " + person.getGender() + ") is " +
                        person.getRole());
            }
        }
        return selectedPartner;
    }

    public static void defineUnmarriedPersons (Household hh) {
        // For those that did not become the married couple define role in household (child or single)
        for (Person pp: hh.getPersons().values()) {
            if (pp.getRole() == PersonRole.MARRIED) {
                continue;
            }
            boolean someone15to40yearsOlder = false;      // assumption that this person is a parent
            final int ageMain = pp.getAge();
            for (Person per: hh.getPersons().values()) {
                if (pp.equals(per)) {
                    continue;
                }
                int age = per.getAge();
                if (age >= ageMain + 15 && age <= ageMain + 40) {
                    someone15to40yearsOlder = true;
                }
            }
            if ((someone15to40yearsOlder && ageMain < 50) || ageMain <= 15) {
                pp.setRole(PersonRole.CHILD);
            } else {
                pp.setRole(PersonRole.SINGLE);
            }
            if (pp.getId() == SiloUtil.trackPp || pp.getHousehold().getId() == SiloUtil.trackHh) {
                SiloUtil.trackWriter.println("Defined role of person " + pp.getId() + " in household "
                        + pp.getHousehold().getId() + " as " + pp.getRole());
            }
        }
    }

    public static void findMarriedCouple(Household hh) {
        List<Person> personsCopy = hh.getPersons().values().stream().sorted(new PersonUtils.PersonByAgeComparator()).collect(Collectors.toList());

        for (Person person: personsCopy) {
            Person partner = HouseholdUtil.findMostLikelyUnmarriedPartner(person, hh);
            if (partner != null) {
                partner.setRole(PersonRole.MARRIED);
                person.setRole(PersonRole.MARRIED);
                if (person.getId() == SiloUtil.trackPp || person.getHousehold().getId() == SiloUtil.trackHh) {
                    SiloUtil.trackWriter.println("Defined role of person  " + person.getId() + " in household "
                            + person.getHousehold().getId() +
                            " as " + person.getRole());
                }
                if (partner.getId() == SiloUtil.trackPp || partner.getHousehold().getId() == SiloUtil.trackHh) {
                    SiloUtil.trackWriter.println("Defined role of partner " + partner.getId() + " in household "
                            + partner.getHousehold().getId() +
                            " as " + partner.getRole());
                }
                return;
            }
        }
    }

    public static Nationality defineHouseholdNationality(Household household) {
        Nationality householdNationaliy = null;
        for (Person pp : household.getPersons().values()) {
            if (householdNationaliy == null) {
                householdNationaliy = pp.getNationality();
            } else if (pp.getNationality() != householdNationaliy) {
                return Nationality.OTHER;
            }
        }
        return householdNationaliy;
    }
}
