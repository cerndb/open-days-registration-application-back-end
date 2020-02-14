//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class VisitorsDetailUpdateModel {

    @JsonProperty("visitorDetails")
    public List<VisitorDetailModel> visitorsDetails;

    @JsonProperty("groupHasDisabledPerson")
    public boolean groupHasDisabledPerson;

    @JsonProperty("numberOfAvailableFastTrackPlaces")
    public int getNumberOfAvailableFastTrackPlaces() {
        return numberOfAvailableFastTrackPlaces;
    }

    @JsonProperty("numberOfAvailablePlaces")
    public int getNumberOfAvailablePlaces() {
        return numberOfAvailablePlaces;
    }

    @JsonIgnore
    public int numberOfAvailableFastTrackPlaces;

    @JsonIgnore
    public int numberOfAvailablePlaces;

    public VisitorsDetailUpdateModel() {
        this.visitorsDetails = new ArrayList<>();
        this.groupHasDisabledPerson = false;
        this.numberOfAvailableFastTrackPlaces = 0;
        this.numberOfAvailablePlaces = 0;
    }

    public VisitorsDetailUpdateModel setGroupHasDisabledPerson(boolean groupHasDisabledPerson) {
        this.groupHasDisabledPerson = groupHasDisabledPerson;
        return this;
    }

    public VisitorsDetailUpdateModel setVisitorsDetails(List<VisitorDetailModel> visitorsDetails) {
        this.visitorsDetails = visitorsDetails;
        return this;
    }

    public VisitorsDetailUpdateModel setNumberOfAvailableFastTrackPlaces(int numberOfAvailableFastTrackPlaces) {
        this.numberOfAvailableFastTrackPlaces = numberOfAvailableFastTrackPlaces;
        return this;
    }

    public VisitorsDetailUpdateModel setNumberOfAvailablePlaces(int numberOfAvailablePlaces) {
        this.numberOfAvailablePlaces = numberOfAvailablePlaces;
        return this;
    }

}
