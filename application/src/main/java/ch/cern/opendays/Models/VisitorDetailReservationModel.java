//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

public class VisitorDetailReservationModel {

    private boolean groupHasDisabledPerson;
    private boolean fastTrackAllowed;

    public VisitorDetailReservationModel() {
    }

    public boolean getIsGroupHasDisabledPerson() {
        return groupHasDisabledPerson;
    }

    public boolean getIsFastTrackAllowed() {
        return fastTrackAllowed;
    }

    public VisitorDetailReservationModel setGroupHasDisabledPerson(boolean groupHasDisabledPerson) {
        this.groupHasDisabledPerson = groupHasDisabledPerson;
        return this;
    }

    public VisitorDetailReservationModel setFastTrackAllowed(boolean fastTrackAllowed) {
        this.fastTrackAllowed = fastTrackAllowed;
        return this;
    }

}
