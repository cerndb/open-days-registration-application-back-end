//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Controllers;

import ch.cern.opendays.Constants.EnvironmentConfigConstants;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Models.MessageResponseAPIModel;
import ch.cern.opendays.Models.TestCallModel;
import ch.cern.opendays.Models.TestResponseModel;
import ch.cern.opendays.Providers.ResponseMessageProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping(path="/", produces = "application/json")
    public MessageResponseAPIModel<String> index() {
        return ResponseMessageProvider.returnErrorMessage(null,MessageStatusCodes.IMPLEMENTATION_IS_MISSING.getStatusCode(), EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE);
    }

    @PostMapping(path="/event-registration", consumes = "application/json", produces = "application/json")
    public MessageResponseAPIModel<TestResponseModel> eventRegistration(@RequestBody TestCallModel testCall) {
        return ResponseMessageProvider.returnDataMessage(new TestResponseModel(testCall), MessageStatusCodes.OK.getStatusCode(), EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE);
    }

}
