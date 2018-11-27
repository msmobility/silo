package de.tum.bgu.msm.models.demography;

import com.vividsolutions.jts.geom.Coordinate;
import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.container.SiloModelContainer;
import de.tum.bgu.msm.data.dwelling.Dwelling;
import de.tum.bgu.msm.data.dwelling.DwellingType;
import de.tum.bgu.msm.data.dwelling.DwellingUtils;
import de.tum.bgu.msm.data.household.Household;
import de.tum.bgu.msm.data.household.HouseholdUtil;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.person.Occupation;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonUtils;
import de.tum.bgu.msm.data.person.Race;
import de.tum.bgu.msm.properties.Properties;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class EducationModelTest {

    private static EducationModel model;
    private static SiloDataContainer dataContainer;

    @BeforeClass
    public static void setupModel() {
        SiloUtil.siloInitialization(Implementation.MARYLAND, "./test/scenarios/annapolis/javaFiles/siloMstm.properties");
        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
        SiloModelContainer modelContainer = SiloModelContainer.createSiloModelContainer(dataContainer, null, Properties.get());
        model = modelContainer.getEducationUpdate();
    }

    @Test
    public void testSchoolChange() {
        Person person = PersonUtils.getFactory().createPerson(0, 20, Gender.FEMALE, Race.other, Occupation.EMPLOYED, 0, 0);
        person.setSchoolPlace(10);
        Assert.assertEquals(0, person.getEducationLevel());
        ((MstmEducationModelImpl)model).updateEducation(person);
        Assert.assertEquals(2, person.getEducationLevel());
        Assert.assertEquals(0, person.getSchoolPlace());
    }
}
