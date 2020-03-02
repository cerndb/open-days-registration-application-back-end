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
import ch.cern.opendays.Enums.RegistrationProcessStatusCodes;
import ch.cern.opendays.Exceptions.ActionHistoryException;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.Models.AccessTokenResponseModel;
import ch.cern.opendays.Models.MessageResponseAPIModel;
import ch.cern.opendays.Models.RequestAccessTokenModel;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;
import ch.cern.opendays.Providers.ResponseMessageProvider;
import ch.cern.opendays.Providers.TokenProviderInteface;
import ch.cern.opendays.Providers.TokenProviderJWT;
import ch.cern.opendays.Services.PasscodeServiceDB;
import ch.cern.opendays.Services.PasscodeServiceInterface;
import ch.cern.opendays.Services.RegisteredPersonServiceDB;
import ch.cern.opendays.Services.RegisteredPersonServiceInterface;
import ch.cern.opendays.Validators.EmailAddressValidator;
import ch.cern.opendays.Validators.StringValidator;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestTokenController {

    private static final Logger logger = LogManager.getLogger(RequestPasscodeController.class);
    private TokenProviderInteface tokenProvider;
    private PasscodeServiceInterface passcodeService;
    private RegisteredPersonServiceInterface registeredPersonService;

    @Autowired
    public void setTokenProvider(TokenProviderJWT tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Autowired
    public void setPasscodeService(PasscodeServiceDB passcodeService) {
        this.passcodeService = passcodeService;
    }

    @Autowired
    public void setRegistredPersonService(RegisteredPersonServiceDB registeredPersonServiceDB) {
        this.registeredPersonService = registeredPersonServiceDB;
    }

    @PostMapping(path = "/request-access-token", consumes = "application/json", produces = "application/json")
    public MessageResponseAPIModel<AccessTokenResponseModel> requestAccessToken(@RequestBody RequestAccessTokenModel requestModel, HttpServletRequest request) {

        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        // requestor seems to be a robot or request is suspicios
        if (!this.validateRequestor(requestModel)) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.INCORRECT_API_INPUT.getStatusCode(), responseLanguage);
        }

        // validate request model
        if (!this.validateRequestModel(requestModel)) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.INCORRECT_API_INPUT.getStatusCode(), responseLanguage);
        }

        TokenStoredRegistrationInformationModel registrationInformation;

        // get registration information and track if user logs in the first time
        try {
            registrationInformation = this.registeredPersonService.getRegistrationInformationForToken(requestModel.contactMailAddress);

            // if user has no registration record
            if (registrationInformation.getIdRegistration().equals(ControllerConstants.DEFAULT_USER_ID)) {
                return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.USER_NOT_REGISTERED.getStatusCode(), responseLanguage);
            } else if (registrationInformation.getRegistrationStatusCode() == RegistrationProcessStatusCodes.INTERESTED.getStatusCode()) {
                // confirm e-mail address tracking
                try {
                    this.registeredPersonService.emailAddressIsConfirmed(registrationInformation.getIdRegistration());
                } catch (ActionHistoryException ex) {
                    logger.info(String.format("Action history store failed for e-mail confirmation, work, but we let the workflow to continue for this registration id : %1$d", registrationInformation.getIdRegistration()));
                }
            }

            // validate if passcode is still valid
            if (!this.validatePasscode(registrationInformation.getIdRegistration(), requestModel.passcode)) {
                logger.info("User provided token is not matching. User will be asked to request a new token");
                return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.PASSCODE_VALIDATION_TRY_AGAIN.getStatusCode(), responseLanguage);
            }

            // use passcode
            try {
                this.passcodeService.usePasscode(registrationInformation.getIdRegistration());
            } catch (ActionHistoryException ex) {
                logger.info(String.format("Action history store failed for token creation, but we let the workflow to continue for this registration id : %1$d", registrationInformation.getIdRegistration()));
            }
            return ResponseMessageProvider.returnDataMessage(new AccessTokenResponseModel(this.tokenProvider.generateRegistrationWorkflowToken(registrationInformation)), MessageStatusCodes.OK.getStatusCode(), responseLanguage);

        } catch (OutOfWorkflowScopeException ex) {
            logger.error("Token generation terminated, because of data inconsistency.");
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (DatabaseException ex) {
            logger.error("There was a database error during the token creation");
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }

    }

    private boolean validateRequestModel(RequestAccessTokenModel requestModel) {
        return StringValidator.stringIsNotNull(requestModel.contactMailAddress)
                && StringValidator.stringIsNotNull(requestModel.passcode)
                && EmailAddressValidator.emailIsValid(requestModel.contactMailAddress);
    }

    private boolean validatePasscode(Long idRegistration, String passcode) throws DatabaseException, OutOfWorkflowScopeException {
        return this.passcodeService.validateEmailWithPasscode(idRegistration, passcode);
    }

    private boolean validateRequestor(RequestAccessTokenModel requestModel) {
        return this.validateHoneyPotAttributesEmpty(requestModel);
    }

    private boolean validateHoneyPotAttributesEmpty(RequestAccessTokenModel requestModel) {

        return StringValidator.stringIsNotNull(requestModel.honeyPotFirstname)
                && StringValidator.stringIsNotNull(requestModel.honeyPotLastname)
                && StringValidator.stringIsNotNull(requestModel.honeyPotPhone)
                && !StringValidator.stringIsNotEmpty(requestModel.honeyPotFirstname)
                && !StringValidator.stringIsNotEmpty(requestModel.honeyPotLastname)
                && !StringValidator.stringIsNotEmpty(requestModel.honeyPotPhone);
    }

}
