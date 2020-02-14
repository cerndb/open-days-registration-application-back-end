//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Constants.WorkflowConstants;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.InterfacesDAO.VisitorDetailRepository;
import ch.cern.opendays.Models.ReservedTicketsModel;
import ch.cern.opendays.Models.VisitorDetailModel;
import ch.cern.opendays.Models.VisitorsDetailModel;
import ch.cern.opendays.ModelsDAO.VisitorDetailModelDAO;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisitorDetailServiceDB implements VisitorDetailServiceInterface {

    private static final Logger logger = LogManager.getLogger(VisitorDetailServiceDB.class);
    private VisitorDetailRepository visitorDetailRepository;

    @Autowired
    public void setVisitorDetailRepository(VisitorDetailRepository visitorDetailRepository) {
        this.visitorDetailRepository = visitorDetailRepository;
    }

    @Override
    public void deletePreviousVisitorMetaData(Long idReservation) throws DatabaseException {
        try {
            this.visitorDetailRepository.deletePreviousVisitorsMetadataForReservation(idReservation);
        } catch (Exception ex) {
            logger.error(String.format("Failed to delete visitor metadate for this reservation: %1$d", idReservation));
            throw new DatabaseException(String.format("Failed to delete visitor metadate for this reservation: %1$d", idReservation), ex);
        }
    }

    @Override
    public void storeVisitorDetails(List<VisitorDetailModelDAO> visitors, Long idReservation) throws DatabaseException {

        try {
            visitors.forEach(visitorDetail -> {
                this.visitorDetailRepository.save(visitorDetail);
            });

        } catch (Exception ex) {
            logger.error(String.format("Failed to store visitors metadata for this reservation: %1$d", idReservation));
            throw new DatabaseException(String.format("Failed to store visitors metadata for this reservation: %1$d", idReservation), ex);
        }

    }

    @Override
    public VisitorsDetailModel getStoredVisitors(Long idReservation) throws DatabaseException {
        VisitorsDetailModel visitorDetailsModel = new VisitorsDetailModel();

        try {
            this.visitorDetailRepository.findVisitorsDetailByReservationId(idReservation).forEach((storedVisitor) -> {
                visitorDetailsModel.visitorsDetails.add(
                        new VisitorDetailModel()
                                .setVisitorAge(storedVisitor.getAge())
                                .setIdVisitor(storedVisitor.getIdVisitor())
                                .setFastTrackSelected(storedVisitor.getRequestedFastTrack())
                );
            });
        } catch (Exception ex) {
            throw new DatabaseException(String.format("Failed to load visitors metadata for this reservation id: %1$d ", idReservation), ex).setErrorCode(MessageStatusCodes.VISITOR_DETAILS_LOAD_FAILED.getStatusCode());
        }

        // sort visitors based on visitor id
        Collections.sort(visitorDetailsModel.visitorsDetails, (VisitorDetailModel visitor1, VisitorDetailModel visitor2) -> visitor1.getIdVisitor().compareTo(visitor2.getIdVisitor()));

        return visitorDetailsModel;
    }

    @Override
    public void updateRemoveFastTrackFromVisitorDetails(Long idReservation) throws DatabaseException {
        try {
            this.visitorDetailRepository.removeFastTrackFromVisitorDetails(idReservation);
        } catch (Exception ex) {
            logger.error(String.format("Failed to remove fast track flag for the following reservation : %1$d", idReservation));
            throw new DatabaseException(String.format("Failed to remove fast track flag for the following reservation : %1$d", idReservation), ex)
                    .setErrorCode(MessageStatusCodes.VISITORS_REMOVE_FASTTRACK.getStatusCode());

        }
    }

    @Override
    public ReservedTicketsModel getReservationTickets(Long idReservation) throws DatabaseException {
        try {
            ReservedTicketsModel reservationTicketInfo = new ReservedTicketsModel()
                    .setFastTrackTickets(WorkflowConstants.ZERO_INTEGER)
                    .setStandardTickets(WorkflowConstants.ZERO_INTEGER);
            this.visitorDetailRepository.getReservationVisistsFromMetadata(idReservation)
                    .stream()
                    .forEach((aggregatedTicketInfo) -> {
                if (aggregatedTicketInfo.getTicketIsFastTrack() > WorkflowConstants.ZERO_INTEGER) {
                            reservationTicketInfo.setFastTrackTickets(aggregatedTicketInfo.getReservedTickets());
                        } else {
                            reservationTicketInfo.setStandardTickets(aggregatedTicketInfo.getReservedTickets());
                        }
                    });

            return reservationTicketInfo;
        } catch (Exception ex) {
            logger.error(String.format("Failed to load visitor metadata for this reservation : %1$d", idReservation));
            throw new DatabaseException(String.format("Failed to load visitor metadata for this reservation : %1$d", idReservation), ex);
        }
    }
}
