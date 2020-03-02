//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Constants.ControllerConstants;
import ch.cern.opendays.Enums.DayPrivilegeTypes;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.InterfacesDAO.ArrivalPointOpeningHourRepository;
import ch.cern.opendays.Models.ArrivalPointDatesModel;
import ch.cern.opendays.Models.ArrivalPointRadioButtonModel;
import ch.cern.opendays.Models.AvailablePlacesModel;
import ch.cern.opendays.Models.DailyAvailablePlaceModel;
import ch.cern.opendays.Models.DailyAvailablePlacesResponseModel;
import ch.cern.opendays.ModelsDAO.AvailablePlacesOfTimeslotQueryDAO;
import ch.cern.opendays.ModelsDAO.DailyFreeAvailablePlacesQueryDAO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArrivalPointOpeningHourServiceDB implements ArrivalPointOpeningHourServiceInterface {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(ArrivalPointOpeningHourServiceDB.class);
    private ArrivalPointOpeningHourRepository arrivalPointOpeningHourRepository;

    @Autowired
    public void setArrivalPointOpeningHourRepository(ArrivalPointOpeningHourRepository arrivalPointOpeningHourRepository) {
        this.arrivalPointOpeningHourRepository = arrivalPointOpeningHourRepository;
    }

    @Override
    public ArrivalPointDatesModel getArrivalDates(Set<LocalDate> privilegedDates, Long idRegistration, String language) throws DatabaseException {
        try {
            Set<LocalDate> visitDates = new HashSet<>();

            // populate unique visit dates array
            this.arrivalPointOpeningHourRepository.getVisitDates(idRegistration).forEach((possibleVisitDay) -> {
                if (possibleVisitDay.getPrivilegedOpening() == DayPrivilegeTypes.IS_NORMAL.getPrivilegeTypeCode()) {
                    // day is normal we add it
                    visitDates.add(possibleVisitDay.getOpenDay().toLocalDate());
                } else {
                    // we check if we have privilege if yes we add it
                    privilegedDates.forEach((privilegeDay) -> {
                        if (privilegeDay.isEqual(possibleVisitDay.getOpenDay().toLocalDate())) {
                            visitDates.add(possibleVisitDay.getOpenDay().toLocalDate());
                        }
                    });
                }
            });

            List<LocalDate> sortedVisitDates = new ArrayList<>(visitDates);
            Collections.sort(sortedVisitDates);

            List<ArrivalPointRadioButtonModel> radioButtonDates = new ArrayList<>();

            sortedVisitDates.forEach((allowedVisitDay) -> {
                radioButtonDates.add(new ArrivalPointRadioButtonModel(allowedVisitDay, language));
            });
            return new ArrivalPointDatesModel()
                    .setArrivalDates(radioButtonDates);
        } catch (Exception ex) {
            logger.error(String.format("Failed to load arrival opening hours for this registration id : %1$d", idRegistration));
            throw new DatabaseException(String.format("Failed to load arrival opening hours for this registration id : %1$d", idRegistration), ex)
                    .setErrorCode(MessageStatusCodes.ARRIVAL_POINT_GET_AVAILABLE_DATES_LOAD_FAILED.getStatusCode());
        }
    }

    @Override
    public AvailablePlacesModel getAvailablePlaces(LocalDateTime visitDay, LocalDateTime timeslot, int idArrivalPoint, Long idReservation) throws DatabaseException {
        try {
            AvailablePlacesOfTimeslotQueryDAO availablePlaces = this.arrivalPointOpeningHourRepository.getAvailablePlacesForTimeslot(visitDay, idArrivalPoint, timeslot);
            return new AvailablePlacesModel().setAvailablePlaces(availablePlaces.getAvailablePlaces()).setAvailableFastTrackPlaces(availablePlaces.getFastTrackAvailablePlaces());
        } catch (Exception ex) {
            String errorMessage = String.format("Failed to load available places for this reservation: %1$d , arrivalPoint: %2$d , timeslot: %3$s  visitday %4$s", idReservation, idArrivalPoint, visitDay.format(ControllerConstants.DATE_FORMAT_PATTERN), timeslot.format(ControllerConstants.DATETIME_FORMAT_PATTERN));
            logger.error(errorMessage);
            throw new DatabaseException(errorMessage, ex);
        }

    }

    @Override
    public DailyAvailablePlacesResponseModel getDailyAvailablePlaces(String selectedLanguage) throws DatabaseException {
        DailyAvailablePlacesResponseModel availablePlacesResponse = new DailyAvailablePlacesResponseModel();
        try {
            List<DailyFreeAvailablePlacesQueryDAO> visitDayDataList = this.arrivalPointOpeningHourRepository.getNumberOfFreePlacesPerDays();
            visitDayDataList.forEach((visitDayData) -> {

                if (visitDayData.getVisitDay().toLocalDate().isAfter(LocalDate.parse(ControllerConstants.PRIVILEGE_DAY, ControllerConstants.DATE_FORMAT_PATTERN))) {
                    availablePlacesResponse.dailyAvailablePlaces.add(new DailyAvailablePlaceModel(visitDayData, selectedLanguage));
                }
            });
        } catch (Exception ex) {
            String errorMessage = "Failed to load free spaces per visit days";
            logger.error(errorMessage);
            throw new DatabaseException(errorMessage, ex).setErrorCode(MessageStatusCodes.FAILED_TO_LOAD_AVAILABLE_PLACES_PER_DAYS.getStatusCode());
        }
        return availablePlacesResponse;
    }
}
