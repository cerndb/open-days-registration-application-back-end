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

public class PasscodePK implements Serializable {

    private Long idRegistration;
    private LocalDateTime timestampOfCreation;
    private Integer idOperation;

    public PasscodePK(Long idRegistration, LocalDateTime timestampOfCreation, Integer idOperation) {
        this.idRegistration = idRegistration;
        this.timestampOfCreation = timestampOfCreation;
        this.idOperation = idOperation;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.idRegistration);
        hash = 59 * hash + Objects.hashCode(this.timestampOfCreation);
        hash = 59 * hash + Objects.hashCode(this.getIdOperation());
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
        final PasscodePK other = (PasscodePK) obj;
        if (!Objects.equals(this.idRegistration, other.idRegistration)) {
            return false;
        }
        if (!Objects.equals(this.timestampOfCreation, other.timestampOfCreation)) {
            return false;
        }
        if (!Objects.equals(this.idOperation, other.idOperation)) {
            return false;
        }
        return true;
    }

    public PasscodePK() {
    }

    public Long getIdRegistration() {
        return idRegistration;
    }

    public void setIdRegistration(Long idRegistration) {
        this.idRegistration = idRegistration;
    }

    public LocalDateTime getTimestampOfCreation() {
        return timestampOfCreation;
    }

    public void setTimestampOfCreation(LocalDateTime timestampOfCreation) {
        this.timestampOfCreation = timestampOfCreation;
    }

    public Integer getIdOperation() {
        return idOperation;
    }

    public void setIdOperation(Integer idOperation) {
        this.idOperation = idOperation;
    }

}
