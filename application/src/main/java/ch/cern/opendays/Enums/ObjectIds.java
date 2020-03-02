//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Enums;

public enum ObjectIds {
    REGISTRATION_OBJECT(0),
    PASSCODE_OBJECT(1),
    VISITOR_DETAILS_OBJECTS(2),
    VISITOR_TRANSPORT_TYPES(3),
    RESERVATION_OBJECT(4),
    MAIL_OBJECT(5);

    private final int objectId;

    private ObjectIds(int objectId) {
        this.objectId = objectId;
    }

    public int getObjectId() {
        return objectId;
    }

}
