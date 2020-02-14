//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Enums;

public enum UserActions {
    USER_SHOWS_INTEREST(1),
    SYSTEM_PASSCODE_GENERATION(2),
    USER_CONFIRMS_EMAIL_ADDRESS(3),
    PASSCODE_VALIDATED_TOKEN_CREATED(4),
    VISITOR_DETAILS_CONFIRMED(5),
    VISITOR_TRANSPORT_TYPES_CONFIRMED(6),
    ARRIVAL_POINT_CONFIRMED(7),
    NEW_RESERVATION(8),
    RESERVATION_STATUS_FINAL(9),
    RESERVATION_STATUS_CANCELLED(10),
    UPDATE_ARRIVAL_POINT(11),
    UPDATE_TRANSPORT_TYPES(12),
    UPDATE_VISITOR_DETAILS(13),
    USER_RE_REQUEST_CONFIRMATION_MAIL(14);

    private final int userAction;

    private UserActions(int userAction) {
        this.userAction = userAction;
    }

    public int getUserAction() {
        return userAction;
    }
}
