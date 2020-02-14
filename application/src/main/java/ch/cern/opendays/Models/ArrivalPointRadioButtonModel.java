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
import java.time.LocalDate;

public class ArrivalPointRadioButtonModel {

    @JsonProperty("displayName")
    public String displayName;

    @JsonProperty("value")
    public String value;

    @JsonIgnore
    private LocalDate valueLocalInDateFormat;

    public ArrivalPointRadioButtonModel(String displayName, String value) {
        this.displayName = displayName;
        this.value = value;
    }

    public ArrivalPointRadioButtonModel(LocalDate radioDate, String language) {
        this.valueLocalInDateFormat = radioDate;
        this.value = radioDate.format(ControllerConstants.DATE_FORMAT_PATTERN);
        this.displayName = CustomDateFormatter.LanguageSpecificLocalDatePrinting(radioDate, language);
    }

    public ArrivalPointRadioButtonModel setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ArrivalPointRadioButtonModel setValue(String value) {
        this.value = value;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return value;
    }

    public LocalDate getValueLocalInDateFormat() {
        return valueLocalInDateFormat;
    }

    public ArrivalPointRadioButtonModel setValueLocalInDateFormat(LocalDate valueLocalInDateFormat) {
        this.valueLocalInDateFormat = valueLocalInDateFormat;
        return this;
    }

}
