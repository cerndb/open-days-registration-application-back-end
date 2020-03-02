//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Controllers;

import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.Models.AutoTestingPasscodeRequestModel;
import ch.cern.opendays.Models.AutoTestingPasscodeResponseModel;
import ch.cern.opendays.Models.MessageResponseAPIModel;
import ch.cern.opendays.Providers.ResponseMessageProvider;
import ch.cern.opendays.Services.PasscodeServiceDB;
import ch.cern.opendays.Services.PasscodeServiceInterface;
import ch.cern.opendays.Services.RegisteredPersonServiceInterface;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile({"dev", "qa"})
public class PasscodeController {

    // This controller is here for doing automated testing it should be available for dev and qa
    private PasscodeServiceInterface passcodeService;
    private RegisteredPersonServiceInterface registeredPersonService;

    @Autowired
    public void setPasscodeService(PasscodeServiceDB passcodeService) {
        this.passcodeService = passcodeService;
    }

    @Autowired
    public void setRegisteredPersonService(RegisteredPersonServiceInterface registeredPersonService) {
        this.registeredPersonService = registeredPersonService;
    }

    @PostMapping(path = "/request-passcode-for-testing", consumes = "application/json", produces = "application/json")
    public MessageResponseAPIModel<AutoTestingPasscodeResponseModel> getPasscode(@RequestBody AutoTestingPasscodeRequestModel requestModel, HttpServletRequest request) {

        // This call returns the passcode for a specific mail address
        String responseLanguage = "en";

        requestModel.mailAddress = (requestModel.mailAddress == null) ? "" : requestModel.mailAddress.toLowerCase();

        if (requestModel.mailAddress.isEmpty()) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode(), responseLanguage);
        }

        try {
            AutoTestingPasscodeResponseModel passcodeResponse = new AutoTestingPasscodeResponseModel();
            passcodeResponse.setPasscode(this.passcodeService.getPasscodeForTesting(this.registeredPersonService.getExistingApplicationId(requestModel.mailAddress)));
            return ResponseMessageProvider.returnDataMessage(passcodeResponse, MessageStatusCodes.OK.getStatusCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.AUTOMATED_TEST_PASSCODE_ERROR.getStatusCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode(), responseLanguage);
        }
    }

}
