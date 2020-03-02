//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Constants.ControllerConstants;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Enums.SupportedLanguages;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.InterfacesDAO.ArrivalPointActivitiesRepository;
import ch.cern.opendays.InterfacesDAO.ArrivalPointRepository;
import ch.cern.opendays.Models.ArrivalPointModel;
import ch.cern.opendays.Models.ArrivalPointOpenTimeslotModel;
import ch.cern.opendays.ModelsDAO.ArrivalPointActivitiesModelDAO;
import ch.cern.opendays.ModelsDAO.AvailablePlacesQueryDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArrivalPointServiceDB implements ArrivalPointServiceInterface {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(ArrivalPointServiceDB.class);
    private ArrivalPointRepository arrivalPointRepository;
    private ArrivalPointActivitiesRepository arrivalPointActivitiesRepository;

    @Autowired
    public void setArrivalPointRepository(ArrivalPointRepository arrivalPointRepository) {
        this.arrivalPointRepository = arrivalPointRepository;
    }

    @Autowired
    public void setArrivalPointActivitiesRepository(ArrivalPointActivitiesRepository arrivalPointActivitiesRepository) {
        this.arrivalPointActivitiesRepository = arrivalPointActivitiesRepository;
    }

    @Override
    public List<ArrivalPointModel> getAvailablePlaces(LocalDateTime visitDay, Long idRegistration, String language, Long idReservation)
            throws DatabaseException {
        List<ArrivalPointModel> arrivalPoints = new ArrayList<>();

        try {

            // get arrivalPointActivities number for the day
            List<ArrivalPointActivitiesModelDAO> activityList = arrivalPointActivitiesRepository
                    .getArrivalPointActivities(visitDay);

            // get available places for all timeslots and sort them based on startTimeslot
            List<AvailablePlacesQueryDAO> availablePlacesForTimeslots = this.arrivalPointRepository
                    .getArrivalPointTimeslotBasedAvailablePlaces(visitDay, idReservation);
            Collections.sort(availablePlacesForTimeslots,
                    (AvailablePlacesQueryDAO timeslot1, AvailablePlacesQueryDAO timeslot2) -> timeslot1.getTimeslotStart()
                            .compareTo(timeslot2.getTimeslotStart()));

            this.arrivalPointRepository.findAll().forEach((arrivalPoint) -> {
                ArrivalPointModel arrivalPointElement = new ArrivalPointModel()
                        .setArrivalPointName(arrivalPoint.getPointName())
                        .setIdArrivalPoint(arrivalPoint.getIdArrivalPoint())
                        .setSiteAccessibilityInfoURL(language.equals(SupportedLanguages.fr.toString()) ? arrivalPoint.getAccessibilityInfoURL_FR() : arrivalPoint.getAccessibilityInfoURL_EN())
                        .setSiteActivitiesInfoURL(language.equals(SupportedLanguages.fr.toString()) ? arrivalPoint.getSiteActivitiesInfoURL_FR() : arrivalPoint.getSiteActivitiesInfoURL_EN());

                Optional<ArrivalPointActivitiesModelDAO> activityNumbersHolder = activityList.stream().filter(
                        activityListElement -> activityListElement.getIdArrivalPoint() == arrivalPoint.getIdArrivalPoint())
                        .findFirst();

                if (activityNumbersHolder == null || !activityNumbersHolder.isPresent()) {
                    arrivalPointElement.setNumberOfSurfaceActivities(0).setNumberOfUndergroundActivities(0);
                } else {
                    arrivalPointElement
                            .setNumberOfSurfaceActivities(activityNumbersHolder.get().getNumberOfSurfaceActivities())
                            .setNumberOfUndergroundActivities(activityNumbersHolder.get().getNumberOfUndergroundActivities());
                }

                arrivalPoints.add(arrivalPointElement);

            });

            availablePlacesForTimeslots.forEach((availablePlaceTimeslot) -> {
                arrivalPoints.forEach((arrivalPoint) -> {
                    if (availablePlaceTimeslot.getIdArrivalPoint() == arrivalPoint.getIdArrivalPoint()) {
                        arrivalPoint.opentimeSlots.add(new ArrivalPointOpenTimeslotModel()
                                .setAvailablePlaces(availablePlaceTimeslot.getAvailablePlaces()).setIsSelected(Boolean.FALSE)
                                .setIdArrivalPoint(availablePlaceTimeslot.getIdArrivalPoint())
                                .setTimeslotStart(availablePlaceTimeslot.getTimeslotStart())
                                .setDurration(availablePlaceTimeslot.getOpeningDurrationMinutes())
                                .setAvailableFastTrackPlaces(availablePlaceTimeslot.getAvailableFastTrackPlaces())
                        );
                    }
                });
            });

        } catch (Exception ex) {
            logger.error(
                    String.format("Failed to load available palces for this day : %1$s for this registration id: %2$d",
                            visitDay.format(ControllerConstants.DATE_FORMAT_PATTERN), idRegistration));
            throw new DatabaseException(
                    String.format("Failed to load available palces for this day : %1$s for this registration id: %2$d",
                            visitDay.format(ControllerConstants.DATE_FORMAT_PATTERN), idRegistration),
                    ex).setErrorCode(MessageStatusCodes.ARRIVAL_POINT_GET_AVAILABLE_PLACES_LOAD_FAILED.getStatusCode());
        }

        return arrivalPoints;
    }

}
