//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Constants.ControllerConstants;
import ch.cern.opendays.Constants.EnvironmentConfigConstants;
import ch.cern.opendays.Constants.WorkflowConstants;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Enums.ReservationStatusCodes;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.InterfacesDAO.ReservationRepository;
import ch.cern.opendays.Models.AvailablePlacesModel;
import ch.cern.opendays.Models.ConfirmArrivalPointModel;
import ch.cern.opendays.Models.ReservationSummariesModel;
import ch.cern.opendays.Models.ReservationSummaryModel;
import ch.cern.opendays.Models.ReservedTicketsModel;
import ch.cern.opendays.Models.StoreVisitorDetailsModel;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;
import ch.cern.opendays.Models.TransportTypeModel;
import ch.cern.opendays.Models.VisitorDetailModel;
import ch.cern.opendays.Models.VisitorDetailReservationModel;
import ch.cern.opendays.ModelsDAO.ReservationModelDAO;
import ch.cern.opendays.ModelsDAO.TransportTypeModelDAO;
import ch.cern.opendays.ModelsDAO.VisitorDetailModelDAO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceDB implements ReservationServiceInterface {

    private static final Logger logger = LogManager.getLogger(ReservationServiceDB.class);
    private VisitorDetailServiceInterface visitorDetailService;
    private ReservationRepository reservationRepository;
    private ArrivalPointOpeningHourServiceInterface timeslotService;

    @Autowired
    public void setVisitorDetailService(VisitorDetailServiceDB visitorDetailService) {
        this.visitorDetailService = visitorDetailService;
    }

    @Autowired
    public void setReservationRepository(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Autowired
    public void setTimeslotService(ArrivalPointOpeningHourServiceDB timeslotService) {
        this.timeslotService = timeslotService;
    }

    @Override
    public TokenStoredRegistrationInformationModel createNewReservation(TokenStoredRegistrationInformationModel tokenInformation) throws DatabaseException, OutOfWorkflowScopeException {

        ReservationModelDAO reservation = new ReservationModelDAO();
        List<ReservationModelDAO> notFinishedReservations;

        try {

            // get all existing reservation
            List<ReservationModelDAO> storedReservations = this.reservationRepository
                    .findByIdRegistration(tokenInformation.getIdRegistration());

            // filter the not final reservatins
            notFinishedReservations = storedReservations.stream()
                    .filter(reservationElement -> reservationElement
                    .getReservationStatus() == ReservationStatusCodes.IN_PROGRESS.getReservationStatusCode()
                    || reservationElement.getReservationStatus() == ReservationStatusCodes.CREATED
                    .getReservationStatusCode())
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            logger.error(String.format("Failed to load existing reservation records for this registration id: %1$d", tokenInformation.getIdRegistration()));
            throw new DatabaseException(String.format("Failed to load existing reservation records for this registration id: %1$d", tokenInformation.getIdRegistration()), ex)
                    .setErrorCode(MessageStatusCodes.NEW_RESERVATION_CREATION_FAILED.getStatusCode());
        }

        switch (notFinishedReservations.size()) {
            case 0:
                reservation.setIdRegistration(tokenInformation.getIdRegistration());
                break;
            case 1:
                reservation = notFinishedReservations.get(0);
                break;
            default:
                logger.error(String.format("Data model error, there are multiple not finished reservations are for this registration id: %1$d , new reservation creation will terminate", tokenInformation.getIdRegistration()));
                throw new OutOfWorkflowScopeException(String.format("Data model error, there are multiple not finished reservations are for this registration id: %1$d , new reservation creation will terminate", tokenInformation.getIdRegistration()))
                        .setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        }

        // clear registration information
        reservation
                .setChangeDate(LocalDateTime.now())
                .setNumberOfReservedTickets(0)
                .setNumberOfAdultTickets(0)
                .setReservationStatus(ReservationStatusCodes.CREATED.getReservationStatusCode())
                .setBarcode("")
                .setNumberOfChildTickets(0)
                .setNumberOfFastTrackTickets(0)
                .setHasReducedMobility(Boolean.FALSE);

        try {
            reservation = this.reservationRepository.save(reservation);
        } catch (Exception ex) {
            logger.error(String.format("Failed to create / update reservation for this registration id: %1$d", tokenInformation.getIdRegistration()));
            throw new DatabaseException(String.format("Failed to create / update reservation for this registration id: %1$d", tokenInformation.getIdRegistration()), ex)
                    .setErrorCode(MessageStatusCodes.NEW_RESERVATION_CREATION_FAILED.getStatusCode());
        }

        tokenInformation.setIdReservation(reservation.getIdReservation());

        return tokenInformation;
    }

    @Override
    public void reservationUpdateWithArrivalPoint(ConfirmArrivalPointModel confirmArrival, TokenStoredRegistrationInformationModel tokenInformation) throws DatabaseException, OutOfWorkflowScopeException {

        ReservationModelDAO reservation = new ReservationModelDAO();

        try {
            reservation = this.getReservation(tokenInformation.getIdReservation());

            // only in progress and created reservation can be updated
            if (!(reservation.getReservationStatus() == ReservationStatusCodes.IN_PROGRESS.getReservationStatusCode()
                    || reservation.getReservationStatus() == ReservationStatusCodes.CREATED.getReservationStatusCode())) {
                logger.error(String.format("User wants to update arrival point information for this reservation which status is not in progress : %1$d", tokenInformation.getIdReservation()));
                throw new OutOfWorkflowScopeException(String.format("User wants to update arrival point information for this reservation which status is not in progress : %1$d", tokenInformation.getIdReservation())).setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
            }

        } catch (OutOfWorkflowScopeException ex) {
            logger.error(String.format("Failed to load reservation for arrival detail update for this reservation %1$d", tokenInformation.getIdReservation()));
            throw ex.setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        } catch (DatabaseException ex) {
            logger.error(String.format("Failed to load reservation for arrival detail update for this reservation %1$d", tokenInformation.getIdReservation()));
            throw ex.setErrorCode(MessageStatusCodes.ARRIVAL_POINT_UPDATE_FAILED.getStatusCode());
        }

        // init booking is possible
        boolean bookingPossible = false;

        // get fast track is posssible
        boolean fastTrackIsPossible = false;

        try {
            AvailablePlacesModel availablePlaces = this.timeslotService.getAvailablePlaces(confirmArrival.visitDay, confirmArrival.timeslotStart, confirmArrival.idArrivalPoint, tokenInformation.getIdReservation());

            // if we have places we take it
            if (availablePlaces.getAvailablePlaces() >= ControllerConstants.MAX_NUMBER_OF_RESERVATION_TICKETS) {
                bookingPossible = true;
            }

            // if there are enough palces for fast track we take it
            if (availablePlaces.getAvailableFastTrackPlaces() >= ControllerConstants.MAX_NUMBER_OF_RESERVATION_TICKETS) {
                fastTrackIsPossible = true;
            }

            // if user hit back and selects the same date where the system has already booked ticket for him
            if (reservation.getNumberOfReservedTickets() > WorkflowConstants.ZERO_INTEGER
                    && reservation.getIdArrivalPoint() == confirmArrival.idArrivalPoint
                    && confirmArrival.visitDay.isEqual(reservation.getVisitDay())
                    && confirmArrival.timeslotStart.isEqual(reservation.getTimeslotStart())) {
                bookingPossible = true;

                // if previous reservation have had fast track
                if (reservation.getNumberOfFastTrackTickets() > WorkflowConstants.ZERO_INTEGER) {
                    fastTrackIsPossible = true;
                }
            }
        } catch (DatabaseException ex) {
            String errorMessage = String.format("Failed check freespaces for this reservation: %1$d", tokenInformation.getIdReservation());
            logger.error(errorMessage);
            throw new DatabaseException(errorMessage, ex)
                    .setErrorCode(MessageStatusCodes.ARRIVAL_POINT_UPDATE_FAILED.getStatusCode());
        }

        if (!bookingPossible) {
            String errorMessage = String.format("User tries to book ticket for a timeslot where there are no free places, reservation id: %1$d", tokenInformation.getIdReservation());
            logger.error(errorMessage);
            throw new OutOfWorkflowScopeException(errorMessage).setErrorCode(MessageStatusCodes.NO_FREE_SPACE_FOR_TIMESLOT.getStatusCode());
        }

        // new reservation
        boolean freshReservation = reservation.getNumberOfReservedTickets() == 0;

        // user has selected already a slot which has fast track
        boolean otherTimeslotAllowedFastTrack = reservation.getNumberOfFastTrackTickets() > 0;

        // remove fast track from metadata from exisiting if user confirmed without fast track
        if ((!freshReservation && otherTimeslotAllowedFastTrack && confirmArrival.updateWithoutFastTrack)
                || (!freshReservation && !fastTrackIsPossible && !confirmArrival.updateWithoutFastTrack && otherTimeslotAllowedFastTrack)) {
            this.visitorDetailService.updateRemoveFastTrackFromVisitorDetails(reservation.getIdReservation());
        }

        // reservation gets fast track if fast track possible and user did not say proceed without fast track
        // boolean reservationGetsFastTrack = !confirmArrival.updateWithoutFastTrack && fastTrackIsPossible;
        reservation
                .setIdArrivalPoint(confirmArrival.idArrivalPoint)
                .setTimeslotStart(confirmArrival.timeslotStart)
                .setVisitDay(confirmArrival.visitDay)
                .setChangeDate(LocalDateTime.now())
                .setReservationStatus(ReservationStatusCodes.IN_PROGRESS.getReservationStatusCode())
                .setNumberOfReservedTickets(ControllerConstants.MAX_NUMBER_OF_RESERVATION_TICKETS)
                .setNumberOfFastTrackTickets(fastTrackIsPossible ? ControllerConstants.MAX_NUMBER_OF_RESERVATION_TICKETS : WorkflowConstants.ZERO_INTEGER);

        // update / store reservation
        try {
            this.reservationRepository.save(reservation);
        } catch (Exception ex) {
            logger.error(String.format("Failed to update reservation for arrival detail for this reservation %1$d", tokenInformation.getIdReservation()));
            throw new DatabaseException(String.format("Failed to update reservation for arrival detail for this reservation %1$d", tokenInformation.getIdReservation()), ex)
                    .setErrorCode(MessageStatusCodes.ARRIVAL_POINT_UPDATE_FAILED.getStatusCode());
        }

    }

    private ReservationModelDAO getReservation(Long idReservation) throws OutOfWorkflowScopeException, DatabaseException {
        Optional<ReservationModelDAO> reservation = null;
        try {
            reservation = this.reservationRepository.findById(idReservation);
        } catch (Exception ex) {
            logger.error(String.format("Failed to load reservation: %1$d", idReservation));
            throw new DatabaseException(String.format("Failed to load reservation: %1$d", idReservation), ex);
        }
        if (reservation == null || !reservation.isPresent()) {
            logger.error(String.format("No reservation found for this reservationid %1$d", idReservation));
            throw new OutOfWorkflowScopeException(String.format("No reservation found for this reservationid %1$d", idReservation));
        }

        return reservation.get();
    }

    @Override
    public void storeVisitorDetails(TokenStoredRegistrationInformationModel tokenInformation,
            StoreVisitorDetailsModel requestModel) throws DatabaseException, OutOfWorkflowScopeException {
        try {

            // get reservation
            ReservationModelDAO existingReservation = this.getReservation(tokenInformation.getIdReservation());

            // only in progress reservation can be updated
            if (existingReservation.getReservationStatus() != ReservationStatusCodes.IN_PROGRESS.getReservationStatusCode()) {
                logger.error(String.format("User wants to update not in progress reservation where reservation id is : %1$d", tokenInformation.getIdReservation()));
                throw new OutOfWorkflowScopeException(String.format("User wants to update not in progress reservation where reservation id is : %1$d", tokenInformation.getIdReservation())).setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
            }

            // remove all previous visitor metadata for this reservation
            this.visitorDetailService.deletePreviousVisitorMetaData(tokenInformation.getIdReservation());

            // store visitors metadata
            this.visitorDetailService.storeVisitorDetails(this.populateVisitorDetailsList(requestModel.visitorsDetails, tokenInformation.getIdReservation()), tokenInformation.getIdReservation());

            // update reduced modility
            existingReservation
                    .setHasReducedMobility(requestModel.groupHasDisabledPerson)
                    .setChangeDate(LocalDateTime.now());

            this.reservationRepository.save(existingReservation);

        } catch (OutOfWorkflowScopeException ex) {
            logger.error(String.format("There was no reservation for this reservation id: %1$d", tokenInformation.getIdReservation()));
            throw ex.setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        } catch (DatabaseException ex) {
            logger.error(String.format("Database action failed for visitor details update for this reservation: %1$d", tokenInformation.getIdReservation()));
            throw ex.setErrorCode(MessageStatusCodes.VISITOR_DETAILS_UPDATE_FAILED.getStatusCode());
        } catch (Exception ex) {
            logger.error(String.format("Failed to update reservation where id : %1$d", tokenInformation.getIdReservation()));
            throw new DatabaseException(String.format("Failed to update reservation where id : %1$d", tokenInformation.getIdReservation()), ex).setErrorCode(MessageStatusCodes.VISITOR_DETAILS_UPDATE_FAILED.getStatusCode());
        }

    }

    @Override
    public void updateVisitorDetails(TokenStoredRegistrationInformationModel tokenInformation,
            StoreVisitorDetailsModel requestModel) throws DatabaseException, OutOfWorkflowScopeException {

        try {

            // get reservation
            ReservationModelDAO existingReservation = this.getReservation(tokenInformation.getIdReservation());

            // only finalized reservation can be updated
            if (existingReservation.getReservationStatus() != ReservationStatusCodes.FINAL.getReservationStatusCode()) {
                String errorMessage = String.format("User wants to update not in final status reservation where reservation id is : %1$d", tokenInformation.getIdReservation());
                logger.error(errorMessage);
                throw new OutOfWorkflowScopeException(errorMessage).setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
            }

            // remove all previous visitor metadata for this reservation
            this.visitorDetailService.deletePreviousVisitorMetaData(tokenInformation.getIdReservation());

            // store visitors metadata
            this.visitorDetailService.storeVisitorDetails(this.populateVisitorDetailsList(requestModel.visitorsDetails, tokenInformation.getIdReservation()), tokenInformation.getIdReservation());

            // update visitor detail information from metadata
            existingReservation = this.populateVisitorDataFromMetadata(existingReservation, requestModel.visitorsDetails);

            // update reduced modility
            existingReservation
                    .setHasReducedMobility(requestModel.groupHasDisabledPerson)
                    .setChangeDate(LocalDateTime.now());

            this.reservationRepository.save(existingReservation);

        } catch (OutOfWorkflowScopeException ex) {
            logger.error(String.format("There was no reservation for this reservation id: %1$d", tokenInformation.getIdReservation()));
            throw ex.setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        } catch (DatabaseException ex) {
            logger.error(String.format("Database action failed for visitor details update for this reservation: %1$d", tokenInformation.getIdReservation()));
            throw ex.setErrorCode(MessageStatusCodes.UPDATE_EXISITNG_RESERVATION_VISITOR_DETAILS.getStatusCode());
        } catch (Exception ex) {
            logger.error(String.format("Failed to update reservation where id : %1$d", tokenInformation.getIdReservation()));
            throw new DatabaseException(String.format("Failed to update reservation where id : %1$d", tokenInformation.getIdReservation()), ex).setErrorCode(MessageStatusCodes.UPDATE_EXISITNG_RESERVATION_VISITOR_DETAILS.getStatusCode());
        }

    }

    private List<VisitorDetailModelDAO> populateVisitorDetailsList(List<VisitorDetailModel> visitors, Long idReservation) {
        List<VisitorDetailModelDAO> visitorList = new ArrayList<>();

        visitors.forEach(visitorItem -> {
            visitorList.add(new VisitorDetailModelDAO().setAge(visitorItem.visitorAge).setIdVisitor(visitorItem.idVisitor)
                    .setRequestedFastTrack(visitorItem.fastTrackSelected).setIdReservation(idReservation));
        });

        return visitorList;
    }

    @Override
    public void updateReservationStatus(Long idReservation, Integer reservationStatus, Long idRegistration) throws DatabaseException, OutOfWorkflowScopeException {

        try {
            ReservationModelDAO updateExistingReservation = this.getReservation(idReservation);

            if (!idRegistration.equals(updateExistingReservation.getIdRegistration())) {
                String errorMessage = String.format("User wants to change somebody else reservation status, where change user: %1$d, target user: %2$d, target reservation: %3$d", idRegistration, updateExistingReservation.getIdRegistration(), idReservation);
                logger.error(errorMessage);
                throw new OutOfWorkflowScopeException(errorMessage);
            }

            // final status update can be set only from in progress status
            if (reservationStatus == ReservationStatusCodes.FINAL.getReservationStatusCode()
                    && !(updateExistingReservation.getReservationStatus() == ReservationStatusCodes.IN_PROGRESS.getReservationStatusCode())) {
                logger.error(String.format("Final status can be updated only from in progress status . User wants to update status for this reservation: %1$d", idReservation));
                throw new OutOfWorkflowScopeException(String.format("Final status can be updated only from in progress status . User wants to update status for this reservation: %1$d", idReservation));
            }

            // cancel status can be set only from final status
            if (reservationStatus == ReservationStatusCodes.CANCELLED.getReservationStatusCode()
                    && !(updateExistingReservation.getReservationStatus() == ReservationStatusCodes.FINAL.getReservationStatusCode())) {
                logger.error(String.format("Cancelled status can be updated only from final status . User wants to update status for this reservation: %1$d", idReservation));
                throw new OutOfWorkflowScopeException(String.format("Cancelled status can be updated only from final status . User wants to update status for this reservation: %1$d", idReservation));
            }

            // get visitors ticke data from metadata
            if (reservationStatus == ReservationStatusCodes.FINAL.getReservationStatusCode()) {

                try {
                    List<VisitorDetailModel> visitorDetailsMetadata = this.visitorDetailService.getStoredVisitors(updateExistingReservation.getIdReservation()).visitorsDetails;
                    // get visitors data
                    updateExistingReservation = this.populateVisitorDataFromMetadata(updateExistingReservation, visitorDetailsMetadata);
                } catch (DatabaseException ex) {
                    String errorMessage = String.format("Failed to get visitor stored metadata for this reservation : %1$d ", updateExistingReservation.getIdReservation());
                    logger.error(errorMessage);
                    // error code will be overriden on the controller level
                    throw ex;
                }
            }

            updateExistingReservation.setReservationStatus(reservationStatus)
                    .setChangeDate(LocalDateTime.now());

            this.reservationRepository.save(updateExistingReservation);

        } catch (OutOfWorkflowScopeException | DatabaseException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error(String.format("Failed to update this reservation : %1$d to this status: %2$d", idReservation, reservationStatus));
            throw new DatabaseException(String.format("Failed to update this reservation : %1$d to this status: %2$d", idReservation, reservationStatus), ex);
        }

    }

    private ReservationModelDAO populateVisitorDataFromMetadata(ReservationModelDAO reservation, List<VisitorDetailModel> visitorDetails) throws OutOfWorkflowScopeException {
        reservation.setNumberOfChildTickets(WorkflowConstants.ZERO_INTEGER)
                .setNumberOfAdultTickets(WorkflowConstants.ZERO_INTEGER)
                .setNumberOfFastTrackTickets(WorkflowConstants.ZERO_INTEGER)
                .setNumberOfReservedTickets(WorkflowConstants.ZERO_INTEGER)
                .setChangeDate(LocalDateTime.now());

        visitorDetails.forEach((storedVisitorData) -> {

            // if fast track selected we increase the number
            if (storedVisitorData.getFastTrackSelected()) {
                reservation.increaseNumberOfFastTrackTickets();
            }

            // increase child or adult
            if (storedVisitorData.getVisitorAge() < WorkflowConstants.CHILD_AGE_LIMITER) {
                reservation.increaseNumeberOfChildTickets();
            } else {
                reservation.increaseNumberOfAdultTickets();
            }

            // we increase the tickets number always
            reservation.increaseNumberOfReservedTickets();
        });

        if (reservation.getNumberOfReservedTickets() == WorkflowConstants.ZERO_INTEGER
                || reservation.getNumberOfReservedTickets() > ControllerConstants.MAX_NUMBER_OF_RESERVATION_TICKETS
                || reservation.getNumberOfReservedTickets() < reservation.getNumberOfFastTrackTickets()
                || reservation.getNumberOfAdultTickets() == WorkflowConstants.ZERO_INTEGER) {

            String errorMessage = String.format("Invalid ticket data for this reservation : %1$d ", reservation.getIdReservation());
            logger.error(errorMessage);
            // error code will be overriden on the controller level
            throw new OutOfWorkflowScopeException(errorMessage);
        }

        return reservation;
    }

    private ReservationSummaryModel prepareReservationSummaryData(ReservationModelDAO storedReservation, String selectedLanguage) {

        ReservationSummaryModel reservation = new ReservationSummaryModel()
                .setSelectedLanguage(selectedLanguage)
                .setVisitDay(storedReservation.getVisitDay())
                .setArrivalTimeslotStart(storedReservation.getTimeslotStart())
                .setNumberOfBookedStandardTickets(storedReservation.getNumberOfReservedTickets() - storedReservation.getNumberOfFastTrackTickets())
                .setNumberOfBookedFastTrackTickets(storedReservation.getNumberOfFastTrackTickets())
                .setIdReservation(storedReservation.getIdReservation())
                .setGroupHasDisabledMobility(storedReservation.getHasReducedMobility())
                .setIdArrivalPoint(storedReservation.getIdArrivalPoint());

        // if arrival point is selected get arrival point information
        if (storedReservation.getIdArrivalPoint() != null) {
            Integer openDurration = storedReservation.getArrivalPointOpeningTimes().getOpeningDurrationInMinutes();
            String arrivalPointName = storedReservation.getArrivalPoint().getPointName();

            reservation.setArrivalTimeslotEnd(storedReservation.getTimeslotStart().plusMinutes(openDurration))
                    .setNameArrivalPoint(arrivalPointName);
        }

        // include transport type details to summary
        storedReservation.getTransportTypes().forEach((selectedTransportType) -> {

            TransportTypeModelDAO transportTypeDescriptor = selectedTransportType.getTransportType();

            String transportTypeDisplayName = selectedLanguage.equals(EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE) ? transportTypeDescriptor.getTransportNameEN() : transportTypeDescriptor.getTransportNameFR();

            reservation.getTransportTypes().add(new TransportTypeModel()
                    .setDisplayName(transportTypeDisplayName)
                    .setIdTransportType(transportTypeDescriptor.getIdTransportType())
                    .setValue(Boolean.TRUE));
        });

        if (storedReservation.getPointOfOrigin() != null) {
            // include point of origin details to summary
            reservation.setPointOfOriginDisplayName(selectedLanguage.equals(EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE) ? storedReservation.getPointOfOrigin().getPointOfOriginNameEN() : storedReservation.getPointOfOrigin().getPointOfOriginNameFR());
        }

        return reservation;
    }

    @Override
    public ReservationSummaryModel getReservationSummary(Long idReservation, String selectedLanguage) throws OutOfWorkflowScopeException, DatabaseException {
        try {

            ReservationSummaryModel storedReservation = this.prepareReservationSummaryData(this.getReservation(idReservation), selectedLanguage);

            ReservedTicketsModel ticketInformation = this.visitorDetailService.getReservationTickets(idReservation);

            return storedReservation
                    .setNumberOfBookedFastTrackTickets(ticketInformation.getFastTrackTickets())
                    .setNumberOfBookedStandardTickets(ticketInformation.getStandardTickets());
        } catch (DatabaseException ex) {
            logger.error(String.format("Failed to load reservation summary for this reservation id : %1$d", idReservation));
            throw ex.setErrorCode(MessageStatusCodes.RESERVATION_SUMMARY_LOAD_FAILED.getStatusCode());
        } catch (OutOfWorkflowScopeException ex) {
            logger.error(String.format("Data for this reservation id : %1$d is not correct failed to load summary", idReservation));
            throw ex.setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        }
    }

    @Override
    public ReservationSummariesModel getRegistrationLinkedReservationSummaries(Long idRegistration, String selectedLanguage) throws DatabaseException, NoSuchElementException {

        ReservationSummariesModel reservations = new ReservationSummariesModel();

        try {
            // get all existing reservation
            List<ReservationModelDAO> storedReservations = this.reservationRepository.findByIdRegistration(idRegistration);

            if (!storedReservations.isEmpty()) {
                storedReservations.forEach((storedReservation) -> {

                    if (storedReservation.getReservationStatus() == ReservationStatusCodes.FINAL.getReservationStatusCode()) {
                        ReservationSummaryModel reservation = this.prepareReservationSummaryData(storedReservation, selectedLanguage);
                        reservations.existingReservations.add(reservation);
                    }

                });
            }

            Collections.sort(reservations.existingReservations, new Comparator<ReservationSummaryModel>() {
                public int compare(ReservationSummaryModel o1, ReservationSummaryModel o2) {
                    if (o1.visitDay == null || o2.visitDay == null) {
                        return 0;
                    }
                    if (o1.arrivalTimeslotStart == null || o2.arrivalTimeslotStart == null) {
                        return 0;
                    }

                    if (o1.visitDay.isBefore(o2.visitDay)) {
                        return o1.arrivalTimeslotStart.compareTo(o2.arrivalTimeslotStart);
                    } else {
                        return 0;
                    }
                }
            });

        } catch (Exception ex) {
            logger.error(String.format("Failed to load reservations for this registration: %1$d", idRegistration));
            throw new DatabaseException(String.format("Failed to load reservations for this registration: %1$d", idRegistration), ex)
                    .setErrorCode(MessageStatusCodes.RESERVATION_DASHBOARD_SUMMARY_LOAD_FAILED.getStatusCode());
        }

        if (reservations.existingReservations.isEmpty()) {
            logger.info(String.format("There were no reservations for this registration: %1$d", idRegistration));
            throw new NoSuchElementException(String.format("There were no reservations for this registration: %1$d", idRegistration));
        }

        return reservations;
    }

    @Override
    public boolean reservationStatusIsTheSameCheck(Long idReservation, Integer expectedStatus, Long idModifier) throws OutOfWorkflowScopeException, DatabaseException {

        try {
            ReservationModelDAO storedReservation = this.getReservation(idReservation);

            if (!storedReservation.getIdRegistration().equals(idModifier)) {
                logger.error(String.format("User wants to update a somebody else reservation. Target reservation id: %1$d , user: %2$d", idReservation, idModifier));
                return false;
            }

            return storedReservation.getReservationStatus() == expectedStatus;

        } catch (OutOfWorkflowScopeException ex) {
            logger.error(String.format("Data model error too many reservation for this id reservation: %1$d", idReservation));
            throw ex.setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        } catch (DatabaseException ex) {
            logger.error(String.format("Failed to load data for transport type update for this reservation id : %1$d", idReservation));
            throw ex.setErrorCode(MessageStatusCodes.VISITOR_TRANSPORT_TYPE_UPDATE_FAILED.getStatusCode());
        }

    }

    @Override
    public Set<LocalDate> getReservedDates(Long idRegistration) throws DatabaseException {
        try {
            List<ReservationModelDAO> storedReservations = this.reservationRepository.findByIdRegistration(idRegistration);

            Set<LocalDate> registartionDates = new HashSet<>();

            if (storedReservations.size() > WorkflowConstants.ZERO_INTEGER) {
                storedReservations.forEach((bookedReservation) -> {
                    if (bookedReservation.getReservationStatus() == ReservationStatusCodes.FINAL.getReservationStatusCode() || bookedReservation.getReservationStatus() == ReservationStatusCodes.IN_MODIFICATION.getReservationStatusCode()) {
                        registartionDates.add(bookedReservation.getVisitDay().toLocalDate());
                    }
                });
            }

            return registartionDates;
        } catch (Exception ex) {
            String errorMessage = String.format("Failed to get existing reservation dates for this idRegistration :%1$d", idRegistration);
            logger.error(errorMessage);
            throw new DatabaseException(errorMessage, ex);
        }
    }

    @Override
    public VisitorDetailReservationModel reservationHasFastTrackPossibilty(Long idReservation) throws OutOfWorkflowScopeException, DatabaseException {
        try {
            ReservationModelDAO reservation = this.getReservation(idReservation);

            return new VisitorDetailReservationModel()
                    .setFastTrackAllowed(reservation.getNumberOfFastTrackTickets() == ControllerConstants.MAX_NUMBER_OF_RESERVATION_TICKETS)
                    .setGroupHasDisabledPerson(reservation.getHasReducedMobility());
        } catch (OutOfWorkflowScopeException ex) {
            logger.error(String.format("Failed to load fast track information for this reservation : %1$d", idReservation));
            throw ex.setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        } catch (DatabaseException ex) {
            logger.error(String.format("Failed to load fast track information for this reservation : %1$d", idReservation));
            throw ex.setErrorCode(MessageStatusCodes.VISITOR_DETAILS_LOAD_FAILED.getStatusCode());
        }
    }

    @Override
    public Integer getReservationPointOfOrigin(Long idReservation) throws OutOfWorkflowScopeException, DatabaseException {

        try {
            return this.getReservation(idReservation).getIdPointOfOrigin() == null ? WorkflowConstants.DEFAULT_SELECTED_DROP_DOWN_ELEMENT : this.getReservation(idReservation).getIdPointOfOrigin();
        } catch (OutOfWorkflowScopeException ex) {
            String errorMessage = String.format("Workflow is outside of the supported range for this reservation", idReservation);
            logger.error(errorMessage);
            throw ex;
        } catch (Exception ex) {
            String errorMessage = String.format("Failed to get point of origin data for this reservation", idReservation);
            logger.error(errorMessage);
            throw new DatabaseException(errorMessage, ex);
        }
    }

    @Override
    public void updateReservationWithPointOfOrigin(Long idReservation, Integer pointOfOrigin) throws OutOfWorkflowScopeException, DatabaseException {
        try {

            ReservationModelDAO existingReservation = this.getReservation(idReservation)
                    .setChangeDate(LocalDateTime.now())
                    .setIdPointOfOrigin(pointOfOrigin);

            this.reservationRepository.save(existingReservation);

        } catch (OutOfWorkflowScopeException ex) {
            throw ex.setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        } catch (DatabaseException ex) {
            throw ex.setErrorCode(MessageStatusCodes.POINT_OF_ORIGIN_FAILED_TO_STORE.getStatusCode());
        } catch (Exception ex) {
            String errorMessage = String.format("Failed to store point of origin for this reservation", idReservation);
            logger.error(errorMessage);
            throw new DatabaseException(errorMessage, ex).setErrorCode(MessageStatusCodes.POINT_OF_ORIGIN_FAILED_TO_STORE.getStatusCode());
        }

    }

    @Override
    public void updateReservationValidation(Long idReservation, Long idRegistration) throws OutOfWorkflowScopeException, DatabaseException {

        ReservationModelDAO storedReservation = null;

        try {
            storedReservation = this.getReservation(idReservation);
        } catch (DatabaseException ex) {
            throw ex;
        } catch (OutOfWorkflowScopeException ex) {
            throw ex.setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        }

        // if registration id of the user does not match
        if (!idRegistration.equals(storedReservation.getIdRegistration())) {
            String errorMessage = String.format("User wants to get update token for a different user's reservation, where change user: %1$d, target user: %2$d, target reservation: %3$d", idRegistration, storedReservation.getIdRegistration(), idReservation);
            logger.error(errorMessage);
            throw new OutOfWorkflowScopeException(errorMessage).setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        }

        // user can only update reservation which is not in final status
        if (storedReservation.getReservationStatus() != ReservationStatusCodes.FINAL.getReservationStatusCode()) {
            String errorMessage = String.format("User wants to update reservation which is not in final status, where change user: %1$d, reservation: %3$d", idRegistration, idReservation);
            logger.error(errorMessage);
            throw new OutOfWorkflowScopeException(errorMessage).setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        }
    }

    @Override
    public void reservationUpdateArrivalPoint(ConfirmArrivalPointModel confirmArrival, TokenStoredRegistrationInformationModel tokenInformation) throws DatabaseException, OutOfWorkflowScopeException {

        try {
            ReservationModelDAO updateRequiredReservation = this.getReservation(tokenInformation.getIdReservation());

            updateRequiredReservation
                    .setIdArrivalPoint(confirmArrival.idArrivalPoint)
                    .setTimeslotStart(confirmArrival.timeslotStart)
                    .setChangeDate(LocalDateTime.now());

            this.reservationRepository.save(updateRequiredReservation);

        } catch (OutOfWorkflowScopeException ex) {
            logger.error(String.format("There are too many or too few reservation for this reservation id: %1$d", tokenInformation.getIdReservation()));
            throw ex;
        } catch (DatabaseException ex) {
            logger.error(String.format("Failed to load reservation for arrival point update, where reservation id: %1$d", tokenInformation.getIdReservation()));
            throw ex;
        } catch (Exception ex) {
            String errorMessage = String.format("Failed to update arrival point information for existing reservation where reservation id: %1$d", tokenInformation.getIdReservation());
            logger.error(errorMessage);
            throw new DatabaseException(errorMessage, ex);
        }

    }

    public void resendReservationConfirmation(TokenStoredRegistrationInformationModel tokenInformation, Long idReservation) throws DatabaseException, OutOfWorkflowScopeException {

        try {
            ReservationModelDAO storedReservation = this.getReservation(idReservation);

            if (!storedReservation.getIdRegistration().equals(tokenInformation.getIdRegistration()) || storedReservation.getReservationStatus() != ReservationStatusCodes.FINAL.getReservationStatusCode()) {
                String errorMessage = String.format("User wants to request reservation mail confirmation which is not owned or which is not in final status, where reservation id : $1%d, registration id: $2%d", tokenInformation.getIdReservation(), tokenInformation.getIdRegistration());
                logger.error(errorMessage);
                throw new OutOfWorkflowScopeException(errorMessage);
            }

        } catch (OutOfWorkflowScopeException ex) {
            logger.error(String.format("Can not send reservation confirmation mail again, because data is out of workflow, where reservation id: %1$d", tokenInformation.getIdReservation()));
            throw ex;
        } catch (DatabaseException ex) {
            logger.error(String.format("Failed to load stored reservation for confirmation mail send, where reservation id: %1$d", tokenInformation.getIdReservation()));
            throw ex;
        } catch (Exception ex) {
            String errorMessage = String.format("Failed to create mail action for reservation resend, where reservation id: %1$d", tokenInformation.getIdReservation());
            logger.error(errorMessage);
            throw new DatabaseException(errorMessage, ex);
        }

    }

}
