//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Controllers;

import ch.cern.opendays.Constants.EnvironmentConfigConstants;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Enums.ReservationStatusCodes;
import ch.cern.opendays.Exceptions.ActionHistoryException;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.Exceptions.TokenException;
import ch.cern.opendays.Models.AccessTokenResponseModel;
import ch.cern.opendays.Models.DatabaseUpdatedResponseModel;
import ch.cern.opendays.Models.MessageResponseAPIModel;
import ch.cern.opendays.Models.ReqestReservationConfirmationModel;
import ch.cern.opendays.Models.ReservationStatusUpdateRequestModel;
import ch.cern.opendays.Models.ReservationSummariesModel;
import ch.cern.opendays.Models.ReservationSummaryModel;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;
import ch.cern.opendays.Providers.ResponseMessageProvider;
import ch.cern.opendays.Providers.TokenProviderInteface;
import ch.cern.opendays.Providers.TokenProviderJWT;
import ch.cern.opendays.Services.ActionHistoryServiceInterface;
import ch.cern.opendays.Services.MailActionHistoryServiceDB;
import ch.cern.opendays.Services.MailActionHistoryServiceInterface;
import ch.cern.opendays.Services.ReservationServiceDB;
import ch.cern.opendays.Services.ReservationServiceInterface;
import ch.cern.opendays.Services.TransportTypeServiceDB;
import ch.cern.opendays.Services.TransportTypeServiceInterface;
import ch.cern.opendays.Services.VisitorDetailServiceDB;
import ch.cern.opendays.Services.VisitorDetailServiceInterface;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(ReservationController.class);
    private ActionHistoryServiceInterface actionHistoryServiec;
    private ReservationServiceInterface reservationService;
    private TokenProviderInteface tokenProvider;
    private TransportTypeServiceInterface transportTypeService;
    private VisitorDetailServiceInterface visitorDetailsService;
    private MailActionHistoryServiceInterface mailService;

    @Autowired
    public void setReservationService(ReservationServiceDB reservationService) {
        this.reservationService = reservationService;
    }

    @Autowired
    public void setTokenProvider(TokenProviderJWT tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Autowired
    public void setTransportTypeService(TransportTypeServiceDB transportTypeService) {
        this.transportTypeService = transportTypeService;
    }

    @Autowired
    public void setVisitorDetailsService(VisitorDetailServiceDB visitorDetailsService) {
        this.visitorDetailsService = visitorDetailsService;
    }

    @Autowired
    public void setMailService(MailActionHistoryServiceDB mailService) {
        this.mailService = mailService;
    }

    @Autowired
    public void setActionHistoryServiec(ActionHistoryServiceInterface actionHistoryServiec) {
        this.actionHistoryServiec = actionHistoryServiec;
    }

    @GetMapping(path = "/create-new-reservation", produces = "application/json")
    public MessageResponseAPIModel<AccessTokenResponseModel> createNewReservation(HttpServletRequest request) {

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        try {

            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            // create new reservation and update token with reservation id
            // in case we have in progress reservation we will use that one
            tokenInformation = this.reservationService.createNewReservation(tokenInformation);

            // clean visitors metadata
            this.visitorDetailsService.deletePreviousVisitorMetaData(tokenInformation.getIdReservation());
            // clean visitor transportypes metadata
            this.transportTypeService.deletePreviousVisitorTransportTypeMetaData(tokenInformation.getIdReservation());

            // track new reservation creation action
            try {
                this.actionHistoryServiec.trackUserCreatedNewReservation(tokenInformation.getIdReservation(), tokenInformation.getIdRegistration());
            } catch (ActionHistoryException ex) {
                logger.warn(String.format("Action history new reservation store failed, but we let the workflow to continue for this registration id : %1$d", tokenInformation.getIdRegistration()));
            }

            // generate new token which contains reservation id
            return ResponseMessageProvider.returnDataMessage(new AccessTokenResponseModel(this.tokenProvider.generateRegistrationWorkflowToken(tokenInformation)), MessageStatusCodes.OK.getStatusCode(), responseLanguage);

        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.NEW_RESERVATION_CREATION_FAILED.getStatusCode(), responseLanguage);
        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }
    }

    @GetMapping(path = "/finalize-reservation", produces = "application/json")
    public MessageResponseAPIModel<DatabaseUpdatedResponseModel> finalizeReservation(HttpServletRequest request) {
        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();
        Long idReservation = null;

        try {

            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));
            idReservation = tokenInformation.getIdReservation();

            return this.updateReservationToSepcificStatus(tokenInformation.getIdRegistration(), tokenInformation.getIdReservation(), ReservationStatusCodes.FINAL.getReservationStatusCode(), responseLanguage);
        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (DatabaseException ex) {
            logger.error(String.format("There was a database error for updating reservation to final status where reservation id : %1$d", idReservation));
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.RESERVATION_STATUS_FINAL_UPDATE_FAILED.getStatusCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            logger.error(String.format("Failed to update reservation to final due data error for this reservation id : %1$d", idReservation));
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode(), responseLanguage);
        }
    }

    @PostMapping(path = "/cancel-reservation", consumes = "application/json", produces = "application/json")
    public MessageResponseAPIModel<DatabaseUpdatedResponseModel> cancelReservation(@RequestBody ReservationStatusUpdateRequestModel statusUpdateRequest, HttpServletRequest request) {
        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();
        Long idReservation = null;

        try {
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));
            idReservation = tokenInformation.getIdReservation();
            return this.updateReservationToSepcificStatus(tokenInformation.getIdRegistration(), statusUpdateRequest.idReservation, ReservationStatusCodes.CANCELLED.getReservationStatusCode(), responseLanguage);
        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (DatabaseException ex) {
            logger.error(String.format("There was a database error for updating reservation to cancelled status where reservation id : %1$d", idReservation));
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.RESERVATION_STATUS_CANCEL_UPDATE_FAILED.getStatusCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            logger.error(String.format("Failed to update reservation to cancelled due data error for this reservation id : %1$d", idReservation));
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode(), responseLanguage);
        }
    }

    private MessageResponseAPIModel<DatabaseUpdatedResponseModel> updateReservationToSepcificStatus(Long idRegistration, Long idReservation, Integer updateStatus, String responseLanguage) throws DatabaseException, OutOfWorkflowScopeException {

            this.reservationService.updateReservationStatus(idReservation, updateStatus, idRegistration);

            // create mail action
            if (updateStatus == ReservationStatusCodes.FINAL.getReservationStatusCode()
                    || updateStatus == ReservationStatusCodes.CANCELLED.getReservationStatusCode()) {
                this.mailService.generateReservationFeedbackMail(idRegistration, idReservation, updateStatus);
        }

        // status update action tracking
        ReservationStatusCodes statusChangeEnum = ReservationStatusCodes.getReservationStatus(updateStatus);
        try {
            this.actionHistoryServiec.trackReservationStatusChange(idReservation, idRegistration, statusChangeEnum);
        } catch (ActionHistoryException ex) {
            logger.info(String.format("Action history reservation status change creation failed, but we let the workflow to continue for this reservation id : %1$d", idReservation));
        }

        return ResponseMessageProvider.returnDataMessage(new DatabaseUpdatedResponseModel(true), MessageStatusCodes.OK.getStatusCode(), responseLanguage);
    }

    @GetMapping(path = "/get-active-reservations", produces = "application/json")
    public MessageResponseAPIModel<ReservationSummariesModel> getActiveReservations(HttpServletRequest request) {
        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        try {
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            return ResponseMessageProvider.returnDataMessage(this.reservationService.getRegistrationLinkedReservationSummaries(tokenInformation.getIdRegistration(), responseLanguage), MessageStatusCodes.OK.getStatusCode(), responseLanguage);

        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.TOKEN_PARSE_ERROR.getStatusCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (NoSuchElementException ex) {
            return ResponseMessageProvider.returnDataMessage(new ReservationSummariesModel(), MessageStatusCodes.NO_DATA_TO_DISPLAY.getStatusCode(), responseLanguage);
        }
    }

    @GetMapping(path = "/get-reservation", produces = "application/json")
    public MessageResponseAPIModel<ReservationSummaryModel> getReservation(HttpServletRequest request) {
        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        try {
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            return ResponseMessageProvider.returnDataMessage(this.reservationService.getReservationSummary(tokenInformation.getIdReservation(), responseLanguage), MessageStatusCodes.OK.getStatusCode(), responseLanguage);

        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.TOKEN_PARSE_ERROR.getStatusCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }
    }

    @PostMapping(path = "/get-update-reservation-token", consumes = "application/json", produces = "application/json")
    public MessageResponseAPIModel<AccessTokenResponseModel> generateReservationUpdateToken(@RequestBody ReservationStatusUpdateRequestModel statusUpdateRequest, HttpServletRequest request) {

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        try {
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            this.reservationService.updateReservationValidation(statusUpdateRequest.idReservation, tokenInformation.getIdRegistration());

            tokenInformation.setIdReservation(statusUpdateRequest.idReservation);

            // generate update token
            return ResponseMessageProvider.returnDataMessage(new AccessTokenResponseModel(this.tokenProvider.generateRegistrationWorkflowToken(tokenInformation)), MessageStatusCodes.OK.getStatusCode(), responseLanguage);

        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.TOKEN_PARSE_ERROR.getStatusCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.UPDATE_EXISTING_RESERVATION_TOKEN_GENERATION_FAILED.getStatusCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }
    }

    @PostMapping(path = "/resend-reservation-confirmation", consumes = "application/json", produces = "application/json")
    public MessageResponseAPIModel<DatabaseUpdatedResponseModel> resendReservationConfirmation(@RequestBody ReqestReservationConfirmationModel requestModel, HttpServletRequest request){

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        try {
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            if(!this.reservationService.reservationStatusIsTheSameCheck(requestModel.idReservation, ReservationStatusCodes.FINAL.getReservationStatusCode(),tokenInformation.getIdRegistration())){
                return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode(), responseLanguage);
            }

            this.mailService.generateReservationFeedbackMail(tokenInformation.getIdRegistration(),requestModel.idReservation, ReservationStatusCodes.FINAL.getReservationStatusCode());

            // track new reservation creation action
            try {
                this.actionHistoryServiec.trackUserRequestConfirmationMail(tokenInformation.getIdReservation(), tokenInformation.getIdRegistration());
            } catch (ActionHistoryException ex) {
                logger.warn(String.format("Action history new reservation confirmation mail store failed, but we let the workflow to continue for this registration id : %1$d", tokenInformation.getIdRegistration()));
            }

        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.TOKEN_PARSE_ERROR.getStatusCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.FAILED_TO_REREQUEST_RESERVATION_CONFIRMATION_MAIL.getStatusCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }

        return ResponseMessageProvider.returnDataMessage(new DatabaseUpdatedResponseModel(true), MessageStatusCodes.OK.getStatusCode(), responseLanguage);
    }
}
