/*
 * Copyright  2005 PB Consult Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package edu.umd.ncsg.realEstate;

import java.util.HashMap;
import org.apache.log4j.Logger;
import com.pb.common.calculator.IndexValues;


/**
 * Simulates renovation and deterioration of dwellings
 * Author: Rolf Moeckel, PB Albuquerque
 * Created on 7 Januar 2010 in Rhede
 **/

public class RenovationDMU {

    protected transient Logger logger = Logger.getLogger(RenovationDMU.class);
    protected HashMap<String, Integer> methodIndexMap;

    // uec variables
    private int quality;
    private IndexValues dmuIndex;


    public RenovationDMU() {
		dmuIndex = new IndexValues();
	}


    public void setQuality (int qualityType) {
    	this.quality = qualityType;
    }


    public IndexValues getDmuIndexValues() {
        return dmuIndex;
    }


    // DMU methods - define one of these for every @var in the mode choice control file.
	public int getQuality() {
		return quality;
	}
}