//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Enums;

public enum RegistrationProcessStatusCodes {
    INTERESTED(0),
    EMAIL_CONFIRMED(1),
    REGISTERED_ON_EVENTS(2),
    UNREGISTERED(3);

    private final int registrationProcessStatusCode;

    private RegistrationProcessStatusCodes(int registrationProcessStatusCode) {
        this.registrationProcessStatusCode = registrationProcessStatusCode;
    }

    public int getStatusCode() {
        return this.registrationProcessStatusCode;
    }
}
