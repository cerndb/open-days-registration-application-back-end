//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.InterfacesDAO;

import ch.cern.opendays.ModelsDAO.VisitorTransportTypeModelDAO;
import ch.cern.opendays.ModelsDAO.VisitorTransportTypePK;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface VisitorTransportTypeRepository extends CrudRepository<VisitorTransportTypeModelDAO, VisitorTransportTypePK> {

    public List<VisitorTransportTypeModelDAO> findByIdReservation(Long idReservation);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM VISITOR_TRANSPORT_TYPE visitor_transport_type WHERE visitor_transport_type.ID_RESERVATION = :idReservation", nativeQuery = true)
    int deletePreviousTransportTypesMetadataForReservation(@Param("idReservation") Long idReservation);
}
