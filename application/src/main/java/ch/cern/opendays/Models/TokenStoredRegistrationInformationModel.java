//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

import ch.cern.opendays.Constants.ControllerConstants;
import ch.cern.opendays.ModelsDAO.RegisteredPersonModelDAO;
import java.util.UUID;

public class TokenStoredRegistrationInformationModel {

    private Long idRegistration;
    private UUID personId;
    private int registrationStatusCode;
    private Long idReservation;

    public TokenStoredRegistrationInformationModel() {
    }

    public TokenStoredRegistrationInformationModel(RegisteredPersonModelDAO registerdPerson) {
        this.idRegistration = registerdPerson.getIdRegistration();
        this.registrationStatusCode = registerdPerson.getRegistrationProcessStatusCode();
        this.idReservation = ControllerConstants.DEFAULT_USER_ID;
    }

    public Long getIdRegistration() {
        return idRegistration;
    }

    public TokenStoredRegistrationInformationModel setIdRegistration(Long idRegistration) {
        this.idRegistration = idRegistration;
        return this;
    }

    public UUID getPersonId() {
        return personId;
    }

    public TokenStoredRegistrationInformationModel setPersonId(UUID personId) {
        this.personId = personId;
        return this;
    }

    public int getRegistrationStatusCode() {
        return registrationStatusCode;
    }

    public TokenStoredRegistrationInformationModel setRegistrationStatusCode(int registrationStatusCode) {
        this.registrationStatusCode = registrationStatusCode;
        return this;
    }

    public Long getIdReservation() {
        return idReservation;
    }

    public TokenStoredRegistrationInformationModel setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
        return this;
    }
}
