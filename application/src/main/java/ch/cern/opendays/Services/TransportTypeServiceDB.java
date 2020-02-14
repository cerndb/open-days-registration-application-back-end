//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Constants.EnvironmentConfigConstants;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.InterfacesDAO.TransportTypeRepository;
import ch.cern.opendays.InterfacesDAO.VisitorTransportTypeRepository;
import ch.cern.opendays.Models.StoreVisitorTransportTypesModel;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;
import ch.cern.opendays.Models.TransportTypeModel;
import ch.cern.opendays.Models.TransportTypeSelectionListModel;
import ch.cern.opendays.ModelsDAO.VisitorTransportTypeModelDAO;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransportTypeServiceDB implements TransportTypeServiceInterface {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(TransportTypeServiceDB.class);
    private TransportTypeRepository transportTypeRepository;
    private VisitorTransportTypeRepository visitorTransportTypeRepository;

    @Autowired
    public void setTransportTypeRepository(TransportTypeRepository transportTypeRepository) {
        this.transportTypeRepository = transportTypeRepository;
    }

    @Autowired
    public void setVisitorTransportTypeRepository(VisitorTransportTypeRepository visitorTransportTypeRepository) {
        this.visitorTransportTypeRepository = visitorTransportTypeRepository;
    }

    public TransportTypeServiceDB() {
    }

    @Override
    public TransportTypeSelectionListModel getTransportTypesForSelection(String language, TokenStoredRegistrationInformationModel tokenInformation) throws DatabaseException {
        try {
            return this.markUserDefinedTransportTypes(this.getAllTransportTypes(language), tokenInformation);
        } catch (DatabaseException ex) {
            logger.error(String.format("Failed to load transport types for transport type selection for this reservation : %1$d", tokenInformation.getIdReservation()));
            throw ex.setErrorCode(MessageStatusCodes.VISITOR_TRANSPORT_TYPE_LOAD_FAILED.getStatusCode());
        }
    }

    // get all transportType
    private TransportTypeSelectionListModel getAllTransportTypes(String language) throws DatabaseException {
        try {
            TransportTypeSelectionListModel transportTypes = new TransportTypeSelectionListModel();

            this.transportTypeRepository.findAll().forEach((transportTypeListElement) -> {

                String displayName = (language.equals(EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE)) ? transportTypeListElement.getTransportNameEN() : transportTypeListElement.getTransportNameFR();

                TransportTypeModel transport = new TransportTypeModel()
                        .setValue(false)
                        .setIdTransportType(transportTypeListElement.getIdTransportType())
                        .setDisplayName(displayName);
                transportTypes.transportTypes.add(transport);

            });

            return transportTypes;
        } catch (Exception ex) {
            logger.error("Failed to load all transport types");
            throw new DatabaseException("Faild to load all transport types", ex);
        }
    }

    // mark stored transport types
    private TransportTypeSelectionListModel markUserDefinedTransportTypes(TransportTypeSelectionListModel allTransportTypes, TokenStoredRegistrationInformationModel tokenInformation) throws DatabaseException {

        try{
            List<VisitorTransportTypeModelDAO> selectedTransportTypes = this.visitorTransportTypeRepository.findByIdReservation(tokenInformation.getIdReservation());
            if(selectedTransportTypes.size()>0){
                selectedTransportTypes.forEach((selectedTransport) -> {
                    allTransportTypes.transportTypes.forEach((transporType) -> {
                        if (selectedTransport.getIdTransportType() == transporType.idTransportType) {
                            transporType.value = true;
                        }
                    });
                });
            }
        } catch (Exception ex) {
            logger.error(String.format("Failed to load existing stored transport types for this reservation: %1$d", tokenInformation.getIdReservation()));
            throw new DatabaseException(String.format("Failed to load existing stored transport types for this reservation: %1$d", tokenInformation.getIdReservation()), ex);
        }

        return allTransportTypes;
    }

    @Override
    public void storeConfirmedTransportTypes(StoreVisitorTransportTypesModel transportTypes, TokenStoredRegistrationInformationModel tokenInformation) throws DatabaseException {

        try {
            transportTypes.visitorsDetails.forEach((transportType) -> {
                VisitorTransportTypeModelDAO storeModel = new VisitorTransportTypeModelDAO()
                        .setIdReservation(tokenInformation.getIdReservation())
                        .setIdTransportType(transportType.idTransportType);
                this.visitorTransportTypeRepository.save(storeModel);
            });
        } catch (Exception ex) {
            logger.error(String.format("Failed to store new transport types for this reservation: %1$d", tokenInformation.getIdReservation()));
            throw new DatabaseException(String.format("Failed to store new transport types for this reservation: %1$d", tokenInformation.getIdReservation()), ex)
                    .setErrorCode(MessageStatusCodes.VISITOR_TRANSPORT_TYPE_UPDATE_FAILED.getStatusCode());
        }
    }

    @Override
    public void deletePreviousVisitorTransportTypeMetaData(Long idReservation) throws DatabaseException {
        try {
            this.visitorTransportTypeRepository.deletePreviousTransportTypesMetadataForReservation(idReservation);
        } catch (Exception ex) {
            logger.error(String.format("Failed to delete previous transport type metadata for this reservation id : %1$d", idReservation));
            throw new DatabaseException(String.format("Failed to delete previous  transport type metadata for this reservation id : %1$d", idReservation), ex)
                    .setErrorCode(MessageStatusCodes.VISITOR_TRANSPORT_TYPE_UPDATE_FAILED.getStatusCode());
        }
    }

}
