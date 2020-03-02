//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Validators;

import ch.cern.opendays.Constants.ControllerConstants;

public class StringValidator {

    public static boolean stringIsNotNull(String checkString) {
        return (checkString != null);
    }

    public static boolean stringIsNotEmpty(String checkString) {
        if (checkString == null) {
            return false;
        }
        return !ControllerConstants.EMPTY_STRING.equals(checkString.trim());
    }
}
