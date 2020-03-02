//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.ModelsDAO;

import java.io.Serializable;
import java.util.Objects;

public class VisitorDetailPK implements Serializable {

    private Integer idVisitor;
    private Long idReservation;

    public VisitorDetailPK() {
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.idVisitor);
        hash = 47 * hash + Objects.hashCode(this.idReservation);
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
        final VisitorDetailPK other = (VisitorDetailPK) obj;
        if (!Objects.equals(this.idVisitor, other.idVisitor)) {
            return false;
        }
        if (!Objects.equals(this.idReservation, other.idReservation)) {
            return false;
        }
        return true;
    }

    public Integer getIdVisitor() {
        return idVisitor;
    }

    public VisitorDetailPK setIdVisitor(Integer idVisitor) {
        this.idVisitor = idVisitor;
        return this;
    }

    public Long getIdReservation() {
        return idReservation;
    }

    public VisitorDetailPK setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
        return this;
    }

}
