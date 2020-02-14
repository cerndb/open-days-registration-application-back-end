//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Controllers;

import ch.cern.opendays.Constants.EnvironmentConfigConstants;
import ch.cern.opendays.Constants.WorkflowConstants;
import ch.cern.opendays.Enums.MailActionCodes;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Exceptions.ActionHistoryException;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Exceptions.InvalidRequestException;
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.Exceptions.ReservationModificationException;
import ch.cern.opendays.Exceptions.TokenException;
import ch.cern.opendays.Models.AvailablePlacesModel;
import ch.cern.opendays.Models.DatabaseUpdatedResponseModel;
import ch.cern.opendays.Models.MessageResponseAPIModel;
import ch.cern.opendays.Models.ReservationSummaryModel;
import ch.cern.opendays.Models.StoreVisitorDetailsModel;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;
import ch.cern.opendays.Models.VisitorDetailReservationModel;
import ch.cern.opendays.Models.VisitorsDetailModel;
import ch.cern.opendays.Models.VisitorsDetailUpdateModel;
import ch.cern.opendays.Providers.ResponseMessageProvider;
import ch.cern.opendays.Providers.TokenProviderInteface;
import ch.cern.opendays.Providers.TokenProviderJWT;
import ch.cern.opendays.Services.ActionHistoryServiceInterface;
import ch.cern.opendays.Services.ArrivalPointOpeningHourServiceDB;
import ch.cern.opendays.Services.ArrivalPointOpeningHourServiceInterface;
import ch.cern.opendays.Services.MailActionHistoryServiceDB;
import ch.cern.opendays.Services.MailActionHistoryServiceInterface;
import ch.cern.opendays.Services.ReservationServiceDB;
import ch.cern.opendays.Services.ReservationServiceInterface;
import ch.cern.opendays.Services.VisitorDetailServiceDB;
import ch.cern.opendays.Services.VisitorDetailServiceInterface;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VisitorDetailsController {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(VisitorDetailsController.class);
    private TokenProviderInteface tokenProvider;
    private ReservationServiceInterface reservationService;
    private VisitorDetailServiceInterface visitorDetailService;
    private ActionHistoryServiceInterface actionHistoryService;
    private ArrivalPointOpeningHourServiceInterface arrivalPointOpeningHourService;
    private MailActionHistoryServiceInterface mailActionHistoryService;

    @Autowired
    public void setMailActionHistoryService(MailActionHistoryServiceDB mailActionHistoryService) {
        this.mailActionHistoryService = mailActionHistoryService;
    }

    @Autowired
    public void setArrivalPointOpeningHourService(ArrivalPointOpeningHourServiceDB arrivalPointOpeningHourService) {
        this.arrivalPointOpeningHourService = arrivalPointOpeningHourService;
    }

    @Autowired
    public void setReservationService(ReservationServiceDB reservationService) {
        this.reservationService = reservationService;
    }

    @Autowired
    public void setTokenProvider(TokenProviderJWT tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Autowired
    public void setVisitorDetailService(VisitorDetailServiceDB visitorDetailService) {
        this.visitorDetailService = visitorDetailService;
    }

    @Autowired
    public void setActionHistoryService(ActionHistoryServiceInterface actionHistoryService) {
        this.actionHistoryService = actionHistoryService;
    }

    @PostMapping(path = "/store-visitors", consumes = "application/json", produces = "application/json")
    public MessageResponseAPIModel<DatabaseUpdatedResponseModel> storeVisitors(@RequestBody StoreVisitorDetailsModel requestModel, HttpServletRequest request) {

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        try {
            // get token stored information
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            this.validateRequestModel(requestModel, tokenInformation.getIdReservation(), Boolean.FALSE, tokenInformation, responseLanguage);

            // validat request model
            this.reservationService.storeVisitorDetails(tokenInformation, requestModel);

            //track user confirmed visitor details
            try {
                this.actionHistoryService.trackUserConfirmedVisitorDetails(tokenInformation.getIdReservation(), tokenInformation.getIdRegistration());
            } catch (ActionHistoryException ex) {
                logger.warn(String.format("Action history visitor detail confirmations store failed, but we let the workflow to continue for this reservation id : %1$d", tokenInformation.getIdReservation()));
            }

            return ResponseMessageProvider.returnDataMessage(new DatabaseUpdatedResponseModel(true), MessageStatusCodes.OK.getStatusCode(), responseLanguage);

        } catch (InvalidRequestException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (ReservationModificationException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode(), responseLanguage);
        }

    }

    @PostMapping(path = "/update-visitors", consumes = "application/json", produces = "application/json")
    public MessageResponseAPIModel<DatabaseUpdatedResponseModel> updateVisitors(@RequestBody StoreVisitorDetailsModel requestModel, HttpServletRequest request) {

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        try {
            // get token stored information
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            this.validateRequestModel(requestModel, tokenInformation.getIdReservation(), Boolean.TRUE, tokenInformation, responseLanguage);

            // validat request model
            this.reservationService.updateVisitorDetails(tokenInformation, requestModel);

            // insert mail action
            this.mailActionHistoryService.insertReservationMailAction(tokenInformation.getIdRegistration(), tokenInformation.getIdReservation(), MailActionCodes.RESERVATION_VISITOR_DETAILS_UPDATED.getMailActionCode());

            //track user confirmed visitor details
            try {
                this.actionHistoryService.trackUserUpdatedVisitorDetails(tokenInformation.getIdReservation(), tokenInformation.getIdRegistration());
            } catch (ActionHistoryException ex) {
                logger.warn(String.format("Action history visitor detail update failed, but we let the workflow to continue for this reservation id : %1$d", tokenInformation.getIdReservation()));
            }

            return ResponseMessageProvider.returnDataMessage(new DatabaseUpdatedResponseModel(true), MessageStatusCodes.OK.getStatusCode(), responseLanguage);

        } catch (InvalidRequestException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.UPDATE_EXISITNG_RESERVATION_VISITOR_DETAILS.getStatusCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (ReservationModificationException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }
    }

    @GetMapping(path = "/get-confirmed-visitor-details", produces = "application/json")
    public MessageResponseAPIModel<VisitorsDetailModel> getConfirmedVisitorDetails(HttpServletRequest request) {

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        try {
            // get token stored information
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            VisitorsDetailModel loadedVisitorDetails = this.visitorDetailService.getStoredVisitors(tokenInformation.getIdReservation());

            VisitorDetailReservationModel visitorReservationInfo = this.reservationService.reservationHasFastTrackPossibilty(tokenInformation.getIdReservation());

            loadedVisitorDetails
                    .setFastTrackAllowed(visitorReservationInfo.getIsFastTrackAllowed())
                    .setGroupHasDisabledPerson(visitorReservationInfo.getIsGroupHasDisabledPerson());

            return ResponseMessageProvider.returnDataMessage(loadedVisitorDetails, MessageStatusCodes.OK.getStatusCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }
    }

    @GetMapping(path = "/get-visitor-details-for-update", produces = "application/json")
    public MessageResponseAPIModel<VisitorsDetailUpdateModel> getVisitorDetailsForUpdate(HttpServletRequest request) {

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        try {
            // get token stored information
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            VisitorsDetailUpdateModel loadedVisitorDetails = new VisitorsDetailUpdateModel();
            loadedVisitorDetails.setVisitorsDetails(this.visitorDetailService.getStoredVisitors(tokenInformation.getIdReservation()).visitorsDetails);

            // get reservation details
            ReservationSummaryModel reservation = this.reservationService.getReservationSummary(tokenInformation.getIdReservation(), responseLanguage);

            // get available places
            AvailablePlacesModel availablePlaces = this.arrivalPointOpeningHourService.getAvailablePlaces(reservation.visitDay, reservation.arrivalTimeslotStart, reservation.idArrivalPoint, reservation.idReservation);

            int availableTicketsNumber = availablePlaces.getAvailablePlaces() < WorkflowConstants.ZERO_INTEGER ? reservation.numberOfBookedStandardTickets + reservation.numberOfBookedFastTrackTickets : (availablePlaces.getAvailablePlaces() + reservation.numberOfBookedStandardTickets + reservation.numberOfBookedFastTrackTickets > WorkflowConstants.SIX_INTEGER ? WorkflowConstants.SIX_INTEGER : availablePlaces.getAvailablePlaces() + reservation.numberOfBookedStandardTickets + reservation.numberOfBookedFastTrackTickets);
            int availableFastTrackTicketNumber = availablePlaces.getAvailableFastTrackPlaces() < WorkflowConstants.ZERO_INTEGER ? WorkflowConstants.ZERO_INTEGER + reservation.numberOfBookedFastTrackTickets : (availablePlaces.getAvailableFastTrackPlaces() + reservation.numberOfBookedFastTrackTickets > WorkflowConstants.SIX_INTEGER ? WorkflowConstants.SIX_INTEGER : availablePlaces.getAvailableFastTrackPlaces() + reservation.numberOfBookedFastTrackTickets);

            loadedVisitorDetails
                    .setGroupHasDisabledPerson(reservation.groupHasDisabledMobility)
                    .setNumberOfAvailableFastTrackPlaces(availableFastTrackTicketNumber)
                    .setNumberOfAvailablePlaces(availableTicketsNumber);

            return ResponseMessageProvider.returnDataMessage(loadedVisitorDetails, MessageStatusCodes.OK.getStatusCode(), responseLanguage);

        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.UPDATE_EXISTING_RESERVATION_LOAD_VISITOR_DETAILS.getStatusCode(), responseLanguage);
        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }
    }

    private void validateRequestModel(StoreVisitorDetailsModel requestModel, Long idReservation, boolean isUpdateWorkflow, TokenStoredRegistrationInformationModel tokenInformation, String responseLanguage) throws InvalidRequestException, DatabaseException, OutOfWorkflowScopeException, ReservationModificationException {

        String errorMessage;

        // at least one person
        if (requestModel.visitorsDetails.isEmpty()) {
            errorMessage = String.format("User provided an empty visitor list for this reservation : %1$d ", idReservation);
            logger.error(errorMessage);
            throw new InvalidRequestException(errorMessage)
                    .setErrorCode(MessageStatusCodes.VISITOR_VALIDATION_MIN_ONE_VISITOR.getStatusCode());
        }

        // max 6 person
        if (requestModel.visitorsDetails.size() > WorkflowConstants.SIX_INTEGER) {
            errorMessage = String.format("User wants to register more than 6 persons for this reservation : %1$d ", idReservation);
            logger.error(errorMessage);
            throw new InvalidRequestException(errorMessage)
                    .setErrorCode(MessageStatusCodes.VISITOR_VALIDATION_MAX_SIX_VISITOR.getStatusCode());
        }

        Long conditionMatchedVisitorDataCount;

        // age is between the limitation
        conditionMatchedVisitorDataCount = requestModel.visitorsDetails.stream().filter(visitorMetaData -> !(visitorMetaData.visitorAge >= WorkflowConstants.ZERO_INTEGER && WorkflowConstants.MAX_AGE >= visitorMetaData.visitorAge)).count();

        if (conditionMatchedVisitorDataCount.intValue() > WorkflowConstants.ZERO_INTEGER) {
            errorMessage = String.format("User provided unsupported age in the visitor detail for this reservation id: %1$d ", idReservation);
            logger.error(errorMessage);
            throw new InvalidRequestException(errorMessage)
                    .setErrorCode(MessageStatusCodes.VISITOR_VALIDATION_AGE.getStatusCode());
        }

        // at least one adult
        conditionMatchedVisitorDataCount = requestModel.visitorsDetails.stream().filter(visitorMetaData -> visitorMetaData.visitorAge >= WorkflowConstants.CHILD_AGE_LIMITER).count();

        if (conditionMatchedVisitorDataCount.intValue() < WorkflowConstants.ONE_INTEGER) {
            errorMessage = String.format("User provided group without an adult for this reservation id: %1$d ", idReservation);
            logger.error(errorMessage);
            throw new InvalidRequestException(errorMessage)
                    .setErrorCode(MessageStatusCodes.VISITOR_VALIDATION_ADULT.getStatusCode());
        }

        // count reserved fast track tickets if not reached fast track age
        conditionMatchedVisitorDataCount = requestModel.visitorsDetails.stream().filter(visitorMetaData -> visitorMetaData.visitorAge < WorkflowConstants.FAST_TRACK_AGE_LIMITER && visitorMetaData.fastTrackSelected).count();

        if (conditionMatchedVisitorDataCount.intValue() > WorkflowConstants.ZERO_INTEGER) {
            errorMessage = String.format("User provided unsupported age for fast-track: %1$d ", idReservation);
            logger.error(errorMessage);
            throw new InvalidRequestException(errorMessage)
                    .setErrorCode(MessageStatusCodes.VISITOR_VALIDATION_FAST_TRACK_AGE.getStatusCode());
        }

        // number of fast track
        conditionMatchedVisitorDataCount = requestModel.visitorsDetails.stream().filter(visitorMetaData -> visitorMetaData.fastTrackSelected).count();

        if (!isUpdateWorkflow) {


            try {
                VisitorDetailReservationModel visitorReservationInfo = this.reservationService.reservationHasFastTrackPossibilty(idReservation);

                // if fast track not allowed, but user provided
                if (!visitorReservationInfo.getIsFastTrackAllowed() && conditionMatchedVisitorDataCount.intValue() > WorkflowConstants.ZERO_INTEGER) {
                    errorMessage = String.format("User provided fast track where fast track was not allowed for this reservation id: %1$d ", idReservation);
                    logger.error(errorMessage);
                    throw new InvalidRequestException(errorMessage)
                            .setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
                }

            } catch (OutOfWorkflowScopeException | DatabaseException ex) {
                errorMessage = String.format("Failed to load reservation information for visitor details store validation for this reservation : %1$d", idReservation);
                logger.error(errorMessage);
                throw new DatabaseException(errorMessage, ex)
                        .setErrorCode(MessageStatusCodes.VISITOR_DETAILS_UPDATE_FAILED.getStatusCode());
            } catch (InvalidRequestException ex) {
                throw ex;
            }
        } else {

            ReservationSummaryModel reservation;
            AvailablePlacesModel availablePlaces;

            try {
                // get reservation details
                reservation = this.reservationService.getReservationSummary(tokenInformation.getIdReservation(), responseLanguage);

                availablePlaces = this.arrivalPointOpeningHourService.getAvailablePlaces(reservation.visitDay, reservation.arrivalTimeslotStart, reservation.idArrivalPoint, reservation.idReservation);
            } catch (DatabaseException ex) {
                throw ex;
            } catch (OutOfWorkflowScopeException ex) {
                throw ex;
            }

            int availableTicketsNumber = availablePlaces.getAvailablePlaces() < WorkflowConstants.ZERO_INTEGER ? reservation.numberOfBookedStandardTickets + reservation.numberOfBookedFastTrackTickets : (availablePlaces.getAvailablePlaces() + reservation.numberOfBookedStandardTickets + reservation.numberOfBookedFastTrackTickets > WorkflowConstants.SIX_INTEGER ? WorkflowConstants.SIX_INTEGER : availablePlaces.getAvailablePlaces() + reservation.numberOfBookedStandardTickets + reservation.numberOfBookedFastTrackTickets);
            int availableFastTrackTicketNumber = availablePlaces.getAvailableFastTrackPlaces() < WorkflowConstants.ZERO_INTEGER ? WorkflowConstants.ZERO_INTEGER + reservation.numberOfBookedFastTrackTickets : (availablePlaces.getAvailableFastTrackPlaces() + reservation.numberOfBookedFastTrackTickets > WorkflowConstants.SIX_INTEGER ? WorkflowConstants.SIX_INTEGER : availablePlaces.getAvailableFastTrackPlaces() + reservation.numberOfBookedFastTrackTickets);

            if (requestModel.visitorsDetails.size() > availableTicketsNumber) {
                errorMessage = String.format("Failed to update existing reservation (idReservation: %1$d), because there are not enough free spaces", tokenInformation.getIdReservation());
                logger.error(errorMessage);
                throw new ReservationModificationException(errorMessage).setErrorCode(MessageStatusCodes.RESERVATION_UPDATE_NOT_ENOUGH_PLACE.getStatusCode());
            }

            if (conditionMatchedVisitorDataCount.intValue() > availableFastTrackTicketNumber) {
                errorMessage = String.format("Failed to update existing reservation (idReservation: %1$d), because there are not enough free fast track spaces", tokenInformation.getIdReservation());
                logger.error(errorMessage);
                throw new ReservationModificationException(errorMessage).setErrorCode(MessageStatusCodes.RESERVATION_UPDATE_NOT_ENOUGH_FAST_TRACK_PLACE.getStatusCode());
            }
        }

    }
}
