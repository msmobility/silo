package de.tum.bgu.msm.run;

import org.matsim.core.config.ReflectiveConfigGroup;

public class CapeTownConfig extends ReflectiveConfigGroup {
	public static final String NAME = "CapeTown" ;

	public CapeTownConfig( ){
		super( NAME );
	}

	private RunType runType;
	public RunType getRunType() {
		return this.runType ;
	}
	public void setRunType( RunType runType ) {
		this.runType = runType ;
	}

	public enum RunType { base, improvedRail, reducedRail }
}
