//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.InterfacesDAO;

import ch.cern.opendays.Constants.CustomQueryConstants;
import ch.cern.opendays.ModelsDAO.ReservationVisitsQueryDAO;
import ch.cern.opendays.ModelsDAO.VisitorDetailModelDAO;
import ch.cern.opendays.ModelsDAO.VisitorDetailPK;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface VisitorDetailRepository extends CrudRepository<VisitorDetailModelDAO, VisitorDetailPK> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM VISITOR_DETAIL visitor_detail WHERE visitor_detail.ID_RESERVATION = :idReservation", nativeQuery = true)
    int deletePreviousVisitorsMetadataForReservation(@Param("idReservation") Long idReservation);

    @Modifying
    @Transactional
    @Query(value = "UPDATE visitor_detail visitors SET requested_fast_track = 0 WHERE visitors.id_reservation = :idReservation", nativeQuery = true)
    int removeFastTrackFromVisitorDetails(@Param("idReservation") Long idReservation);

    @Query(value = "SELECT * FROM VISITOR_DETAIL visitor_detail WHERE visitor_detail.ID_RESERVATION = :idReservation", nativeQuery = true)
    List<VisitorDetailModelDAO> findVisitorsDetailByReservationId(@Param("idReservation") Long idReservation);

    @Query(value = CustomQueryConstants.GET_TICKET_DATA_FROM_VISITORS_METADATA, nativeQuery = true)
    List<ReservationVisitsQueryDAO> getReservationVisistsFromMetadata(@Param("idReservation") Long idReservation);
}
