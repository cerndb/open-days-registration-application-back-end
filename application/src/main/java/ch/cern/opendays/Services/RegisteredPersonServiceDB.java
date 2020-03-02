//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Constants.ControllerConstants;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Enums.RegistrationProcessStatusCodes;
import ch.cern.opendays.Exceptions.ActionHistoryException;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.InterfacesDAO.RegisteredPersonRepository;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;
import ch.cern.opendays.ModelsDAO.RegisteredPersonModelDAO;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisteredPersonServiceDB implements RegisteredPersonServiceInterface {

    private ActionHistoryServiceInterface actionHistory;
    private RegisteredPersonRepository personRepository;
    private static final Logger logger = LogManager.getLogger(RegisteredPersonServiceDB.class);

    @Autowired
    public void setPersonRepository(RegisteredPersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Autowired
    public void setActionHistory(ActionHistoryServiceDB actionHistory) {
        this.actionHistory = actionHistory;
    }

    private RegisteredPersonModelDAO saveModel(RegisteredPersonModelDAO storeObject) throws DatabaseException {
        try {
            return this.personRepository.save(storeObject);
        } catch (Exception ex) {
            logger.error(String.format("Failed to store registration information with these data: %1$s", storeObject.toString()));
            throw new DatabaseException("Failed to store changes for person registration into the database with these information: " + storeObject.toString(), ex);
        }
    }

    @Override
    public RegisteredPersonModelDAO getRegisteredPerson(Long idRegistration) throws DatabaseException, OutOfWorkflowScopeException {

        try {
            List<RegisteredPersonModelDAO> registrations = this.personRepository.findByIdRegistration(idRegistration);

            if (registrations.size() == 1) {
                return registrations.get(0);
            }

            throw new OutOfWorkflowScopeException(String.format("To many registrations for this registration id: %1$d", idRegistration));

        } catch (Exception ex) {
            throw new DatabaseException("Failed to load registration information", ex);
        }
    }

    private RegisteredPersonModelDAO registerNewPerson(RegisteredPersonModelDAO newRegistration) throws DatabaseException, ActionHistoryException {

        RegisteredPersonModelDAO databaseStoredRegistration = null;

        // register new person
        try {
            databaseStoredRegistration = saveModel(newRegistration);
        } catch (DatabaseException ex) {
            logger.error("Failed to register new person");
            throw ex.setErrorCode(MessageStatusCodes.PASSCODE_CREATION_ERROR.getStatusCode());
        }

        // track new registration command
        try {
            this.actionHistory.trackShowInterestAction(databaseStoredRegistration);
        } catch (ActionHistoryException ex) {
            logger.error("Failed to create action history record for new person registration");
            throw ex;
        }

        return databaseStoredRegistration;
    }

    @Override
    public Long getExistingApplicationId(String mailAddress) throws OutOfWorkflowScopeException, DatabaseException {

        // set default registartion id which out of normal id range
        Long idRegistration = ControllerConstants.DEFAULT_USER_ID;

        List<RegisteredPersonModelDAO> existingRegistrations;

        // get registrations based on mail address
        try {
            existingRegistrations = this.personRepository.findByContactMailAddress(mailAddress);
        } catch (Exception ex) {
            logger.error(String.format("Failed to existing application for this mail address: %1$s", mailAddress), ex);
            throw new DatabaseException(String.format("Failed to load data from database for this mail address: %s ", mailAddress), ex)
                    .setErrorCode(MessageStatusCodes.PASSCODE_CREATION_ERROR.getStatusCode());
        }

        // population method happens in the try catch, no need to handle null
        // registration id should be only one for one person
        if (existingRegistrations.size() == 1) {
            idRegistration = existingRegistrations.get(0).getIdRegistration();
        } else if (existingRegistrations.size() > 1) {
            logger.error(String.format("Multiple registration records for this mail address: %1$s", mailAddress));
            throw new OutOfWorkflowScopeException(String.format("Multiple registration records for this mail address: %1$s", mailAddress))
                    .setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        }

        return idRegistration;
    }

    // register person with e-mail if it hasn't got a history
    @Override
    public Long showInterest(String mailAddress) throws DatabaseException, ActionHistoryException {

        RegisteredPersonModelDAO databaseStoredRegistration = null;

        // register new person
        try {
            databaseStoredRegistration = this.registerNewPerson(new RegisteredPersonModelDAO(mailAddress));
        } catch (DatabaseException ex) {
            logger.error(String.format("Failed to show interest for this mail address: %1$s", mailAddress));
            throw ex;
        } catch (ActionHistoryException ex) {
            logger.error(String.format("Show interest failed to create action history for this mail address: %1$s", mailAddress));
            throw ex;
        }

        return databaseStoredRegistration.getIdRegistration();
    }

    @Override
    public TokenStoredRegistrationInformationModel getRegistrationInformationForToken(String mailAddress) throws OutOfWorkflowScopeException, DatabaseException {

        List<RegisteredPersonModelDAO> existingRegistrations;

        // get registrations based on mail address
        try {
            existingRegistrations = this.personRepository.findByContactMailAddress(mailAddress);
        } catch (Exception ex) {
            logger.error(String.format("Failed to get token for this mail address: %s ", mailAddress));
            throw new DatabaseException(String.format("Failed to get token for this mail address: %s ", mailAddress), ex)
                    .setErrorCode(MessageStatusCodes.PASSCODE_VALIDATION_TRY_AGAIN.getStatusCode());
        }

        // populate token registration information object
        if (existingRegistrations.size() == 1) {
            return new TokenStoredRegistrationInformationModel(existingRegistrations.get(0));

        } else if (existingRegistrations.size() > 1) {
            logger.error(String.format("Multiple mail addresses where mail address: %1$s, token validation terminates", mailAddress));
            throw new OutOfWorkflowScopeException(String.format("Multiple mail addresses where mail address: %1$s, token validation should terminate", mailAddress))
                    .setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        }

        logger.error(String.format("There is no registartion for this mail address: %1$s, token validation terminates", mailAddress));
        throw new OutOfWorkflowScopeException(String.format("There is no registartion for this mail address: %1$s, token validation terminates", mailAddress))
                .setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
    }

    @Override
    public void emailAddressIsConfirmed(Long idRegistration) throws DatabaseException, OutOfWorkflowScopeException, ActionHistoryException {

        RegisteredPersonModelDAO existingRegistration = new RegisteredPersonModelDAO();

        List<RegisteredPersonModelDAO> registrationList;

        // get registrations based on mail address
        try {
            registrationList = this.personRepository.findByIdRegistration(idRegistration);
        } catch (Exception ex) {
            logger.error(String.format("Failed to load registration for mail address confirmation where registration id is this : %1$d ", idRegistration));
            throw new DatabaseException(String.format("Failed to load registration for mail address confirmation where registration id is this : %1$d ", idRegistration), ex);
        }

        if (registrationList.size() == 1) {
            existingRegistration = registrationList.get(0);
            existingRegistration.setRegistrationProcessStatusCode(RegistrationProcessStatusCodes.EMAIL_CONFIRMED.getStatusCode());
        } else {
            logger.error(String.format("Data error, there are multiple registration records for the same id where id : %1$d, mail confirmation process terminates", idRegistration));
            throw new OutOfWorkflowScopeException(String.format("Data error, there are multiple registration records for the same id where id : %1$d, mail confirmation process terminates", idRegistration))
                    .setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        }

        try {
            this.saveModel(existingRegistration);
        } catch (DatabaseException ex) {
            logger.error(String.format("Failed to update registration record status to mail confirmed for this registration: %1$d", idRegistration));
            throw ex.setErrorCode(MessageStatusCodes.PASSCODE_VALIDATION_TRY_AGAIN.getStatusCode());
        }

        logger.info("User successfully confirmed e-mail address");

        // track new registration command
        try {
            this.actionHistory.trackEmailIsConfirmedAction(existingRegistration);
        } catch (ActionHistoryException ex) {
            logger.error(String.format("Failed to track e-mail confirmation change for this registration id: %1$d", idRegistration));
            throw ex;
        }

    }

    @Override
    public String getMailAddress(Long idRegistration) throws DatabaseException, OutOfWorkflowScopeException {

        Optional<RegisteredPersonModelDAO> registration = null;
        try {
            registration = this.personRepository.findById(idRegistration);
        } catch (Exception ex) {
            logger.error(String.format("Failed to load registartion: %1$d for privilage date checking", idRegistration));
            throw new DatabaseException(String.format("Failed to load registartion: %1$d for privilage date checking", idRegistration), ex)
                    .setErrorCode(MessageStatusCodes.ARRIVAL_POINT_GET_AVAILABLE_DATES_LOAD_FAILED.getStatusCode());
        }
        if (registration == null) {
            logger.error(String.format("No registartion found for this registration %1$d for privilage date checking", idRegistration));
            throw new OutOfWorkflowScopeException(String.format("No registartion found for this registration %1$d for privilage date checking", idRegistration))
                    .setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        }

        return registration.get().getContactMailAddress();
    }
}
