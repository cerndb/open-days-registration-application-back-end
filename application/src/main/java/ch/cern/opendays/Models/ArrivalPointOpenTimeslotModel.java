//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

import ch.cern.opendays.Constants.ControllerConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class ArrivalPointOpenTimeslotModel {

    @JsonProperty("idArrivalPoint")
    public Integer idArrivalPoint;
    @JsonProperty("availablePlaces")
    public Integer availablePlaces;
    @JsonProperty("isSelected")
    public boolean isSelected;
    @JsonProperty("timeslotStart")
    public String getTimeslotStart() {
        return this.timeslotStart.format(ControllerConstants.TIME_FORMAT_PATTERN_HH);
    }
    @JsonProperty("timeslotEnd")
    public String getTimeslotEnd() {
        return this.timeslotStart.plusMinutes(this.durration).format(ControllerConstants.TIME_FORMAT_PATTERN_HH);
    }

    @JsonIgnore
    public LocalDateTime timeslotStart;

    @JsonIgnore
    public Integer durration;

    @JsonIgnore
    public Integer availableFastTrackPlaces;

    public ArrivalPointOpenTimeslotModel(){
    }

    public ArrivalPointOpenTimeslotModel setIdArrivalPoint(Integer idArrivalPoint) {
        this.idArrivalPoint = idArrivalPoint;
        return this;
    }

    public ArrivalPointOpenTimeslotModel setAvailablePlaces(Integer availablePlaces) {
        this.availablePlaces = availablePlaces;
        return this;
    }

    public ArrivalPointOpenTimeslotModel setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
        return this;
    }

    public ArrivalPointOpenTimeslotModel setTimeslotStart(LocalDateTime timeslotStart) {
        this.timeslotStart = timeslotStart;
        return this;
    }

    public ArrivalPointOpenTimeslotModel setDurration(Integer durration) {
        this.durration = durration;
        return this;
    }

    public Integer getAvailableFastTrackPlaces() {
        return availableFastTrackPlaces;
    }

    public ArrivalPointOpenTimeslotModel setAvailableFastTrackPlaces(Integer availableFastTrackPlaces) {
        this.availableFastTrackPlaces = availableFastTrackPlaces;
        return this;
    }
}
