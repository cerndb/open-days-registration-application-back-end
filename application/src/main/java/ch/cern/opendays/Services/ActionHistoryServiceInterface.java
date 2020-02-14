//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Enums.ReservationStatusCodes;
import ch.cern.opendays.Exceptions.ActionHistoryException;
import ch.cern.opendays.ModelsDAO.RegisteredPersonModelDAO;

public interface ActionHistoryServiceInterface {
    public void trackEmailConfirmationPasscodeGeneration(Long idRegistration, Integer idOperation) throws ActionHistoryException;
    public void trackShowInterestAction(RegisteredPersonModelDAO registeredPerson) throws ActionHistoryException;
    public void trackEmailIsConfirmedAction(RegisteredPersonModelDAO registeredPerson) throws ActionHistoryException;
    public void trackPasscodeValidationPassedTokenCreated(Long idRegistration) throws ActionHistoryException;
    public void trackUserConfirmedVisitorDetails(Long idReservation, Long idRegistration) throws ActionHistoryException;
    public void trackUserConfirmedVisitorTransportTypes(Long idReservation, Long idRegistration) throws ActionHistoryException;
    public void trackUserConfirmedArrivalPoint(Long idReservation, Long idRegistration) throws ActionHistoryException;
    public void trackUserCreatedNewReservation(Long idReservation, Long idRegistration) throws ActionHistoryException;
    public void trackReservationStatusChange(Long idReservation, Long idRegistration, ReservationStatusCodes statusChangeEnum) throws ActionHistoryException;
    public void trackUserUpdatedArrivalPoint(Long idReservation, Long idRegistration) throws ActionHistoryException;
    public void trackUserUpdatedVisitorTransportTypes(Long idReservation, Long idRegistration) throws ActionHistoryException;
    public void trackUserUpdatedVisitorDetails(Long idReservation, Long idRegistration) throws ActionHistoryException;
    public void trackUserRequestConfirmationMail(Long idReservation, Long idRegistration) throws ActionHistoryException;
}
