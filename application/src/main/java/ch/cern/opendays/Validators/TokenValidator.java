//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Validators;

import ch.cern.opendays.Constants.ControllerConstants;
import ch.cern.opendays.Constants.TokenConstants;

public class TokenValidator {

    // check if token is provided and not empty
    public static boolean checkIfTokenIsProvided(String tokenString){
        if (tokenString == null) {
            return false;
        }

        return !ControllerConstants.EMPTY_STRING.equals(tokenString.trim());
    }

    // check if token meets captacha criteria
    public static boolean checkTokenMeetsReCaptchaPattern(String tokenString) {
        return TokenConstants.RECAPTCHA_PATTERN.matcher(tokenString).matches();
    }
}
