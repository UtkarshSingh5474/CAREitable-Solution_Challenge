package com.example.careitable.dao;

public class CharitySuggestObject {

    String organisationName, organisationType,charitySector,charityDemographics;

    public CharitySuggestObject(String organisationName, String organisationType, String charitySector, String charityDemographics) {
        this.organisationName = organisationName;
        this.organisationType = organisationType;
        this.charitySector = charitySector;
        this.charityDemographics = charityDemographics;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(String organisationType) {
        this.organisationType = organisationType;
    }

    public String getCharitySector() {
        return charitySector;
    }

    public void setCharitySector(String charitySector) {
        this.charitySector = charitySector;
    }

    public String getCharityDemographics() {
        return charityDemographics;
    }

    public void setCharityDemographics(String charityDemographics) {
        this.charityDemographics = charityDemographics;
    }
}
