package de.tum.bgu.msm.construction;

import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes;
import de.tum.bgu.msm.models.construction.ConstructionDemandStrategyMuc;
import org.junit.Assert;
import org.junit.Test;

public class ConstructionDemandStrategyMucTest {

    @Test
    public void testConstructionDemandStrategyMuc() {
        final ConstructionDemandStrategyMuc constructionDemandStrategyMuc = new ConstructionDemandStrategyMuc();
        Assert.assertEquals(0.0149999, constructionDemandStrategyMuc.calculateConstructionDemand(0.015, DefaultDwellingTypes.DefaultDwellingTypeImpl.SFD, 1), 0.00001);
        Assert.assertEquals(0, constructionDemandStrategyMuc.calculateConstructionDemand(0.05, DefaultDwellingTypes.DefaultDwellingTypeImpl.SFD, 11000), 0.00001);
        Assert.assertEquals(0.01, constructionDemandStrategyMuc.calculateConstructionDemand(0.001, DefaultDwellingTypes.DefaultDwellingTypeImpl.SFD, 11000), 0.00001);
    }
}
