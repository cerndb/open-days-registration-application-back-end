//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.ModelsDAO;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ARRIVAL_POINT_OPENING_HOUR")
@IdClass(ArrivalPointOpeningHourPK.class)
public class ArrivalPointOpeningHourModelDAO implements Serializable {

    @Id
    @Column(name = "OPEN_DAY")
    private LocalDateTime openDay;

    @Id
    @Column(name = "ID_ARRIVAL_POINT")
    private Integer idArrivalPoint;

    @Id
    @Column(name = "TIMESLOT_START")
    private LocalDateTime timeslotStart;

    @NotNull
    @Column(name = "OPENING_DURRATION_IN_MINUTES")
    private Integer openingDurrationInMinutes;

    @NotNull
    @Column(name = "TOTAL_AVAILABLE_PLACES")
    private Integer totalAvailablePlaces;

    @Column(name = "FAST_TRACK_PERCENTAGE")
    private Integer fastTrackPercentage;

    @Column(name = "CAPACITY_LIMITATION_PERCENTAGE")
    private Integer capacityLimitationPercentage;

    @Column(name = "PRIVILEGED_OPENING")
    private Integer privilegedOpening;

    public ArrivalPointOpeningHourModelDAO() {
    }

    public LocalDateTime getOpenDay() {
        return openDay;
    }

    public void setOpenDay(LocalDateTime openDay) {
        this.openDay = openDay;
    }

    public Integer getIdArrivalPoint() {
        return idArrivalPoint;
    }

    public void setIdArrivalPoint(Integer idArrivalPoint) {
        this.idArrivalPoint = idArrivalPoint;
    }

    public LocalDateTime getTimeslotStart() {
        return timeslotStart;
    }

    public void setTimeslotStart(LocalDateTime timeslotStart) {
        this.timeslotStart = timeslotStart;
    }

    public Integer getOpeningDurrationInMinutes() {
        return openingDurrationInMinutes;
    }

    public void setOpeningDurrationInMinutes(Integer openingDurrationInMinutes) {
        this.openingDurrationInMinutes = openingDurrationInMinutes;
    }

    public Integer getTotalAvailablePlaces() {
        return totalAvailablePlaces;
    }

    public void setTotalAvailablePlaces(Integer totalAvailablePlaces) {
        this.totalAvailablePlaces = totalAvailablePlaces;
    }

    public Integer getFastTrackPercentage() {
        return fastTrackPercentage;
    }

    public void setFastTrackPercentage(Integer fastTrackPercentage) {
        this.fastTrackPercentage = fastTrackPercentage;
    }

    public Integer getCapacityLimitationPercentage() {
        return capacityLimitationPercentage;
    }

    public void setCapacityLimitationPercentage(Integer capacityLimitationPercentage) {
        this.capacityLimitationPercentage = capacityLimitationPercentage;
    }

    public Integer getPrivilegedOpening() {
        return privilegedOpening;
    }

    public void setPrivilegedOpening(Integer privilegedOpening) {
        this.privilegedOpening = privilegedOpening;
    }

}
