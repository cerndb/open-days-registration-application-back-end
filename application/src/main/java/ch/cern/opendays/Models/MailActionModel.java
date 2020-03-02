//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

public class MailActionModel {

    public Long idRegistration;
    public int actionType;
    public Long idReservation;

    public MailActionModel() {
    }

    public MailActionModel(Long idRegistration, Long idReservation, int actionType){
        this.idRegistration = idRegistration;
        this.idReservation = idReservation;
        this.actionType = actionType;
    }

    public MailActionModel setIdRegistration(Long idRegistration) {
        this.idRegistration = idRegistration;
        return this;
    }

    public MailActionModel setActionType(int actionType) {
        this.actionType = actionType;
        return this;
    }

    public MailActionModel setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
        return this;
    }

}
