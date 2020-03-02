//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Validators;

import ch.cern.opendays.Constants.ControllerConstants;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateValidator {
    public static boolean DateIsDefault(LocalDate checkDate) {
        return (checkDate.compareTo(LocalDate.parse(ControllerConstants.DEFAULT_DATE_STRING, ControllerConstants.DATE_FORMAT_PATTERN)) != 0);
    }

    public static boolean DateTimeIsDefault(LocalDateTime checkTime) {
        return (checkTime.compareTo(LocalDateTime.parse(ControllerConstants.DEFAULT_DATE_STRING + " " + ControllerConstants.DEFAULT_TIME_STRING, ControllerConstants.DATETIME_FORMAT_PATTERN)) != 0);
    }
}
