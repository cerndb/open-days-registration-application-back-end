//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.InterfacesDAO;

import ch.cern.opendays.Constants.CustomQueryConstants;
import ch.cern.opendays.ModelsDAO.ArrivalPointOpeningHourModelDAO;
import ch.cern.opendays.ModelsDAO.ArrivalPointOpeningHourPK;
import ch.cern.opendays.ModelsDAO.AvailablePlacesOfTimeslotQueryDAO;
import ch.cern.opendays.ModelsDAO.DailyFreeAvailablePlacesQueryDAO;
import ch.cern.opendays.ModelsDAO.RegistrationDatesQueryDAO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ArrivalPointOpeningHourRepository extends CrudRepository<ArrivalPointOpeningHourModelDAO, ArrivalPointOpeningHourPK> {
    @Query(value = CustomQueryConstants.GET_AVAILABLE_VISIT_DAYS, nativeQuery = true)
    List<RegistrationDatesQueryDAO> getVisitDates(@Param("idRegistration") Long idRegistration);

    @Query(value = CustomQueryConstants.GET_AVAILABLE_PLACES_FOR_TIMESLOT, nativeQuery = true)
    AvailablePlacesOfTimeslotQueryDAO getAvailablePlacesForTimeslot(@Param("visitDay") LocalDateTime visitDay, @Param("idArrivalPoint") Integer idArrivalPoint, @Param("timeslotStart") LocalDateTime timeslotStart);

    @Query(value = CustomQueryConstants.GET_NUMBER_OF_FREE_PLACES_PER_DAYS, nativeQuery = true)
    List<DailyFreeAvailablePlacesQueryDAO> getNumberOfFreePlacesPerDays();
}
