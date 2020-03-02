//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Controllers;

import ch.cern.opendays.Constants.ControllerConstants;
import ch.cern.opendays.Constants.EnvironmentConfigConstants;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Enums.PasscodeOperationCodes;
import ch.cern.opendays.Enums.UserValidationTypesCodes;
import ch.cern.opendays.Exceptions.ActionHistoryException;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Exceptions.InvalidReCaptchaException;
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.Models.DatabaseUpdatedResponseModel;
import ch.cern.opendays.Models.MessageResponseAPIModel;
import ch.cern.opendays.Models.RequestPasscodeModel;
import ch.cern.opendays.Providers.ResponseMessageProvider;
import ch.cern.opendays.Services.CaptchaServiceGoogle;
import ch.cern.opendays.Services.CaptchaServiceInterface;
import ch.cern.opendays.Services.MailActionHistoryServiceDB;
import ch.cern.opendays.Services.MailActionHistoryServiceInterface;
import ch.cern.opendays.Services.PasscodeServiceDB;
import ch.cern.opendays.Services.PasscodeServiceInterface;
import ch.cern.opendays.Services.RegisteredPersonServiceDB;
import ch.cern.opendays.Services.RegisteredPersonServiceInterface;
import ch.cern.opendays.UserValidationSettings;
import ch.cern.opendays.Validators.EmailAddressValidator;
import ch.cern.opendays.Validators.StringValidator;
import ch.cern.opendays.Validators.TokenValidator;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestPasscodeController {

    private RegisteredPersonServiceInterface registeredPersonService;
    private PasscodeServiceInterface passcodeService;
    private CaptchaServiceInterface captchaService;
    private UserValidationSettings userValidationSettings;
    private MailActionHistoryServiceInterface mailActionHistoryService;
    private static final Logger logger = LogManager.getLogger(RequestPasscodeController.class);

    @Autowired
    public void setUserValidationSettings(UserValidationSettings userValidationSettings) {
        this.userValidationSettings = userValidationSettings;
    }

    @Autowired
    public void setCaptchaService(CaptchaServiceGoogle captchaService) {
        this.captchaService = captchaService;
    }

    @Autowired
    public void setPasscodeService(PasscodeServiceDB passcodeService) {
        this.passcodeService = passcodeService;
    }

    @Autowired
    public void setRegistredPersonService(RegisteredPersonServiceDB registeredPersonServiceDB) {
        this.registeredPersonService = registeredPersonServiceDB;
    }

    @Autowired
    public void setMailActionHistoryService(MailActionHistoryServiceDB mailActionHistoryService) {
        this.mailActionHistoryService = mailActionHistoryService;
    }

    @PostMapping(path = "/request-passcode", consumes = "application/json", produces = "application/json")
    public MessageResponseAPIModel<DatabaseUpdatedResponseModel> requestPasscode(@RequestBody RequestPasscodeModel requestPasscode, HttpServletRequest request) {

        // set preferred language
        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        // requestor seems to be a robot or request is suspicios
        if (!this.validateRequestor(requestPasscode, request.getRemoteAddr())) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.INCORRECT_API_INPUT.getStatusCode(), responseLanguage);
        }

        // validate request passcode model
        if (!this.validateRequestPasscodeModel(requestPasscode)) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.INCORRECT_API_INPUT.getStatusCode(), responseLanguage);
        }

        Long idRegistration;

        // if user has history get the registartion id
        // if user doesn't have any history id is the default
        try {
            idRegistration = this.registeredPersonService.getExistingApplicationId(requestPasscode.contactMailAddress);
        } catch (OutOfWorkflowScopeException ex) {
            logger.error(String.format("System wanted to create passcode, but mail address has multiple registration record %1$s", requestPasscode.contactMailAddress));
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (DatabaseException ex) {
            logger.error(String.format("Failed to load registration information for this mail address: %1$s for passcode generation", requestPasscode.contactMailAddress));
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }

        try {

            // if user hasn't got history we create a new history record
            if (idRegistration.equals(ControllerConstants.DEFAULT_USER_ID)) {
                try {
                    idRegistration = this.registeredPersonService.showInterest(requestPasscode.contactMailAddress);
                } catch (ActionHistoryException ex) {
                    logger.info(String.format("Failed to show interest for this mail address: %1$s , but we let the workflow to continue", requestPasscode.contactMailAddress));
                }
            }

            try {
                this.passcodeService.createNewEmailConfirmationPasscode(idRegistration, PasscodeOperationCodes.CONFIRM_EMAIL_ADDRESS.getPasscodeOperationCode());
            } catch (ActionHistoryException ex) {
                logger.info(String.format("Failed to generate passcode generation action this mail address: %1$s , but we let the workflow to continue", requestPasscode.contactMailAddress));
            }

            // create mail record
            this.mailActionHistoryService.reuqestPasscodeMail(idRegistration);

            return ResponseMessageProvider.returnDataMessage(new DatabaseUpdatedResponseModel(true), MessageStatusCodes.OK.getStatusCode(), responseLanguage);

        } catch (DatabaseException ex) {
            logger.error(String.format("Passcode generation workflow failed for this mail address : %1$s which has this registration number: %2$d", requestPasscode.contactMailAddress, idRegistration));
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }

    }

    // check if user is a robot
    private boolean validateRequestor(RequestPasscodeModel requestPassCode, String requestorIP) {

        boolean requestorIsValid = false;

        // use user validation declared in the appliaction settings
        switch (UserValidationTypesCodes.getUserValidationTypeCode(userValidationSettings.getValidationMethod())) {
            case RECAPTCHA_TOKEN:
                // validate user via reCaptcha token
                requestorIsValid
                        = this.reCaptchaTokenFormatChecking(requestPassCode.captchaToken)
                        && this.validateCaptchaWithProvider(requestPassCode, requestorIP)
                        && this.validateHoneyPotAttributesEmpty(requestPassCode);
                break;
            case HONEYPOT:
                // check if HoneyPot fields are empty
                requestorIsValid = this.validateHoneyPotAttributesEmpty(requestPassCode);
                break;
            default:
                // if user validation method is not defined then we always deny further execution
                break;
        }

        return requestorIsValid;
    }

    private boolean validateHoneyPotAttributesEmpty(RequestPasscodeModel requestModel) {

        return StringValidator.stringIsNotNull(requestModel.honeyPotFirstname)
                && StringValidator.stringIsNotNull(requestModel.honeyPotLastname)
                && StringValidator.stringIsNotNull(requestModel.honeyPotPhone)
                && !StringValidator.stringIsNotEmpty(requestModel.honeyPotFirstname)
                && !StringValidator.stringIsNotEmpty(requestModel.honeyPotLastname)
                && !StringValidator.stringIsNotEmpty(requestModel.honeyPotPhone);
    }

    private boolean validateRequestPasscodeModel(RequestPasscodeModel requestModel) {
        return EmailAddressValidator.emailIsValid(requestModel.contactMailAddress);
    }

    private boolean validateCaptchaWithProvider(RequestPasscodeModel requestPassCode, String clientAddressIP) {

        boolean requestorIsValid;

        try {
            this.captchaService.validateCaptcha(requestPassCode.captchaToken, clientAddressIP);
            requestorIsValid = true;
        } catch (InvalidReCaptchaException ex) {
            logger.info("Request has invalid captcha", ex);
            requestorIsValid = false;
        } catch (Exception ex) {
            logger.error("An error occurred while while validating google captcha", ex);
            requestorIsValid = false;
        }

        return requestorIsValid;
    }

    // bacis ReCaptcha token check to filter out if the token is not in the right format
    private boolean reCaptchaTokenFormatChecking(String reCaptchaToken) {
        return TokenValidator.checkIfTokenIsProvided(reCaptchaToken)
                && TokenValidator.checkTokenMeetsReCaptchaPattern(reCaptchaToken);
    }

}
