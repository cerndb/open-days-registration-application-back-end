//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Controllers;

import ch.cern.opendays.Constants.ControllerConstants;
import ch.cern.opendays.Constants.EnvironmentConfigConstants;
import ch.cern.opendays.Constants.WorkflowConstants;
import ch.cern.opendays.Enums.MailActionCodes;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Exceptions.ActionHistoryException;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.Exceptions.ReservationModificationException;
import ch.cern.opendays.Exceptions.TokenException;
import ch.cern.opendays.Models.ArrivalPointDatesModel;
import ch.cern.opendays.Models.ArrivalPointModel;
import ch.cern.opendays.Models.ArrivalPointOpenTimeslotModel;
import ch.cern.opendays.Models.ArrivalPointRadioButtonModel;
import ch.cern.opendays.Models.ConfirmArrivalPointModel;
import ch.cern.opendays.Models.DailyAvailablePlacesResponseModel;
import ch.cern.opendays.Models.DatabaseUpdatedResponseModel;
import ch.cern.opendays.Models.MessageResponseAPIModel;
import ch.cern.opendays.Models.RequestArrivalPointTimeslotsModel;
import ch.cern.opendays.Models.ReservationArrivalDataModel;
import ch.cern.opendays.Models.ReservationSummaryModel;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;
import ch.cern.opendays.Providers.ResponseMessageProvider;
import ch.cern.opendays.Providers.TokenProviderInteface;
import ch.cern.opendays.Providers.TokenProviderJWT;
import ch.cern.opendays.Services.ActionHistoryServiceDB;
import ch.cern.opendays.Services.ActionHistoryServiceInterface;
import ch.cern.opendays.Services.ArrivalPointOpeningHourServiceDB;
import ch.cern.opendays.Services.ArrivalPointOpeningHourServiceInterface;
import ch.cern.opendays.Services.ArrivalPointServiceInterface;
import ch.cern.opendays.Services.MailActionHistoryServiceDB;
import ch.cern.opendays.Services.MailActionHistoryServiceInterface;
import ch.cern.opendays.Services.PrivilegedVisitorServiceDB;
import ch.cern.opendays.Services.PrivilegedVisitorServiceInterface;
import ch.cern.opendays.Services.RegisteredPersonServiceDB;
import ch.cern.opendays.Services.RegisteredPersonServiceInterface;
import ch.cern.opendays.Services.ReservationServiceDB;
import ch.cern.opendays.Services.ReservationServiceInterface;
import ch.cern.opendays.Services.VisitorDetailServiceDB;
import ch.cern.opendays.Services.VisitorDetailServiceInterface;
import ch.cern.opendays.Validators.DateValidator;
import ch.cern.opendays.WorkflowSettings;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArrivalPointSelectorController {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(ArrivalPointSelectorController.class);
    private TokenProviderInteface tokenProvider;
    private ReservationServiceInterface reservationService;
    private PrivilegedVisitorServiceInterface privilegedVisitorService;
    private ArrivalPointOpeningHourServiceInterface arrivalPointOpeningHourService;
    private RegisteredPersonServiceInterface registeredPersonService;
    private WorkflowSettings workflowSettings;
    private ArrivalPointServiceInterface arrialPointService;
    private ActionHistoryServiceInterface actionHistoryService;
    private VisitorDetailServiceInterface visitorDetailsService;
    private MailActionHistoryServiceInterface mailActionHistoryService;

    @Autowired
    public void setMailActionHistoryService(MailActionHistoryServiceDB mailActionHistoryService) {
        this.mailActionHistoryService = mailActionHistoryService;
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
    public void setPrivilegedVisitorService(PrivilegedVisitorServiceDB privilegedVisitorService) {
        this.privilegedVisitorService = privilegedVisitorService;
    }

    @Autowired
    public void setArrivalPointOpeningHourService(ArrivalPointOpeningHourServiceDB arrivalPointOpeningHourService) {
        this.arrivalPointOpeningHourService = arrivalPointOpeningHourService;
    }

    @Autowired
    public void setWorkflowSettings(WorkflowSettings workflowSettings) {
        this.workflowSettings = workflowSettings;
    }

    @Autowired
    public void setRegisteredPersonService(RegisteredPersonServiceDB registeredPersonService) {
        this.registeredPersonService = registeredPersonService;
    }

    @Autowired
    public void setArrialPointService(ArrivalPointServiceInterface arrialPointService) {
        this.arrialPointService = arrialPointService;
    }

    @Autowired
    public void setActionHistoryService(ActionHistoryServiceDB actionHistoryService) {
        this.actionHistoryService = actionHistoryService;
    }

    @Autowired
    public void setVisitorDetailsService(VisitorDetailServiceDB visitorDetailsService) {
        this.visitorDetailsService = visitorDetailsService;
    }

    @GetMapping(path = "/get-daily-available-places", produces = "application/json")
    public MessageResponseAPIModel<DailyAvailablePlacesResponseModel> getDailyAvailablePlaces(HttpServletRequest request){

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();
        try{
            return ResponseMessageProvider.returnDataMessage(this.arrivalPointOpeningHourService.getDailyAvailablePlaces(responseLanguage), MessageStatusCodes.OK.getStatusCode(), responseLanguage);
        }catch(DatabaseException ex){
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }
    }

    @GetMapping(path = "/get-arrival-point-dates", produces = "application/json")
    public MessageResponseAPIModel<ArrivalPointDatesModel> getArrivalPointDates(HttpServletRequest request) {

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        try {
            // get token stored information
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            // get arrival point dates
            ArrivalPointDatesModel arrivalPointDates = this.getArrivalPointDates(tokenInformation, responseLanguage);

            // return available dates
            return ResponseMessageProvider.returnDataMessage(arrivalPointDates, MessageStatusCodes.OK.getStatusCode(), responseLanguage);
        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }

    }

    @PostMapping(path = "/request-arrival-point-timeslots", consumes = "application/json", produces = "application/json")
    public MessageResponseAPIModel<List<ArrivalPointModel>> requestArrivalPointTimeslots(@RequestBody RequestArrivalPointTimeslotsModel requestModel, HttpServletRequest request) {

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        if (!DateValidator.DateTimeIsDefault(requestModel.selectedArrivalDate)) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.INCORRECT_API_INPUT.getStatusCode(), responseLanguage);
        }

        try {
            // get token stored information
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            return ResponseMessageProvider.returnDataMessage(this.arrialPointService.getAvailablePlaces(requestModel.selectedArrivalDate, tokenInformation.getIdRegistration(), responseLanguage, tokenInformation.getIdReservation()), MessageStatusCodes.OK.getStatusCode(), responseLanguage);

        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }
    }

    @PostMapping(path = "/confirm-arrival-point", consumes = "application/json", produces = "application/json")
    public MessageResponseAPIModel<DatabaseUpdatedResponseModel> confirmArrivalPoint(@RequestBody ConfirmArrivalPointModel requestModel, HttpServletRequest request) {

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        if (!validateConfirmArrivalPointMessage(requestModel)) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.INCORRECT_API_INPUT.getStatusCode(), responseLanguage);
        }

        try {
            // get token stored information
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            // validate if selected arrival date is available for the user
            if (!this.validatesSelectedArrivalDate(tokenInformation, requestModel.visitDay)) {
                return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.ARRIVAL_DATE_NOT_AVAILABLE.getStatusCode(), responseLanguage);
            }

            // check if we have enough space
            if (this.checkIfArrivalPointChangePossible(tokenInformation, requestModel, responseLanguage, Boolean.FALSE)) {
                // remove fast track if not possible
                if (requestModel.updateWithoutFastTrack) {
                    this.visitorDetailsService.updateRemoveFastTrackFromVisitorDetails(tokenInformation.getIdReservation());
                }

                // update reservation with arrival point details
                this.reservationService.reservationUpdateWithArrivalPoint(requestModel, tokenInformation);

                // track arrival point selected action
                try {
                    this.actionHistoryService.trackUserConfirmedArrivalPoint(tokenInformation.getIdReservation(), tokenInformation.getIdRegistration());
                } catch (ActionHistoryException ex) {
                    logger.warn(String.format("Action history visitor arrival point store failed, but we let the workflow to continue for this reservation id : %1$d", tokenInformation.getIdReservation()));
                }

                return ResponseMessageProvider.returnDataMessage(new DatabaseUpdatedResponseModel(true), MessageStatusCodes.OK.getStatusCode(), responseLanguage);

            } else {
                return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.NO_FREE_SPACE_FOR_TIMESLOT.getStatusCode(), responseLanguage);
            }

        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (DatabaseException | ReservationModificationException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.ARRIVAL_POINT_UPDATE_FAILED.getStatusCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode(), responseLanguage);
        }

    }

    @PostMapping(path = "/update-arrival-point", consumes = "application/json", produces = "application/json")
    public MessageResponseAPIModel<DatabaseUpdatedResponseModel> updateArrivalPoint(@RequestBody ConfirmArrivalPointModel requestModel, HttpServletRequest request) {

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        if (!validateConfirmArrivalPointMessage(requestModel)) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.INCORRECT_API_INPUT.getStatusCode(), responseLanguage);
        }

        try {

            // get token stored information
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            // check if user has the right to do it and if reservation status is final
            this.reservationService.updateReservationValidation(tokenInformation.getIdReservation(), tokenInformation.getIdRegistration());

            // check if we have enough space
            if (this.checkIfArrivalPointChangePossible(tokenInformation, requestModel, responseLanguage, Boolean.TRUE)) {

                // remove fast track if not possible
                if (requestModel.updateWithoutFastTrack) {
                    this.visitorDetailsService.updateRemoveFastTrackFromVisitorDetails(tokenInformation.getIdReservation());
                }

                this.reservationService.reservationUpdateArrivalPoint(requestModel, tokenInformation);

                // insert mail action
                this.mailActionHistoryService.insertReservationMailAction(tokenInformation.getIdRegistration(), tokenInformation.getIdReservation(), MailActionCodes.RESERVATION_ARRIVAL_POINT_UPDATED.getMailActionCode());

                // track arrival point changed action
                try {
                    this.actionHistoryService.trackUserUpdatedArrivalPoint(tokenInformation.getIdReservation(), tokenInformation.getIdRegistration());
                } catch (ActionHistoryException ex) {
                    logger.warn(String.format("Action history visitor arrival point update failed, but we let the workflow to continue for this reservation id : %1$d", tokenInformation.getIdReservation()));
                }

                return ResponseMessageProvider.returnDataMessage(new DatabaseUpdatedResponseModel(true), MessageStatusCodes.OK.getStatusCode(), responseLanguage);
            } else {
                return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.NO_FREE_SPACE_FOR_TIMESLOT.getStatusCode(), responseLanguage);
            }

        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.UPDATE_EXISTING_RESERVATION_ARRIVAL_POINT_UPDATE_FAILED.getStatusCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode(), responseLanguage);
        } catch (ReservationModificationException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.NO_FAST_TRACK_TICKETS.getStatusCode(), responseLanguage);
        }
    }

    @GetMapping(path = "/get-reservation-arrival-data", produces = "application/json")
    public MessageResponseAPIModel<ReservationArrivalDataModel> getReservationDate(HttpServletRequest request) {
        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        try {

            // get token stored information
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));
            ReservationSummaryModel reservationSummary = this.reservationService.getReservationSummary(tokenInformation.getIdReservation(), responseLanguage);

            return ResponseMessageProvider.returnDataMessage(new ReservationArrivalDataModel(reservationSummary, responseLanguage),
                    MessageStatusCodes.OK.getStatusCode(), responseLanguage);

        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.UPDATE_EXISTING_RESERVATION_LOAD_BOOKED_VISITDAY.getStatusCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode(), responseLanguage);
        }
    }

    private boolean validateConfirmArrivalPointMessage(ConfirmArrivalPointModel requestModel) {
        return DateValidator.DateTimeIsDefault(requestModel.visitDay)
                && DateValidator.DateTimeIsDefault(requestModel.timeslotStart);
    }

    private ArrivalPointDatesModel getArrivalPointDates(TokenStoredRegistrationInformationModel tokenInformation, String language) throws DatabaseException, OutOfWorkflowScopeException {

        // get privileged days based for mail address
        Set<LocalDate> privilegedDays = this.privilegedVisitorService.getPrivilegedDays(this.registeredPersonService.getMailAddress(tokenInformation.getIdRegistration()), tokenInformation.getIdRegistration());

        ArrivalPointDatesModel arrivalPointDates = new ArrivalPointDatesModel();

        // populate available dates
        if (privilegedDays.isEmpty()
                && LocalDateTime.now().isBefore(LocalDateTime.parse(this.workflowSettings.getRegistrationOpeningTime(), ControllerConstants.DATETIME_FORMAT_PATTERN))) {
            // if day not privileged and registration is before we return false
            arrivalPointDates.setRegistrationPeriodIsActive(Boolean.FALSE);
            arrivalPointDates.setPrivilegedVisitor(Boolean.FALSE);
        } else if (!LocalDateTime.now().isBefore(LocalDateTime.parse(this.workflowSettings.getRegistrationOpeningTime(), ControllerConstants.DATETIME_FORMAT_PATTERN))) {
            // if privileged, but already opened
            arrivalPointDates = this.arrivalPointOpeningHourService.getArrivalDates(privilegedDays, tokenInformation.getIdRegistration(), language);
            arrivalPointDates.setRegistrationPeriodIsActive(Boolean.TRUE);
            arrivalPointDates.setPrivilegedVisitor(Boolean.TRUE);
        } else {
            // return only friday privileged user and earlier than registration
            List<ArrivalPointRadioButtonModel> radioButtonDates = new ArrayList<>();

            // if only privileged.
            privilegedDays.forEach((privilageDay) -> {
                radioButtonDates.add(new ArrivalPointRadioButtonModel(privilageDay, language));
            });

            arrivalPointDates.setArrivalDates(radioButtonDates);
            arrivalPointDates.setRegistrationPeriodIsActive(Boolean.TRUE);
            arrivalPointDates.setPrivilegedVisitor(Boolean.TRUE);
        }

        try {
            Set<LocalDate> registeredDates = this.reservationService.getReservedDates(tokenInformation.getIdRegistration());

            if (registeredDates.size() > 0) {
                List<ArrivalPointRadioButtonModel> filteredDates = new ArrayList<>();
                arrivalPointDates.arrivalDates.forEach((arrivalPointDate) -> {
                    Long possibleDateHasRegistration = registeredDates.stream().filter(registeredDate -> arrivalPointDate.getValueLocalInDateFormat().isEqual(registeredDate)).count();

                    if (possibleDateHasRegistration < WorkflowConstants.ONE_INTEGER) {
                        filteredDates.add(arrivalPointDate);
                    }
                });

                arrivalPointDates.setArrivalDates(filteredDates);
            }

        } catch (DatabaseException ex) {
            throw ex.setErrorCode(MessageStatusCodes.ARRIVAL_POINT_GET_AVAILABLE_DATES_LOAD_FAILED.getStatusCode());
        }

        return arrivalPointDates;
    }

    private boolean validatesSelectedArrivalDate(TokenStoredRegistrationInformationModel tokenInformation, LocalDateTime visitDay) throws DatabaseException, OutOfWorkflowScopeException {
        try {
            ArrivalPointDatesModel selectableDates = this.getArrivalPointDates(tokenInformation, EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE);
            return selectableDates.arrivalDates.stream().filter(checkDate -> checkDate.getValueLocalInDateFormat().isEqual(visitDay.toLocalDate())).count() > 0;
        } catch (DatabaseException ex) {
            logger.error(String.format("Failed to validate date (%1$s) before arrival point update for this this reservation %2$d ", visitDay.format(ControllerConstants.DATE_FORMAT_PATTERN), tokenInformation.getIdReservation()));
            throw ex.setErrorCode(MessageStatusCodes.ARRIVAL_POINT_UPDATE_FAILED.getStatusCode());
        } catch (OutOfWorkflowScopeException ex) {
            logger.error(String.format("Failed to validate date (%1$s) before arrival point update for this this reservation %2$d ", visitDay.format(ControllerConstants.DATE_FORMAT_PATTERN), tokenInformation.getIdReservation()));
            throw ex.setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        }
    }

    private boolean checkIfArrivalPointChangePossible(TokenStoredRegistrationInformationModel tokenInformation, ConfirmArrivalPointModel requestModel, String responseLanguage, boolean processIsExistingReservationUpdate) throws DatabaseException, OutOfWorkflowScopeException, ReservationModificationException {

        // get existing reservation
        ReservationSummaryModel storedReservation = this.reservationService.getReservationSummary(tokenInformation.getIdReservation(), responseLanguage);

        int bookRequiredTickets = storedReservation.numberOfBookedStandardTickets == 0 ? WorkflowConstants.SIX_INTEGER : storedReservation.numberOfBookedStandardTickets;

        // get arrival point
        Optional<ArrivalPointModel> arrivalPoint = this.arrialPointService.getAvailablePlaces(requestModel.visitDay, tokenInformation.getIdRegistration(), responseLanguage, tokenInformation.getIdReservation()).stream().filter(listedArrivalPoint -> listedArrivalPoint.idArrivalPoint == requestModel.idArrivalPoint).findFirst();

        if (arrivalPoint == null) {
            String errorMessage = String.format("There is no arrival point data for this day : %1$s error occurred for this reservation action: %2$d ", requestModel.visitDay.format(ControllerConstants.DATETIME_FORMAT_PATTERN), tokenInformation.getIdReservation());
            logger.error(errorMessage);
            throw new OutOfWorkflowScopeException(errorMessage);
        }

        // get timeslot
        Optional<ArrivalPointOpenTimeslotModel> timeslot = arrivalPoint.get().opentimeSlots.stream().filter(listedTimeSlot -> listedTimeSlot.timeslotStart.equals(requestModel.timeslotStart)).findFirst();

        if (timeslot == null) {
            String errorMessage = String.format("There is no arrival point data for this timeslot : %1$s error occurred for this reservation action: %2$d ", requestModel.timeslotStart.format(ControllerConstants.DATETIME_FORMAT_PATTERN), tokenInformation.getIdReservation());
            logger.error(errorMessage);
            throw new OutOfWorkflowScopeException(errorMessage);
        }

        // if user wants to change timeslot, but there are no fast track tickets for the new slot
        if (processIsExistingReservationUpdate && timeslot.get().availableFastTrackPlaces < storedReservation.numberOfBookedFastTrackTickets && !requestModel.updateWithoutFastTrack) {
            String errorMessage = String.format("User tries to book ticket for a timeslot where there are no free fast track places, reservation id: %1$d", tokenInformation.getIdReservation());
            logger.error(errorMessage);
            throw new ReservationModificationException(errorMessage).setErrorCode(MessageStatusCodes.NO_FAST_TRACK_TICKETS.getStatusCode());
        }

        return timeslot.get().availablePlaces >= bookRequiredTickets;
    }
}
