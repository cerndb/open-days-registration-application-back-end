//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.Models.ArrivalPointDatesModel;
import ch.cern.opendays.Models.AvailablePlacesModel;
import ch.cern.opendays.Models.DailyAvailablePlacesResponseModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public interface ArrivalPointOpeningHourServiceInterface {
    public ArrivalPointDatesModel getArrivalDates(Set<LocalDate> privilegedDates, Long idRegistration, String language) throws DatabaseException;
    public AvailablePlacesModel getAvailablePlaces(LocalDateTime visitDay, LocalDateTime timeslot, int idArrivalPoint, Long idReservation) throws DatabaseException;
    public DailyAvailablePlacesResponseModel getDailyAvailablePlaces(String selectedLanguage) throws DatabaseException;
}
