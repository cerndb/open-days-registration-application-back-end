//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Enums;

public enum ReservationStatusCodes {
    CREATED(1),
    IN_PROGRESS(2),
    FINAL(3),
    CANCELLED(4),
    IN_MODIFICATION(5),
    NO_STATUS(-1);

    private final int reservationStatusCode;

    private ReservationStatusCodes(int reservationStatusCode) {
        this.reservationStatusCode = reservationStatusCode;
    }

    public int getReservationStatusCode() {
        return reservationStatusCode;
    }

    public static ReservationStatusCodes getReservationStatus(int reservationStatusCodeSearch) {

        ReservationStatusCodes found = ReservationStatusCodes.NO_STATUS;
        for (ReservationStatusCodes reservationStatusCode : values()) {
            if (reservationStatusCode.getReservationStatusCode() == reservationStatusCodeSearch) {
                found = reservationStatusCode;
            }
        }
        return found;
    }

}
