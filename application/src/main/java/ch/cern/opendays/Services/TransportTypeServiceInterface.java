//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Models.StoreVisitorTransportTypesModel;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;
import ch.cern.opendays.Models.TransportTypeSelectionListModel;

public interface TransportTypeServiceInterface {
    public TransportTypeSelectionListModel getTransportTypesForSelection(String language, TokenStoredRegistrationInformationModel tokenInformation) throws DatabaseException;
    public void deletePreviousVisitorTransportTypeMetaData(Long idReservation) throws DatabaseException;
    public void storeConfirmedTransportTypes(StoreVisitorTransportTypesModel transportTypes, TokenStoredRegistrationInformationModel tokenInformation) throws DatabaseException;
}
