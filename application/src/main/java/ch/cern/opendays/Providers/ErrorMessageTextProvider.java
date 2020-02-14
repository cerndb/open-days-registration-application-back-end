//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Providers;

import ch.cern.opendays.Constants.EnvironmentConfigConstants;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Enums.SupportedLanguages;
import java.util.Locale;
import java.util.ResourceBundle;

public class ErrorMessageTextProvider {

    // based on language selection return error message
    public static String getErrorMessageText(int errorMessageStatusCode, String responseLanguage) {

        // if selected language is not in the list select english by defualt
        String language = SupportedLanguages.getSupportedLanguage(responseLanguage).toString();

        // define target resource file from where we should read
        ResourceBundle messages = ResourceBundle.getBundle(EnvironmentConfigConstants.I18N_LOCATION, new Locale(language));

        // return the error message based on error message code
        // at this point we assume that we have already referenced the error code on a higher level, so it should be part of the enum
        return messages.getString(MessageStatusCodes.getStatusCode(errorMessageStatusCode).toString());
    }
}
