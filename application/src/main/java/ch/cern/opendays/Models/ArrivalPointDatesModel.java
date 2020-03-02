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

public class ArrivalPointDatesModel {

    @JsonProperty("arrivalDates")
    public List<ArrivalPointRadioButtonModel> arrivalDates;

    @JsonProperty("registrationPeriodIsActive")
    public Boolean registrationPeriodIsActive;

    @JsonProperty("privilegedVisitor")
    public Boolean privilegedVisitor;

    public ArrivalPointDatesModel setArrivalDates(List<ArrivalPointRadioButtonModel> arrivalDates) {
        this.arrivalDates = arrivalDates;
        return this;
    }

    public ArrivalPointDatesModel setRegistrationPeriodIsActive(Boolean registrationPeriodIsActive) {
        this.registrationPeriodIsActive = registrationPeriodIsActive;
        return this;
    }

    public ArrivalPointDatesModel() {
        this.arrivalDates = new ArrayList<>();
    }

    public ArrivalPointDatesModel setPrivilegedVisitor(Boolean privilegedVisitor) {
        this.privilegedVisitor = privilegedVisitor;
        return this;
    }

}
