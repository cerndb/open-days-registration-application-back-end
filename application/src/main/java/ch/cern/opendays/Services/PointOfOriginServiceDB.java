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
import ch.cern.opendays.Exceptions.OutOfWorkflowScopeException;
import ch.cern.opendays.InterfacesDAO.PointOfOriginRepository;
import ch.cern.opendays.Models.PointOfOriginDropDownElementModel;
import ch.cern.opendays.Models.PointOfOriginSelectionListModel;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointOfOriginServiceDB implements PointOfOriginServiceInterface {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(PointOfOriginServiceDB.class);
    private PointOfOriginRepository pointOfOriginRepository;
    private ReservationServiceInterface reservationService;

    @Autowired
    public void setPointOfOriginRepository(PointOfOriginRepository pointOfOriginRepository) {
        this.pointOfOriginRepository = pointOfOriginRepository;
    }

    @Autowired
    public void setReservationservice(ReservationServiceDB reservationservice) {
        this.reservationService = reservationservice;
    }

    @Override
    public PointOfOriginSelectionListModel getPointsOfOriginForSelection(String language, TokenStoredRegistrationInformationModel tokenInformation) throws DatabaseException, OutOfWorkflowScopeException {

        try {
        PointOfOriginSelectionListModel pointOfOriginModel = new PointOfOriginSelectionListModel();
        pointOfOriginModel.setPointOfOriginSelectionList(this.getAllPointOfOrigins(language));
        pointOfOriginModel.setSelectedPointOfOrigin(this.reservationService.getReservationPointOfOrigin(tokenInformation.getIdReservation()));

            return pointOfOriginModel;
        } catch (DatabaseException ex) {
            throw ex.setErrorCode(MessageStatusCodes.POINT_OF_ORIGIN_FAILED_TO_RETREIVE.getStatusCode());
        } catch (OutOfWorkflowScopeException ex) {
            throw ex.setErrorCode(MessageStatusCodes.OUT_OF_WORKFLOW_ERROR.getStatusCode());
        }

    }

    // get all point of origins
    private List<PointOfOriginDropDownElementModel> getAllPointOfOrigins(String language) throws DatabaseException {
        try {
            List<PointOfOriginDropDownElementModel> pointOfOrigins = new ArrayList<>();

            this.pointOfOriginRepository.findAll().forEach((pointOfOriginElement)
                    -> {
                String displayName = (language.equals(EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE)) ? pointOfOriginElement.getPointOfOriginNameEN() : pointOfOriginElement.getPointOfOriginNameFR();

                PointOfOriginDropDownElementModel dropDownElement = new PointOfOriginDropDownElementModel()
                        .setDisplayName(displayName)
                        .setValue(pointOfOriginElement.getIdPointOfOrigin());

                pointOfOrigins.add(dropDownElement);
            });

            return pointOfOrigins;
        } catch (Exception ex) {
            String errorMessage = "Failed to load all points of origin";
            logger.error(errorMessage);
            throw new DatabaseException(errorMessage, ex);
        }
    }
}
