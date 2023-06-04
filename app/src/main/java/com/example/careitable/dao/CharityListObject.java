package com.example.careitable.dao;

public class CharityListObject {
    String organisationName, organisationDescription,organisationState,organisationCity, charityID;

    public CharityListObject(String organisationName, String organisationShortDescription, String organisationState, String organisationCity, String charityID) {
        this.organisationName = organisationName;
        this.organisationDescription = organisationShortDescription;
        this.organisationState = organisationState;
        this.organisationCity = organisationCity;
        this.charityID = charityID;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getOrganisationDescription() {
        return organisationDescription;
    }

    public void setOrganisationDescription(String organisationDescription) {
        this.organisationDescription = organisationDescription;
    }

    public String getOrganisationState() {
        return organisationState;
    }

    public void setOrganisationState(String organisationState) {
        this.organisationState = organisationState;
    }

    public String getOrganisationCity() {
        return organisationCity;
    }

    public void setOrganisationCity(String organisationCity) {
        this.organisationCity = organisationCity;
    }

    public String getCharityID() {
        return charityID;
    }

    public void setCharityID(String charityID) {
        this.charityID = charityID;
    }
}
