//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

import ch.cern.opendays.Constants.ControllerConstants;
import ch.cern.opendays.Formatters.CustomDateFormatter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationSummaryModel {

    @JsonIgnore
    public LocalDateTime visitDay;

    @JsonIgnore
    public LocalDateTime arrivalTimeslotStart;

    @JsonIgnore
    public LocalDateTime arrivalTimeslotEnd;

    @JsonIgnore
    public Integer idArrivalPoint;

    @JsonProperty("idReservation")
    public Long idReservation;

    @JsonProperty("nameArrivalPoint")
    public String nameArrivalPoint;

    @JsonProperty("numberOfBookedStandardTickets")
    public Integer numberOfBookedStandardTickets;

    @JsonProperty("numberOfBookedFastTrackTickets")
    public Integer numberOfBookedFastTrackTickets;

    @JsonProperty("transportTypes")
    public List<TransportTypeModel> transportTypes;

    @JsonIgnore
    private String selectedLanguage;

    @JsonProperty("groupHasDisabledMobility")
    public Boolean groupHasDisabledMobility;

    @JsonProperty("pointOfOriginDisplayName")
    public String pointOfOriginDisplayName;

    @JsonProperty("visitDay")
    public String getVisitDay() {
        if(this.visitDay == null){
            return null;
        }
        return CustomDateFormatter.LanguageSpecificLocalDateTimePrinting(this.visitDay, this.selectedLanguage);
    }

    @JsonProperty("arrivalTimeslotStart")
    public String getArrivalTimeslotStart() {
        return (this.arrivalTimeslotStart != null) ? this.arrivalTimeslotStart.format(ControllerConstants.TIME_FORMAT_PATTERN_HH) : null;
    }

    @JsonProperty("arrivalTimeslotEnd")
    public String getArrivalTimeslotEnd() {
        return (this.arrivalTimeslotEnd != null) ? this.arrivalTimeslotEnd.format(ControllerConstants.TIME_FORMAT_PATTERN_HH) : null;
    }

    public ReservationSummaryModel() {
        this.transportTypes = new ArrayList<>();
    }

    public ReservationSummaryModel setVisitDay(LocalDateTime visitDay) {
        this.visitDay = visitDay;
        return this;
    }

    public ReservationSummaryModel setArrivalTimeslotStart(LocalDateTime arrivalTimeslotStart) {
        this.arrivalTimeslotStart = arrivalTimeslotStart;
        return this;
    }

    public ReservationSummaryModel setArrivalTimeslotEnd(LocalDateTime arrivalTimeslotEnd) {
        this.arrivalTimeslotEnd = arrivalTimeslotEnd;
        return this;
    }

    public ReservationSummaryModel setNumberOfBookedStandardTickets(Integer numberOfBookedStandardTickets) {
        this.numberOfBookedStandardTickets = numberOfBookedStandardTickets;
        return this;
    }

    public ReservationSummaryModel setTransportTypes(List<TransportTypeModel> transportTypes) {
        this.transportTypes = transportTypes;
        return this;
    }

    public ReservationSummaryModel setNumberOfBookedFastTrackTickets(Integer numberOfBookedFastTrackTickets) {
        this.numberOfBookedFastTrackTickets = numberOfBookedFastTrackTickets;
        return this;
    }

    public ReservationSummaryModel setNameArrivalPoint(String nameArrivalPoint) {
        this.nameArrivalPoint = nameArrivalPoint;
        return this;
    }

    public ReservationSummaryModel setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
        return this;
    }

    public List<TransportTypeModel> getTransportTypes() {
        return transportTypes;
    }

    public ReservationSummaryModel setSelectedLanguage(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
        return this;
    }

    public ReservationSummaryModel setGroupHasDisabledMobility(Boolean groupHasDisabledMobility) {
        this.groupHasDisabledMobility = groupHasDisabledMobility;
        return this;
    }

    public ReservationSummaryModel setIdArrivalPoint(Integer idArrivalPoint) {
        this.idArrivalPoint = idArrivalPoint;
        return this;
    }

    public ReservationSummaryModel setPointOfOriginDisplayName(String pointOfOriginDisplayName) {
        this.pointOfOriginDisplayName = pointOfOriginDisplayName;
        return this;
    }

}
