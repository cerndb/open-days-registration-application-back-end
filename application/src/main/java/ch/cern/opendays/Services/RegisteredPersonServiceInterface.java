//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Exceptions.ActionHistoryException;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;
import ch.cern.opendays.ModelsDAO.RegisteredPersonModelDAO;

public interface RegisteredPersonServiceInterface {
    public String getMailAddress(Long idRegistration) throws DatabaseException, OutOfWorkflowScopeException;
    public Long showInterest(String mailAddress) throws DatabaseException, ActionHistoryException;
    public Long getExistingApplicationId(String mailAddress) throws OutOfWorkflowScopeException, DatabaseException;
    public TokenStoredRegistrationInformationModel getRegistrationInformationForToken(String mailAddress) throws OutOfWorkflowScopeException, DatabaseException;
    public void emailAddressIsConfirmed(Long idRegistration) throws DatabaseException, OutOfWorkflowScopeException, ActionHistoryException;
    public RegisteredPersonModelDAO getRegisteredPerson(Long idRegistration) throws DatabaseException, OutOfWorkflowScopeException;
}
