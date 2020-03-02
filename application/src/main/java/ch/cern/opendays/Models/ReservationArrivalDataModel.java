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

public class ReservationArrivalDataModel {

    @JsonIgnore
    public LocalDateTime timeslotStartDateTime;

    @JsonProperty("visitdayDetails")
    public ArrivalPointRadioButtonModel visitdayDetails;
    @JsonProperty("idArrivalPoint")
    public Integer idArrivalPoint;
    @JsonProperty("timeslotStart")
    public String getTimeslotStartDateTime() {
        return timeslotStartDateTime.format(ControllerConstants.TIME_FORMAT_PATTERN_HH);
    }

    public ReservationArrivalDataModel() {
    }

    public ReservationArrivalDataModel(ReservationSummaryModel summaryModel, String responseLanguage) {
        this.visitdayDetails = new ArrivalPointRadioButtonModel(summaryModel.visitDay.toLocalDate(), responseLanguage);
        this.timeslotStartDateTime = summaryModel.arrivalTimeslotStart;
        this.idArrivalPoint = summaryModel.idArrivalPoint;

    }

    public ReservationArrivalDataModel setVisitdayDetails(ArrivalPointRadioButtonModel visitdayDetails) {
        this.visitdayDetails = visitdayDetails;
        return this;
    }

    public ReservationArrivalDataModel setIdArrivalPoint(Integer idArrivalPoint) {
        this.idArrivalPoint = idArrivalPoint;
        return this;
    }

    public ReservationArrivalDataModel setTimeslotStartDateTime(LocalDateTime timeslotStartDateTime) {
        this.timeslotStartDateTime = timeslotStartDateTime;
        return this;
    }

}
