//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.ModelsDAO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ArrivalPointOpeningHourPK implements Serializable {

    private LocalDateTime openDay;
    private Integer idArrivalPoint;
    private LocalDateTime timeslotStart;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.openDay);
        hash = 97 * hash + Objects.hashCode(this.idArrivalPoint);
        hash = 97 * hash + Objects.hashCode(this.timeslotStart);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ArrivalPointOpeningHourPK other = (ArrivalPointOpeningHourPK) obj;
        if (!Objects.equals(this.openDay, other.openDay)) {
            return false;
        }
        if (!Objects.equals(this.idArrivalPoint, other.idArrivalPoint)) {
            return false;
        }
        if (!Objects.equals(this.timeslotStart, other.timeslotStart)) {
            return false;
        }
        return true;
    }

    public ArrivalPointOpeningHourPK() {
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

}
