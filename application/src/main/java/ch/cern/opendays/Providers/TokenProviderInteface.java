//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Providers;

import ch.cern.opendays.Exceptions.TokenException;
import ch.cern.opendays.Models.TokenModel;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;

public interface TokenProviderInteface {

    public TokenModel generateRegistrationWorkflowToken(TokenStoredRegistrationInformationModel registartionInformation);
    public TokenStoredRegistrationInformationModel getTokenStoredWorkflowInformation(String receivedToken) throws TokenException;
    public void validateToken(String receivedToken) throws TokenException;

}
