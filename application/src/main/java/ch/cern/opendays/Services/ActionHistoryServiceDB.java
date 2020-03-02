//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Constants.UserActionConstants;
import ch.cern.opendays.Enums.ObjectIds;
import ch.cern.opendays.Enums.ReservationStatusCodes;
import ch.cern.opendays.Enums.UserActions;
import ch.cern.opendays.Exceptions.ActionHistoryException;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.InterfacesDAO.ActionHistoryRepository;
import ch.cern.opendays.Models.ActionHistoryModel;
import ch.cern.opendays.ModelsDAO.ActionHistoryModelDAO;
import ch.cern.opendays.ModelsDAO.RegisteredPersonModelDAO;
import java.time.LocalDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActionHistoryServiceDB implements ActionHistoryServiceInterface {

    private ActionHistoryRepository historyRepository;
    private RegisteredPersonServiceInterface registeredPersonService;
    private static final Logger logger = LogManager.getLogger(ActionHistoryServiceDB.class);

    @Autowired
    public void setHistoryRepository(ActionHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Autowired
    public void setRegisteredPersonService(RegisteredPersonServiceDB registeredPersonService) {
        this.registeredPersonService = registeredPersonService;
    }

    private void trackUserAction(ActionHistoryModel historyAction) throws ActionHistoryException {
        ActionHistoryModelDAO storeObject = new ActionHistoryModelDAO(historyAction);

        try {
            this.historyRepository.save(storeObject);
        } catch (Exception ex) {
            logger.error(String.format("Failed to store action with these details: %1$s", storeObject.toString()), ex);
            throw new ActionHistoryException(String.format("Failed to store action with these details: %1$s", storeObject.toString()), ex);
        }

    }

    // create action history record for e-mail address confirmation
    @Override
    public void trackEmailConfirmationPasscodeGeneration(Long idRegistration, Integer idOperation) throws ActionHistoryException {

        ActionHistoryModel userAction = new ActionHistoryModel()
                .setChangeComment("Email confirmation passcode generated for user")
                .setChangeDate(LocalDateTime.now())
                .setIdAction(UserActions.SYSTEM_PASSCODE_GENERATION.getUserAction())
                .setIdChangeObject(idRegistration.toString())
                .setIdModifier(UserActionConstants.SYSTEM_GUID)
                .setObjectType(ObjectIds.PASSCODE_OBJECT.getObjectId());

        // track passcode creation
        try {
            this.trackUserAction(userAction);
        } catch (ActionHistoryException ex) {
            logger.error("Passcode generation action history store failed");
            throw ex;
        }
    }

    // create show interest workflow actions
    @Override
    public void trackShowInterestAction(RegisteredPersonModelDAO registeredPerson) throws ActionHistoryException {

        ActionHistoryModel userAction = new ActionHistoryModel(registeredPerson)
                .setIdAction(UserActions.USER_SHOWS_INTEREST.getUserAction())
                .setChangeComment(UserActionConstants.USER_SHOWED_INTEREST);

        logger.info(String.format("User showed interest with the following informations", userAction.toString()));

        try {
            trackUserAction(userAction);
        } catch (ActionHistoryException ex) {
            logger.error("Show interest user action history store failed");
            throw ex;
        }
    }

    // create show interest workflow actions
    @Override
    public void trackEmailIsConfirmedAction(RegisteredPersonModelDAO registeredPerson) throws ActionHistoryException {

        ActionHistoryModel userAction = new ActionHistoryModel(registeredPerson)
                .setIdAction(UserActions.USER_CONFIRMS_EMAIL_ADDRESS.getUserAction())
                .setChangeComment(UserActionConstants.USER_CONFIRMED_MAIL);

        try {
            this.trackUserAction(userAction);
        } catch (ActionHistoryException ex) {
            logger.error("User has confirmed email action history store failed");
            throw ex;
        }
    }

    @Override
    public void trackPasscodeValidationPassedTokenCreated(Long idRegistration) throws ActionHistoryException {

        try {

            RegisteredPersonModelDAO registerdPerson = this.registeredPersonService.getRegisteredPerson(idRegistration);

            ActionHistoryModel userAction = new ActionHistoryModel()
                    .setChangeComment("Passcode validation successful, token generated")
                    .setChangeDate(LocalDateTime.now())
                    .setIdAction(UserActions.PASSCODE_VALIDATED_TOKEN_CREATED.getUserAction())
                    .setIdChangeObject(idRegistration.toString())
                    .setIdModifier(registerdPerson.getIdPerson())
                    .setObjectType(ObjectIds.PASSCODE_OBJECT.getObjectId());

            logger.info(String.format("Passcode validation successful, token generated for this registration: %1$d", idRegistration));

            this.trackUserAction(userAction);
        } catch (ActionHistoryException ex) {
            logger.error(String.format("Failed to create passcode used and token generated action for this registration id: %1$d", idRegistration));
            throw ex;
        } catch (DatabaseException | OutOfWorkflowScopeException ex) {
            logger.error(String.format("Failed to load registration information for creating passcode used and token generated action for this registration id : %1$d", idRegistration));
            throw new ActionHistoryException(String.format("Failed to load registration information for creating passcode used and token generated action for this registration id : %1$d", idRegistration), ex);
        }
    }

    @Override
    public void trackUserConfirmedVisitorDetails(Long idReservation, Long idRegistration) throws ActionHistoryException {

        try {

            RegisteredPersonModelDAO registerdPerson = this.registeredPersonService.getRegisteredPerson(idRegistration);

            ActionHistoryModel userAction = new ActionHistoryModel()
                    .setChangeComment("Visitor confirmed visitor details for reservation")
                    .setChangeDate(LocalDateTime.now())
                    .setIdAction(UserActions.VISITOR_DETAILS_CONFIRMED.getUserAction())
                    .setIdChangeObject(idReservation.toString())
                    .setIdModifier(registerdPerson.getIdPerson())
                    .setObjectType(ObjectIds.VISITOR_DETAILS_OBJECTS.getObjectId());

            logger.info(String.format("Visitor confirmed visitor details for this reservation: %1$d", idReservation));

            this.trackUserAction(userAction);
        } catch (ActionHistoryException ex) {
            logger.error("Failed to create visitor detials confirmation action");
            throw ex;
        } catch (DatabaseException | OutOfWorkflowScopeException ex) {
            logger.error(String.format("Failed to load registration for this registration id: %1$d for visitor details action history creation", idRegistration));
            throw new ActionHistoryException(String.format("Failed to load registration for this registration id: %1$d for visitor details action history creation", idRegistration), ex);
        }

    }

    @Override
    public void trackUserConfirmedVisitorTransportTypes(Long idReservation, Long idRegistration) throws ActionHistoryException {

        try {

            RegisteredPersonModelDAO registerdPerson = this.registeredPersonService.getRegisteredPerson(idRegistration);

            ActionHistoryModel userAction = new ActionHistoryModel()
                    .setChangeComment("Visitor confirmed visitor transfor types for reservation")
                    .setChangeDate(LocalDateTime.now())
                    .setIdAction(UserActions.VISITOR_TRANSPORT_TYPES_CONFIRMED.getUserAction())
                    .setIdChangeObject(idReservation.toString())
                    .setIdModifier(registerdPerson.getIdPerson())
                    .setObjectType(ObjectIds.VISITOR_TRANSPORT_TYPES.getObjectId());

            logger.info(String.format("Visitor confirmed visitor transfor types for this reservation: %1$d", idReservation));

            this.trackUserAction(userAction);
        } catch (ActionHistoryException ex) {
            logger.error("Failed to create visitor transport type confirmation action");
            throw ex;
        } catch (DatabaseException | OutOfWorkflowScopeException ex) {
            logger.error(String.format("Failed to load registration for this registration id: %1$d for visitor transport type action history creation", idRegistration));
            throw new ActionHistoryException(String.format("Failed to load registration for this registration id: %1$d for visitor transport type action history creation", idRegistration), ex);
        }

    }

    @Override
    public void trackUserConfirmedArrivalPoint(Long idReservation, Long idRegistration) throws ActionHistoryException {

        try {

            RegisteredPersonModelDAO registerdPerson = this.registeredPersonService.getRegisteredPerson(idRegistration);

            ActionHistoryModel userAction = new ActionHistoryModel()
                    .setChangeComment("Visitor confirmed arrival point for reservation")
                    .setChangeDate(LocalDateTime.now())
                    .setIdAction(UserActions.ARRIVAL_POINT_CONFIRMED.getUserAction())
                    .setIdChangeObject(idReservation.toString())
                    .setIdModifier(registerdPerson.getIdPerson())
                    .setObjectType(ObjectIds.RESERVATION_OBJECT.getObjectId());

            logger.info(String.format("Visitor confirmed arrival point for this reservation: %1$d", idReservation));

            this.trackUserAction(userAction);
        } catch (ActionHistoryException ex) {
            logger.error("Failed to create visitor arrival point confirmation action");
            throw ex;
        } catch (DatabaseException | OutOfWorkflowScopeException ex) {
            logger.error(String.format("Failed to load registration for this registration id: %1$d for visitor arrival point action history creation", idRegistration));
            throw new ActionHistoryException(String.format("Failed to load registration for this registration id: %1$d for visitor arrival point action history creation", idRegistration), ex);
        }

    }

    @Override
    public void trackUserCreatedNewReservation(Long idReservation, Long idRegistration) throws ActionHistoryException {

        try {

            RegisteredPersonModelDAO registerdPerson = this.registeredPersonService.getRegisteredPerson(idRegistration);

            ActionHistoryModel userAction = new ActionHistoryModel()
                    .setChangeComment("New reservation record has been created")
                    .setChangeDate(LocalDateTime.now())
                    .setIdAction(UserActions.NEW_RESERVATION.getUserAction())
                    .setIdChangeObject(idReservation.toString())
                    .setIdModifier(registerdPerson.getIdPerson())
                    .setObjectType(ObjectIds.RESERVATION_OBJECT.getObjectId());

            logger.info(String.format("New reservation has been created for this registration  %1$d", idRegistration));

            this.trackUserAction(userAction);
        } catch (ActionHistoryException ex) {
            logger.error("Failed to create new reservation action action");
            throw ex;
        } catch (DatabaseException | OutOfWorkflowScopeException ex) {
            logger.error(String.format("Failed to load registration for this registration id: %1$d for new reservation action history creation", idRegistration));
            throw new ActionHistoryException(String.format("Failed to load registration for this registration id: %1$d for new reservation action history creation", idRegistration), ex);
        }

    }

    @Override
    public void trackReservationStatusChange(Long idReservation, Long idRegistration, ReservationStatusCodes statusChangeEnum) throws ActionHistoryException {

        try {

            RegisteredPersonModelDAO registerdPerson = this.registeredPersonService.getRegisteredPerson(idRegistration);

            ActionHistoryModel userAction = new ActionHistoryModel()
                    .setChangeDate(LocalDateTime.now())
                    .setIdChangeObject(idReservation.toString())
                    .setIdModifier(registerdPerson.getIdPerson())
                    .setObjectType(ObjectIds.RESERVATION_OBJECT.getObjectId());

            switch (statusChangeEnum) {
                case FINAL:
                    userAction
                            .setIdAction(UserActions.RESERVATION_STATUS_FINAL.getUserAction())
                            .setChangeComment("Reservation status code changed to final");
                    logger.info(String.format("Reservation status code changed to final for this reservation  %1$d", idReservation));
                    break;
                case CANCELLED:
                    userAction
                            .setIdAction(UserActions.RESERVATION_STATUS_CANCELLED.getUserAction())
                            .setChangeComment("Reservation status code changed to cancelled");
                    logger.info(String.format("Reservation status code changed to cancelled for this reservation  %1$d", idReservation));
                    break;
                default:
                    logger.error(String.format("Reservation status change not defined for action tracking for this reservation: %1$d", idReservation));
                    throw new OutOfWorkflowScopeException(String.format("Reservation status change not defined for action tracking for this reservation: %1$d", idReservation));
            }

            this.trackUserAction(userAction);
        } catch (ActionHistoryException ex) {
            logger.error("Failed to create update reservation satatus action");
            throw ex;
        } catch (DatabaseException | OutOfWorkflowScopeException ex) {
            logger.error(String.format("Failed to load registration for this registration id: %1$d for reservation status update action history creation", idRegistration));
            throw new ActionHistoryException(String.format("Failed to load registration for this registration id: %1$d for reservation status update action history creation", idRegistration), ex);
        }

    }

    @Override
    public void trackUserUpdatedArrivalPoint(Long idReservation, Long idRegistration) throws ActionHistoryException {

        try {

            RegisteredPersonModelDAO registerdPerson = this.registeredPersonService.getRegisteredPerson(idRegistration);

            ActionHistoryModel userAction = new ActionHistoryModel()
                    .setChangeComment("Visitor updated arrival point for reservation")
                    .setChangeDate(LocalDateTime.now())
                    .setIdAction(UserActions.UPDATE_ARRIVAL_POINT.getUserAction())
                    .setIdChangeObject(idReservation.toString())
                    .setIdModifier(registerdPerson.getIdPerson())
                    .setObjectType(ObjectIds.RESERVATION_OBJECT.getObjectId());

            logger.info(String.format("Visitor updated arrival point for this reservation: %1$d", idReservation));

            this.trackUserAction(userAction);
        } catch (ActionHistoryException ex) {
            logger.error("Failed to create visitor arrival point update action");
            throw ex;
        } catch (DatabaseException | OutOfWorkflowScopeException ex) {
            String errorMessage = String.format("Failed to load registration for this registration id: %1$d for visitor arrival point update action history creation", idRegistration);
            logger.error(errorMessage);
            throw new ActionHistoryException(errorMessage, ex);
        }

    }

    @Override
    public void trackUserUpdatedVisitorTransportTypes(Long idReservation, Long idRegistration) throws ActionHistoryException {

        try {

            RegisteredPersonModelDAO registerdPerson = this.registeredPersonService.getRegisteredPerson(idRegistration);

            ActionHistoryModel userAction = new ActionHistoryModel()
                    .setChangeComment("Visitor updated visitor transfor types for reservation")
                    .setChangeDate(LocalDateTime.now())
                    .setIdAction(UserActions.UPDATE_TRANSPORT_TYPES.getUserAction())
                    .setIdChangeObject(idReservation.toString())
                    .setIdModifier(registerdPerson.getIdPerson())
                    .setObjectType(ObjectIds.VISITOR_TRANSPORT_TYPES.getObjectId());

            logger.info(String.format("Visitor updated visitor transfor types for this reservation: %1$d", idReservation));

            this.trackUserAction(userAction);
        } catch (ActionHistoryException ex) {
            logger.error("Failed to create visitor transport type update action");
            throw ex;
        } catch (DatabaseException | OutOfWorkflowScopeException ex) {
            String errorMessage = String.format("Failed to load registration for this registration id: %1$d for visitor transport type update action history creation", idRegistration);
            logger.error(errorMessage);
            throw new ActionHistoryException(errorMessage, ex);
        }

    }

    @Override
    public void trackUserUpdatedVisitorDetails(Long idReservation, Long idRegistration) throws ActionHistoryException {

        try {

            RegisteredPersonModelDAO registerdPerson = this.registeredPersonService.getRegisteredPerson(idRegistration);

            ActionHistoryModel userAction = new ActionHistoryModel()
                    .setChangeComment("Visitor updated visitor details for reservation")
                    .setChangeDate(LocalDateTime.now())
                    .setIdAction(UserActions.UPDATE_VISITOR_DETAILS.getUserAction())
                    .setIdChangeObject(idReservation.toString())
                    .setIdModifier(registerdPerson.getIdPerson())
                    .setObjectType(ObjectIds.VISITOR_DETAILS_OBJECTS.getObjectId());

            logger.info(String.format("Visitor updated visitor details for this reservation: %1$d", idReservation));

            this.trackUserAction(userAction);
        } catch (ActionHistoryException ex) {
            logger.error("Failed to create visitor detials update action");
            throw ex;
        } catch (DatabaseException | OutOfWorkflowScopeException ex) {
            String errorMessage = String.format("Failed to load registration for this registration id: %1$d for visitor details update action history creation", idRegistration);
            logger.error(errorMessage);
            throw new ActionHistoryException(errorMessage, ex);
        }

    }


    public void trackUserRequestConfirmationMail(Long idReservation, Long idRegistration) throws ActionHistoryException{

        try {

            RegisteredPersonModelDAO registerdPerson = this.registeredPersonService.getRegisteredPerson(idRegistration);

            ActionHistoryModel userAction = new ActionHistoryModel()
                    .setChangeComment("Visitor requested mail re-send")
                    .setChangeDate(LocalDateTime.now())
                    .setIdAction(UserActions.USER_RE_REQUEST_CONFIRMATION_MAIL.getUserAction())
                    .setIdChangeObject(idReservation.toString())
                    .setIdModifier(registerdPerson.getIdPerson())
                    .setObjectType(ObjectIds.MAIL_OBJECT.getObjectId());

            logger.info(String.format("Visitor updated visitor details for this reservation: %1$d", idReservation));

            this.trackUserAction(userAction);
        } catch (ActionHistoryException ex) {
            logger.error("Failed to create visitor detials update action");
            throw ex;
        } catch (DatabaseException | OutOfWorkflowScopeException ex) {
            String errorMessage = String.format("Failed to load registration for this registration id: %1$d for visitor details update action history creation", idRegistration);
            logger.error(errorMessage);
            throw new ActionHistoryException(errorMessage, ex);
        }


    }
}
