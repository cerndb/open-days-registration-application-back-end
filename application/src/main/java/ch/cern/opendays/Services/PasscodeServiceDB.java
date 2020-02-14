//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Constants.EnvironmentConfigConstants;
import ch.cern.opendays.Enums.IsActiveStatusCodes;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Exceptions.ActionHistoryException;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.InterfacesDAO.PasscodeRepository;
import ch.cern.opendays.ModelsDAO.PasscodeModelDAO;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasscodeServiceDB implements PasscodeServiceInterface {

    private static final Logger logger = LogManager.getLogger(PasscodeServiceDB.class);

    private PasscodeRepository passcodeRepository;
    private ActionHistoryServiceInterface actionHistory;

    @Autowired
    public void setActionHistory(ActionHistoryServiceDB actionHistory) {
        this.actionHistory = actionHistory;
    }

    @Autowired
    public void setPasscodeRepository(PasscodeRepository passcodeRepository) {
        this.passcodeRepository = passcodeRepository;
    }

    // invalidate all active passcodes for the user and create a new
    @Override
    public void createNewEmailConfirmationPasscode(Long idRegistration, Integer idOperation) throws DatabaseException, ActionHistoryException {

        // invalidate the previous passcodes
        try {
            this.invalidateAllPreviousPasscode(idRegistration);
        } catch (DatabaseException ex) {
            logger.error(String.format("Failed to invalidate existing passcodes for new mail confirmation for this registartion %1$s", idRegistration));
            throw ex.setErrorCode(MessageStatusCodes.PASSCODE_CREATION_ERROR.getStatusCode());
        }

        // create passcode
        try {
            this.createNewPasscode(idRegistration, idOperation);
        } catch (DatabaseException ex) {
            logger.error(String.format("Failed to create new passcode for new mail confirmation for this registartion %1$s", idRegistration));
            throw ex.setErrorCode(MessageStatusCodes.PASSCODE_CREATION_ERROR.getStatusCode());
        }

        logger.info(String.format("Passcode has been created for this registration: %1$d", idRegistration));
        // insert here
        try {
            this.actionHistory.trackEmailConfirmationPasscodeGeneration(idRegistration, idOperation);
        } catch (ActionHistoryException ex) {
            logger.error("Failed to create action history record about new email confirmation passcode");
            throw ex;
        }

    }

    @Override
    public boolean validateEmailWithPasscode(Long idRegistration, String passcode) throws DatabaseException, OutOfWorkflowScopeException {

        String referencePasscode;

        try {
            referencePasscode = this.getPasscode(idRegistration);
        } catch (DatabaseException ex) {
            logger.error(String.format("Failed to load passcode information for this registration: %1$d", idRegistration));
            throw ex;
        } catch (OutOfWorkflowScopeException ex) {
            try {
                this.invalidateAllPreviousPasscode(idRegistration);
            } catch (DatabaseException e) {
                logger.error(String.format("Failed to invalidate old passcodes for this registration %1$d, user needs to get a new token which will invalidate all old", idRegistration));
                throw ex;
            }

            logger.info("We have successfully cleared the old passcodes");
            throw ex;
        }

        return passcode.equals(referencePasscode);
    }

    @Override
    public String getPasscodeForTesting(Long idRegistration) throws DatabaseException, OutOfWorkflowScopeException {

        String passcode = null;

        try {
            List<PasscodeModelDAO> passcodes = this.passcodeRepository.getPasscodeByRegistrationIdAndStatus(idRegistration, IsActiveStatusCodes.IS_ACTIVE.getIsActiveStatusCode());
            if (passcodes.size() == 1) {
                passcode = passcodes.get(0).getPasscode();
            }

        } catch (Exception ex) {
            logger.error("Failed to load passcode for testing");
            throw new DatabaseException("Failed to load passcode for testing", ex);
        }

        if (passcode == null) {
            logger.error("Multiple or no passcodes for that mail address");
            throw new OutOfWorkflowScopeException("Multiple or no passcodes for that mail address");
        }

        return passcode;
    }

    private String getPasscode(Long idRegistration) throws DatabaseException, OutOfWorkflowScopeException {
        String passcode = "";

        List<PasscodeModelDAO> passcodes;
        try {
            passcodes = this.passcodeRepository.getPasscodeByRegistrationIdAndStatus(idRegistration, IsActiveStatusCodes.IS_ACTIVE.getIsActiveStatusCode());

            if (passcodes.size() == 1) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime expiration = passcodes.get(0).getTimestampOfCreation().plusMinutes(EnvironmentConfigConstants.TOKEN_GENERATION_VALIDITY_MINUTES);

                // check passcode date
                if (expiration.isAfter(now)
                        && passcodes.get(0).getPasscodeIsActive() == IsActiveStatusCodes.IS_ACTIVE.getIsActiveStatusCode()) {
                    passcode = passcodes.get(0).getPasscode();
                } else {
                    try {
                        this.invalidateAllPreviousPasscode(idRegistration);
                    } catch (DatabaseException ex) {
                        logger.error(String.format("Failed to invalidate expired passcode for this registration id : %1$d.", idRegistration));
                        throw ex.setErrorCode(MessageStatusCodes.PASSCODE_VALIDATION_TRY_AGAIN.getStatusCode());
                    }
                }

            } else {
                throw new OutOfWorkflowScopeException(String.format("Too many passcode is active, we will try to clean the old ones, but user needs to request a new for this registration id: %1$d" + idRegistration))
                        .setErrorCode(MessageStatusCodes.TOO_MANY_ACTIVE_PASSCODE.getStatusCode());
            }

        } catch (DatabaseException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error(String.format("Failed to load passcode objects for this registration: %1$d", idRegistration));
            throw new DatabaseException(String.format("Failed to load passcode objects for this registration: %1$d", idRegistration), ex)
                    .setErrorCode(MessageStatusCodes.PASSCODE_VALIDATION_TRY_AGAIN.getStatusCode());
        }

        return passcode;
    }

    @Override
    public void invalidateAllPreviousPasscode(Long idRegistration) throws DatabaseException {
        try {
            this.passcodeRepository.updatePasscodeSetPasscodeStatus(IsActiveStatusCodes.INACTIVE.getIsActiveStatusCode(), idRegistration);
        } catch (Exception ex) {
            logger.error(String.format("Failed to invalidate existing passcodes for this registartion: %1$d", idRegistration), ex);
            throw new DatabaseException(String.format("Failed to invalidate existing passcodes for this registartion: %1$d", idRegistration), ex);
        }
    }

    private void createNewPasscode(Long idRegistration, Integer idOperation) throws DatabaseException {
        PasscodeModelDAO storeObject = new PasscodeModelDAO(idRegistration, idOperation);
        try {
            this.passcodeRepository.save(storeObject);
        } catch (Exception ex) {
            logger.error(String.format("Failed to insert new passcode for this idRegistration: %1$d", idRegistration));
            throw new DatabaseException(String.format("Failed to insert new passcode for this idRegistration: %1$d", idRegistration), ex);
        }
    }

    @Override
    public void usePasscode(Long idRegistration) throws DatabaseException, OutOfWorkflowScopeException, ActionHistoryException {

        PasscodeModelDAO passcode = new PasscodeModelDAO();

        List<PasscodeModelDAO> passcodes;

        try {
            passcodes = this.passcodeRepository.getPasscodeByRegistrationIdAndStatus(idRegistration, IsActiveStatusCodes.IS_ACTIVE.getIsActiveStatusCode());
        } catch (Exception ex) {
            logger.error(String.format("Failed to load passcode for usage for this idRegistration: %1$d", idRegistration));
            throw new DatabaseException(String.format("Failed to load passcode for usage for this idRegistration: %1$d", idRegistration), ex)
                    .setErrorCode(MessageStatusCodes.PASSCODE_VALIDATION_TRY_AGAIN.getStatusCode());
        }

        if (passcodes.size() == 1) {
            passcode = passcodes.get(0);
            passcode.setPasscodeIsActive(IsActiveStatusCodes.INACTIVE.getIsActiveStatusCode());
            passcode.setTimestampOfUsage(LocalDateTime.now());
        } else {
            logger.error(String.format("There are too many or few active passcodes for this registration id: %1$d"), idRegistration);
            throw new OutOfWorkflowScopeException(String.format("There are too many or few active passcodes for this registration id: %1$d", idRegistration))
                    .setErrorCode(MessageStatusCodes.TOO_MANY_ACTIVE_PASSCODE.getStatusCode());
        }

        try {
            this.passcodeRepository.save(passcode);
        } catch (Exception ex) {
            logger.error(String.format("Failed update passcode with usage for this idRegistration: %1$d", idRegistration));
            throw new DatabaseException(String.format("Failed update passcode with usage for this idRegistration: %1$d", idRegistration), ex)
                    .setErrorCode(MessageStatusCodes.PASSCODE_VALIDATION_TRY_AGAIN.getStatusCode());
        }

        try {
            this.actionHistory.trackPasscodeValidationPassedTokenCreated(idRegistration);
        } catch (ActionHistoryException ex) {
            logger.error(String.format("Failed to create action record for token creation for this registration id: %1$d", idRegistration));
            throw ex;
        }
    }

}
