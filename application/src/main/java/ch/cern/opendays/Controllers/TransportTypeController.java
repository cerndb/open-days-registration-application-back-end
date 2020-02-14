//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Controllers;

import ch.cern.opendays.Constants.EnvironmentConfigConstants;
import ch.cern.opendays.Enums.MailActionCodes;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Enums.ReservationStatusCodes;
import ch.cern.opendays.Exceptions.ActionHistoryException;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.Exceptions.TokenException;
import ch.cern.opendays.Models.DatabaseUpdatedResponseModel;
import ch.cern.opendays.Models.MessageResponseAPIModel;
import ch.cern.opendays.Models.StoreVisitorTransportTypesModel;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;
import ch.cern.opendays.Models.TransportTypeSelectionListModel;
import ch.cern.opendays.Providers.ResponseMessageProvider;
import ch.cern.opendays.Providers.TokenProviderInteface;
import ch.cern.opendays.Providers.TokenProviderJWT;
import ch.cern.opendays.Services.ActionHistoryServiceDB;
import ch.cern.opendays.Services.ActionHistoryServiceInterface;
import ch.cern.opendays.Services.MailActionHistoryServiceDB;
import ch.cern.opendays.Services.MailActionHistoryServiceInterface;
import ch.cern.opendays.Services.ReservationServiceDB;
import ch.cern.opendays.Services.ReservationServiceInterface;
import ch.cern.opendays.Services.TransportTypeServiceDB;
import ch.cern.opendays.Services.TransportTypeServiceInterface;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransportTypeController {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(VisitorDetailsController.class);
    private TokenProviderInteface tokenProvider;
    private TransportTypeServiceInterface transportTypeService;
    private ActionHistoryServiceInterface actionHistoryService;
    private ReservationServiceInterface reservationService;
    private MailActionHistoryServiceInterface mailActionHistoryService;

    @Autowired
    public void setMailActionHistoryService(MailActionHistoryServiceDB mailActionHistoryService) {
        this.mailActionHistoryService = mailActionHistoryService;
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
    public void setActionHistoryService(ActionHistoryServiceDB actionHistoryService) {
        this.actionHistoryService = actionHistoryService;
    }

    @Autowired
    public void setReservationService(ReservationServiceDB reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping(path = "/get-transport-types-selection", produces = "application/json")
    public MessageResponseAPIModel<TransportTypeSelectionListModel> getTransportTypesSelection(HttpServletRequest request) {

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        try {
            // get token stored information
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            return ResponseMessageProvider.returnDataMessage(this.transportTypeService.getTransportTypesForSelection(responseLanguage, tokenInformation), MessageStatusCodes.OK.getStatusCode(), responseLanguage);
        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }

    }

    @PostMapping(path = "/store-transport-types", consumes = "application/json", produces = "application/json")
    public MessageResponseAPIModel<DatabaseUpdatedResponseModel> storeTransportTypes(@RequestBody StoreVisitorTransportTypesModel requestModel, HttpServletRequest request) {

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        try {
            // get token stored information
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            if (!this.reservationService.reservationStatusIsTheSameCheck(tokenInformation.getIdReservation(), ReservationStatusCodes.IN_PROGRESS.getReservationStatusCode(), tokenInformation.getIdRegistration())) {
                return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode(), responseLanguage);
            }

            // remove existing transport types
            this.transportTypeService.deletePreviousVisitorTransportTypeMetaData(tokenInformation.getIdReservation());

            // store new metadata
            this.transportTypeService.storeConfirmedTransportTypes(requestModel, tokenInformation);

            // update reservation with point of origin data
            this.reservationService.updateReservationWithPointOfOrigin(tokenInformation.getIdReservation(), requestModel.selectedPointOfOrigin);

            // track transport type action history
            try {
                this.actionHistoryService.trackUserConfirmedVisitorTransportTypes(tokenInformation.getIdReservation(), tokenInformation.getIdRegistration());
            } catch (ActionHistoryException ex) {
                logger.warn(String.format("Action history visitor transport type confirmation store failed, but we let the workflow to continue for this reservation id : %1$d", tokenInformation.getIdReservation()));
            }

            return ResponseMessageProvider.returnDataMessage(new DatabaseUpdatedResponseModel(true), MessageStatusCodes.OK.getStatusCode(), responseLanguage);
        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }

    }

    @PostMapping(path = "/update-transport-types", consumes = "application/json", produces = "application/json")
    public MessageResponseAPIModel<DatabaseUpdatedResponseModel> updateTransportTypes(@RequestBody StoreVisitorTransportTypesModel requestModel, HttpServletRequest request) {

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        try {
            // get token stored information
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            if (!this.reservationService.reservationStatusIsTheSameCheck(tokenInformation.getIdReservation(), ReservationStatusCodes.FINAL.getReservationStatusCode(), tokenInformation.getIdRegistration())) {
                return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode(), responseLanguage);
            }

            // remove existing transport types
            this.transportTypeService.deletePreviousVisitorTransportTypeMetaData(tokenInformation.getIdReservation());

            // store new metadata
            this.transportTypeService.storeConfirmedTransportTypes(requestModel, tokenInformation);

            // update reservation with point of origin data
            this.reservationService.updateReservationWithPointOfOrigin(tokenInformation.getIdReservation(), requestModel.selectedPointOfOrigin);

            // insert mail action
            this.mailActionHistoryService.insertReservationMailAction(tokenInformation.getIdRegistration(), tokenInformation.getIdReservation(), MailActionCodes.RESERVATION_TRANSPORT_UPDATED.getMailActionCode());

            // track transport type action history
            try {
                this.actionHistoryService.trackUserUpdatedVisitorTransportTypes(tokenInformation.getIdReservation(), tokenInformation.getIdRegistration());
            } catch (ActionHistoryException ex) {
                logger.warn(String.format("Action history visitor transport type confirmation update failed, but we let the workflow to continue for this reservation id : %1$d", tokenInformation.getIdReservation()));
            }

            return ResponseMessageProvider.returnDataMessage(new DatabaseUpdatedResponseModel(true), MessageStatusCodes.OK.getStatusCode(), responseLanguage);
        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }
    }

}
