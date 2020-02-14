//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Enums;

public enum MailActionCodes {
    LOGIN_PASSCODE(1),
    RESERVATION_FINISHED(2),
    RESERVATION_CANCELLED(3),
    RESERVATION_ARRIVAL_POINT_UPDATED(4),
    RESERVATION_TRANSPORT_UPDATED(5),
    RESERVATION_VISITOR_DETAILS_UPDATED(6);

    private final int mailActionCode;

    public int getMailActionCode() {
        return mailActionCode;
    }

    private MailActionCodes(int mailActionCode) {
        this.mailActionCode = mailActionCode;
    }

}
