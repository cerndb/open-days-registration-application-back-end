//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class ArrivalPointModel {

    @JsonProperty("idArrivalPoint")
    public Integer idArrivalPoint;
    @JsonProperty("arrivalPointName")
    public String arrivalPointName;
    @JsonProperty("openTimeslots")
    public List<ArrivalPointOpenTimeslotModel> opentimeSlots;
    @JsonProperty("numberOfSurfaceActivities")
    public Integer numberOfSurfaceActivities;
    @JsonProperty("numberOfUndergroundActivities")
    public Integer numberOfUndergroundActivities;
    @JsonProperty("siteAccessibilityInfoURL")
    public String siteAccessibilityInfoURL;
    @JsonProperty("siteActivitiesInfoURL")
    public String siteActivitiesInfoURL;

    public ArrivalPointModel() {
        this.opentimeSlots = new ArrayList<>();
    }

    public ArrivalPointModel setIdArrivalPoint(Integer idArrivalPoint) {
        this.idArrivalPoint = idArrivalPoint;
        return this;
    }

    public ArrivalPointModel setArrivalPointName(String arrivalPointName) {
        this.arrivalPointName = arrivalPointName;
        return this;
    }

    public ArrivalPointModel setOpentimeSlots(List<ArrivalPointOpenTimeslotModel> opentimeSlots) {
        this.opentimeSlots = opentimeSlots;
        return this;
    }

    public ArrivalPointModel setNumberOfSurfaceActivities(Integer numberOfSurfaceActivities) {
        this.numberOfSurfaceActivities = numberOfSurfaceActivities;
        return this;
    }

    public ArrivalPointModel setNumberOfUndergroundActivities(Integer numberOfUndergroundActivities) {
        this.numberOfUndergroundActivities = numberOfUndergroundActivities;
        return this;
    }

    public Integer getIdArrivalPoint() {
        return idArrivalPoint;
    }

    public String getArrivalPointName() {
        return arrivalPointName;
    }

    public List<ArrivalPointOpenTimeslotModel> getOpentimeSlots() {
        return opentimeSlots;
    }

    public Integer getNumberOfSurfaceActivities() {
        return numberOfSurfaceActivities;
    }

    public Integer getNumberOfUndergroundActivities() {
        return numberOfUndergroundActivities;
    }

    public String getSiteAccessibilityInfoURL() {
        return siteAccessibilityInfoURL;
    }

    public ArrivalPointModel setSiteAccessibilityInfoURL(String siteAccessibilityInfoURL) {
        this.siteAccessibilityInfoURL = siteAccessibilityInfoURL;
        return this;
    }

    public String getSiteActivitiesInfoURL() {
        return siteActivitiesInfoURL;
    }

    public ArrivalPointModel setSiteActivitiesInfoURL(String siteActivitiesInfoURL) {
        this.siteActivitiesInfoURL = siteActivitiesInfoURL;
        return this;
    }
}
