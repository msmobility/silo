package de.tum.bgu.msm.models.demography;

import de.tum.bgu.msm.Implementation;
import de.tum.bgu.msm.data.person.*;
import de.tum.bgu.msm.utils.SiloUtil;
import de.tum.bgu.msm.container.SiloDataContainer;
import de.tum.bgu.msm.properties.Properties;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class LicenseTest {

    private static DriversLicense model;
    private static SiloDataContainer dataContainer;

    @BeforeClass
    public static void setupModel() {
        SiloUtil.siloInitialization(Implementation.MARYLAND, "./test/scenarios/annapolis/javaFiles/siloMstm.properties");
        dataContainer = SiloDataContainer.loadSiloDataContainer(Properties.get());
        model = new DriversLicense(dataContainer);
    }

    @Test
    public void testLicenseChange() {

        Person person = PersonUtils.getFactory().createPerson(0, 18, Gender.FEMALE, Race.other, Occupation.EMPLOYED, PersonRole.SINGLE, 0,0);
        Assert.assertFalse(person.hasDriverLicense());
        model.createLicense(person);
        Assert.assertTrue(person.hasDriverLicense());
    }
}
