//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.ModelsDAO;

import java.io.Serializable;
import java.util.Objects;

public class TransportTypeRatePK implements Serializable {

    private Integer idTransportType;
    private Integer idArrivalPoint;

    public TransportTypeRatePK() {
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.idTransportType);
        hash = 97 * hash + Objects.hashCode(this.idArrivalPoint);
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
        final TransportTypeRatePK other = (TransportTypeRatePK) obj;
        if (!Objects.equals(this.idTransportType, other.idTransportType)) {
            return false;
        }
        if (!Objects.equals(this.idArrivalPoint, other.idArrivalPoint)) {
            return false;
        }
        return true;
    }

    public Integer getIdTransportType() {
        return idTransportType;
    }

    public void setIdTransportType(Integer idTransportType) {
        this.idTransportType = idTransportType;
    }

    public Integer getIdArrivalPoint() {
        return idArrivalPoint;
    }

    public void setIdArrivalPoint(Integer idArrivalPoint) {
        this.idArrivalPoint = idArrivalPoint;
    }

}
