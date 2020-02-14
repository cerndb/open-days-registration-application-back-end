//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Formatters;

import ch.cern.opendays.Constants.ControllerConstants;
import ch.cern.opendays.Enums.SupportedLanguages;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomDateFormatter {

    public static String LanguageSpecificLocalDateTimePrinting(LocalDateTime printDateTime, String targetLanguage) {
        String returnDateTime = "";

        if (SupportedLanguages.getSupportedLanguage(targetLanguage) == SupportedLanguages.fr) {
            returnDateTime = printDateTime.format(ControllerConstants.DATE_DAY_OF_WEEK_DAY_MONTH_FORMAT_FR).toLowerCase();
            returnDateTime = returnDateTime.substring(0, 1).toUpperCase() + returnDateTime.substring(1).toLowerCase();

        } else {
            returnDateTime = printDateTime.format(ControllerConstants.DATE_DAY_OF_WEEK_DAY_MONTH_FORMAT_EN);
        }

        return returnDateTime;
    }

    public static String LanguageSpecificLocalDatePrinting(LocalDate printDate, String targetLanguage) {
        String retrunDate = "";

        if (SupportedLanguages.getSupportedLanguage(targetLanguage) == SupportedLanguages.fr) {
            retrunDate = printDate.format(ControllerConstants.DATE_DAY_OF_WEEK_DAY_MONTH_FORMAT_FR).toLowerCase();
            retrunDate = retrunDate.substring(0, 1).toUpperCase() + retrunDate.substring(1).toLowerCase();

        } else {
            retrunDate = printDate.format(ControllerConstants.DATE_DAY_OF_WEEK_DAY_MONTH_FORMAT_EN);
        }

        return retrunDate;
    }

}
