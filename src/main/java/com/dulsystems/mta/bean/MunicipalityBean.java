package com.dulsystems.mta.bean;

public class MunicipalityBean {
	
	private Integer municipalityId;
	private String municipalityName;
	private Integer stateIdFk;
	private String stateNameFk;
	
	public Integer getMunicipalityId() {
		return municipalityId;
	}
	public void setMunicipalityId(Integer municipalityId) {
		this.municipalityId = municipalityId;
	}
	public String getMunicipalityName() {
		return municipalityName;
	}
	public void setMunicipalityName(String municipalityName) {
		this.municipalityName = municipalityName;
	}
	public Integer getStateIdFk() {
		return stateIdFk;
	}
	public void setStateIdFk(Integer stateIdFk) {
		this.stateIdFk = stateIdFk;
	}
	public String getStateNameFk() {
		return stateNameFk;
	}
	public void setStateNameFk(String stateNameFk) {
		this.stateNameFk = stateNameFk;
	}

}
