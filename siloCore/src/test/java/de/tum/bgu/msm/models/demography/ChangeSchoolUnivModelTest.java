package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.data.Occupation;
import de.tum.bgu.msm.data.person.Gender;
import de.tum.bgu.msm.data.person.Person;
import de.tum.bgu.msm.data.person.PersonUtils;
import de.tum.bgu.msm.data.person.Race;
import de.tum.bgu.msm.properties.Properties;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChangeSchoolUnivModelTest {

    private static ChangeSchoolUnivModel model;
    private static SiloDataContainer dataContainer;

    @BeforeClass
    public static void setupModel() {
        SiloUtil.siloInitialization("./test/scenarios/annapolis/javaFiles/siloMstm.properties", Implementation.MARYLAND);
        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
        model = new ChangeSchoolUnivModel(dataContainer);
    }

    @Test
    public void testSchoolChange() {
        Person person = PersonUtils.getFactory().createPerson(0, 20, Gender.FEMALE, Race.other, Occupation.EMPLOYED, 0, 0);
        person.setSchoolPlace(10);
        Assert.assertEquals(0, person.getEducationLevel());
        model.updateEducation(person);
        Assert.assertEquals(2, person.getEducationLevel());
        Assert.assertEquals(0, person.getSchoolPlace());
    }
}
