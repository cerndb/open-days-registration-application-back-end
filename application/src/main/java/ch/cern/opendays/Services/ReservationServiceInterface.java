//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.Models.ConfirmArrivalPointModel;
import ch.cern.opendays.Models.ReservationSummariesModel;
import ch.cern.opendays.Models.ReservationSummaryModel;
import ch.cern.opendays.Models.StoreVisitorDetailsModel;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;
import ch.cern.opendays.Models.VisitorDetailReservationModel;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Set;

public interface ReservationServiceInterface {
   public TokenStoredRegistrationInformationModel createNewReservation(
         TokenStoredRegistrationInformationModel tokenInformation)
         throws DatabaseException, OutOfWorkflowScopeException;

   public void reservationUpdateWithArrivalPoint(ConfirmArrivalPointModel confirmArrival,
         TokenStoredRegistrationInformationModel tokenInformation)
         throws DatabaseException, OutOfWorkflowScopeException;

    public void reservationUpdateArrivalPoint(ConfirmArrivalPointModel confirmArrival,
            TokenStoredRegistrationInformationModel tokenInformation)
            throws DatabaseException, OutOfWorkflowScopeException;

   public void storeVisitorDetails(TokenStoredRegistrationInformationModel tokenInformation,
         StoreVisitorDetailsModel visitorDetails) throws DatabaseException, OutOfWorkflowScopeException;

    public void updateReservationStatus(Long idReservation, Integer reservationStatus, Long IdRegistration)
            throws DatabaseException, OutOfWorkflowScopeException;

   public ReservationSummariesModel getRegistrationLinkedReservationSummaries(Long idRegistration,
         String selectedLanguage) throws DatabaseException, NoSuchElementException;

   public ReservationSummaryModel getReservationSummary(Long idReservation, String selectedLanguage)
         throws OutOfWorkflowScopeException, DatabaseException;

   public boolean reservationStatusIsTheSameCheck(Long idReservation, Integer expectedStatus, Long idModifier)
         throws OutOfWorkflowScopeException, DatabaseException;

   public VisitorDetailReservationModel reservationHasFastTrackPossibilty(Long idReservation)
         throws OutOfWorkflowScopeException, DatabaseException;

   public Integer getReservationPointOfOrigin(Long idReservation) throws OutOfWorkflowScopeException, DatabaseException;

   public Set<LocalDate> getReservedDates(Long idRegistration) throws DatabaseException;

   public void updateReservationWithPointOfOrigin(Long idReservation, Integer pointOfOrigin)
            throws OutOfWorkflowScopeException, DatabaseException;

    public void updateReservationValidation(Long idReservation, Long idRegistration)
            throws OutOfWorkflowScopeException, DatabaseException;

    public void updateVisitorDetails(TokenStoredRegistrationInformationModel tokenInformation,
            StoreVisitorDetailsModel requestModel) throws DatabaseException, OutOfWorkflowScopeException;

}
