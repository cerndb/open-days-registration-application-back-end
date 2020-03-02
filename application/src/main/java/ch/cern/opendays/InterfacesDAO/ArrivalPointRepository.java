//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.InterfacesDAO;

import ch.cern.opendays.Constants.CustomQueryConstants;
import ch.cern.opendays.ModelsDAO.ArrivalPointModelDAO;
import ch.cern.opendays.ModelsDAO.AvailablePlacesQueryDAO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ArrivalPointRepository extends CrudRepository<ArrivalPointModelDAO, Integer> {
    @Query(value = CustomQueryConstants.GET_DATE_BASED_AVAILABLE_PLACES_TABLE, nativeQuery = true)
    List<AvailablePlacesQueryDAO> getArrivalPointTimeslotBasedAvailablePlaces(@Param("openDay") LocalDateTime openDay, @Param("idReservation") Long idReservation);
}
