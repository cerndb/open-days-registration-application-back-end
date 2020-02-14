//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Controllers;

import ch.cern.opendays.Constants.EnvironmentConfigConstants;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.Exceptions.TokenException;
import ch.cern.opendays.Models.MessageResponseAPIModel;
import ch.cern.opendays.Models.PointOfOriginSelectionListModel;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;
import ch.cern.opendays.Providers.ResponseMessageProvider;
import ch.cern.opendays.Providers.TokenProviderInteface;
import ch.cern.opendays.Providers.TokenProviderJWT;
import ch.cern.opendays.Services.PointOfOriginServiceInterface;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointOfOriginController {

    private static final Logger logger = LogManager.getLogger(PointOfOriginController.class);

    private PointOfOriginServiceInterface pointOfOriginService;
    private TokenProviderInteface tokenProvider;

    @Autowired
    public void setPointOfOriginService(PointOfOriginServiceInterface pointOfOriginService) {
        this.pointOfOriginService = pointOfOriginService;
    }

    @Autowired
    public void setTokenProvider(TokenProviderJWT tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @GetMapping(path = "/get-point-of-origin", produces = "application/json")
    public MessageResponseAPIModel<PointOfOriginSelectionListModel> getPointOfOriginInformation(HttpServletRequest request) {

        // set preferred language
        String responseLanguage = (request.getLocale().toString() != null) ? request.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();

        try {
            TokenStoredRegistrationInformationModel tokenInformation = this.tokenProvider.getTokenStoredWorkflowInformation(request.getHeader("Authorization"));

            // return point of origin
            return ResponseMessageProvider.returnDataMessage(this.pointOfOriginService.getPointsOfOriginForSelection(responseLanguage, tokenInformation), MessageStatusCodes.OK.getStatusCode(), responseLanguage);

        } catch (TokenException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (DatabaseException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        } catch (OutOfWorkflowScopeException ex) {
            return ResponseMessageProvider.returnErrorMessage(null, ex.getErrorCode(), responseLanguage);
        }
    }

}
