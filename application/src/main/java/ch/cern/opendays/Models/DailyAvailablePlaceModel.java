//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

import ch.cern.opendays.Constants.WorkflowConstants;
import ch.cern.opendays.Formatters.CustomDateFormatter;
import ch.cern.opendays.ModelsDAO.DailyFreeAvailablePlacesQueryDAO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class DailyAvailablePlaceModel {

    @JsonProperty("visitDay")
    public String getVisitDay() {
        return CustomDateFormatter.LanguageSpecificLocalDateTimePrinting(this.visitDay, this.responseLanguage);
    }

    @JsonProperty("ticketAvailable")
    public boolean getNumberOfFreeSpaces() {
        return numberOfFreeSpaces >= WorkflowConstants.SIX_INTEGER;
    }

    @JsonIgnore
    public LocalDateTime visitDay;
    @JsonIgnore
    public int numberOfFreeSpaces;
    @JsonIgnore
    public String responseLanguage;

    public DailyAvailablePlaceModel() {
    }

    public DailyAvailablePlaceModel(DailyFreeAvailablePlacesQueryDAO dailyData, String language) {
        this.responseLanguage = language;
        this.visitDay = dailyData.getVisitDay();
        this.numberOfFreeSpaces = dailyData.getAvailablePlaces();
    }


    public DailyAvailablePlaceModel setVisitDay(LocalDateTime visitDay) {
        this.visitDay = visitDay;
        return this;
    }

    public DailyAvailablePlaceModel setNumberOfFreeSpaces(int numberOfFreeSpaces) {
        this.numberOfFreeSpaces = numberOfFreeSpaces;
        return this;
    }

    public DailyAvailablePlaceModel setResponseLanguage(String responseLanguage) {
        this.responseLanguage = responseLanguage;
        return this;
    }
}
