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

public class RequestArrivalPointTimeslotsModel {

    @JsonProperty("selectedArrivalDate")
    public void setSelectedArrivalDate(String selectedArrivalDate) {
        try {
            this.selectedArrivalDate = LocalDateTime.parse(selectedArrivalDate  + " " + ControllerConstants.DEFAULT_TIME_STRING, ControllerConstants.DATETIME_FORMAT_PATTERN);
        } catch (Exception ex) {
            this.selectedArrivalDate = LocalDateTime.parse(ControllerConstants.DEFAULT_DATE_STRING + " " + ControllerConstants.DEFAULT_TIME_STRING, ControllerConstants.DATETIME_FORMAT_PATTERN);
        }
    }

    @JsonIgnore
    public LocalDateTime selectedArrivalDate;

}
