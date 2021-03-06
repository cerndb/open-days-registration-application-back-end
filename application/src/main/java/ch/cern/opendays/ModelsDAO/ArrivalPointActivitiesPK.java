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

public class ArrivalPointActivitiesPK implements Serializable {

    private Integer idArrivalPoint;
    private LocalDateTime visitDay;

    public ArrivalPointActivitiesPK() {
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.idArrivalPoint);
        hash = 83 * hash + Objects.hashCode(this.visitDay);
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
        final ArrivalPointActivitiesPK other = (ArrivalPointActivitiesPK) obj;
        if (!Objects.equals(this.idArrivalPoint, other.idArrivalPoint)) {
            return false;
        }
        if (!Objects.equals(this.visitDay, other.visitDay)) {
            return false;
        }
        return true;
    }

    public Integer getIdArrivalPoint() {
        return idArrivalPoint;
    }

    public void setIdArrivalPoint(Integer idArrivalPoint) {
        this.idArrivalPoint = idArrivalPoint;
    }

    public LocalDateTime getVisitDay() {
        return visitDay;
    }

    public void setVisitDay(LocalDateTime visitDay) {
        this.visitDay = visitDay;
    }

}
