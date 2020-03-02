//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Enums;

public enum UserValidationTypesCodes {
    NOT_DEFINED(0),
    RECAPTCHA_TOKEN(1),
    HONEYPOT(2);

    private final int userValidationTypeCode;

    private UserValidationTypesCodes(int userValidationTypeCode) {
        this.userValidationTypeCode = userValidationTypeCode;
    }

    public int getUserValidationTypeCode() {
        return userValidationTypeCode;
    }

    public static boolean checkTypeCodeIsValid(int userValidationTypeCode) {
        UserValidationTypesCodes found = NOT_DEFINED;
        for (UserValidationTypesCodes typeCode : values())
            if (typeCode.userValidationTypeCode == userValidationTypeCode)
                found = typeCode;
        return (found != NOT_DEFINED);
    }

    public static UserValidationTypesCodes getUserValidationTypeCode(int userValidationTypeCode) {
        UserValidationTypesCodes found = NOT_DEFINED;
        for (UserValidationTypesCodes typeCode : values())
            if (typeCode.userValidationTypeCode == userValidationTypeCode)
                found = typeCode;
        return found;
    }
}
