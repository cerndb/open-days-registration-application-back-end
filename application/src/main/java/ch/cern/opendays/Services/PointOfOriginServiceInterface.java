//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.Models.PointOfOriginSelectionListModel;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;

public interface PointOfOriginServiceInterface {
    public PointOfOriginSelectionListModel getPointsOfOriginForSelection(String language, TokenStoredRegistrationInformationModel tokenInformation) throws DatabaseException, OutOfWorkflowScopeException;
}
