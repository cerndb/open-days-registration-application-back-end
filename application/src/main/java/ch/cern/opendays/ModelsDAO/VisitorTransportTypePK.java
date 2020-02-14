//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.ModelsDAO;

import java.io.Serializable;
import java.util.Objects;

public class VisitorTransportTypePK implements Serializable {

    private Long idReservation;
    private Integer idTransportType;

    public VisitorTransportTypePK() {
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.idReservation);
        hash = 97 * hash + Objects.hashCode(this.idTransportType);
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
        final VisitorTransportTypePK other = (VisitorTransportTypePK) obj;
        if (!Objects.equals(this.idReservation, other.idReservation)) {
            return false;
        }
        if (!Objects.equals(this.idTransportType, other.idTransportType)) {
            return false;
        }
        return true;
    }

    public Long getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
    }

    public Integer getIdTransportType() {
        return idTransportType;
    }

    public void setIdTransportType(Integer idTransportType) {
        this.idTransportType = idTransportType;
    }

}
