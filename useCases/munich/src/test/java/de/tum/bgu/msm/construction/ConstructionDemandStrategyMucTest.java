package de.tum.bgu.msm.construction;

import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypeImpl;
import de.tum.bgu.msm.models.construction.ConstructionDemandStrategyMuc;
import org.junit.Assert;
import org.junit.Test;

public class ConstructionDemandStrategyMucTest {

    @Test
    public void testConstructionDemandStrategyMuc() {
        final ConstructionDemandStrategyMuc constructionDemandStrategyMuc = new ConstructionDemandStrategyMuc();
        Assert.assertEquals(0.035, constructionDemandStrategyMuc.calculateConstructionDemand(0.015, DefaultDwellingTypeImpl.SFD, 1), 0.00001);
        Assert.assertEquals(0.00465, constructionDemandStrategyMuc.calculateConstructionDemand(0.05, DefaultDwellingTypeImpl.SFD, 11000), 0.00001);
        Assert.assertEquals(0.01, constructionDemandStrategyMuc.calculateConstructionDemand(0.001, DefaultDwellingTypeImpl.SFD, 11000), 0.00001);
    }
}
